package com.example.lumen.data.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.GATT_FAILURE
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.content.Context
import app.cash.turbine.test
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.GattConstants.CCCD_UUID
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.GET_INFO_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.utils.hasPermission
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

/**
 * Unit tests for [BleGattControllerImpl]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BleGattControllerImplTest {

    private val device = BleDevice("Test", "00:11:22:33:44:55")
    private val ledStateBytes = ByteArray(12)
    private val commandBytes = ByteArray(4)
    private val callbackSlot = slot<BluetoothGattCallback>()

    private lateinit var context: Context
    private lateinit var btAdapter: BluetoothAdapter
    private lateinit var remoteDevice: BluetoothDevice
    private lateinit var gatt: BluetoothGatt
    private lateinit var service: BluetoothGattService
    private lateinit var characteristic: BluetoothGattCharacteristic
    lateinit var descriptor: BluetoothGattDescriptor

    private lateinit var controller: BleGattControllerImpl

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic("com.example.lumen.utils.PermissionsKt")

        context = mockk()
        btAdapter = mockk()
        remoteDevice = mockk()
        service = mockk()
        gatt = mockk(relaxed = true)
        characteristic = mockk(relaxed = true)
        descriptor = mockk(relaxed = true)

        every { context.hasPermission(any()) } returns true
        every { btAdapter.isEnabled } returns true
        every { btAdapter.getRemoteDevice(device.address) } returns remoteDevice
        every {
            remoteDevice.connectGatt(
                any(),
                any(),
                capture(callbackSlot)
            )
        } returns gatt

        every { gatt.getService(any()) } returns service
        every { service.getCharacteristic(any()) } returns characteristic
        every { characteristic.uuid } returns CHARACTERISTIC_UUID

        controller = BleGattControllerImpl(context, btAdapter)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `full connection flow should reach STATE_LOADED_AND_CONNECTED`() = runTest {
        controller.connectionState.test {
            assertEquals(ConnectionState.DISCONNECTED, awaitItem())

            controller.connect(device)

            assertEquals(ConnectionState.CONNECTING, awaitItem())

            callbackSlot.captured.onConnectionStateChange(
                gatt,
                GATT_SUCCESS,
                STATE_CONNECTED
            )

            assertEquals(ConnectionState.LOADING_DEVICE_STATE, awaitItem())

            callbackSlot.captured.onCharacteristicChanged(
                gatt,
                characteristic,
                ledStateBytes
            )

            assertEquals(ConnectionState.STATE_LOADED_AND_CONNECTED, awaitItem())

            verify(exactly = 1) {
                remoteDevice.connectGatt(any(), any(), any())
            }
            verify(exactly = 1) { gatt.discoverServices() }
        }
    }

    @Test
    fun `connect should set selectedDevice`() = runTest {
        controller.connect(device)

        controller.selectedDevice.test {
            assertEquals(device, awaitItem())
        }
    }

    @Test
    fun `connect should emit error when BLUETOOTH_CONNECT perms missing`() = runTest {
        every { context.hasPermission(any()) } returns false

        controller.connect(device)

        controller.connectionEvents.test {
            assertEquals(
            ConnectionResult.Error("Nearby devices permission missing!"),
            awaitItem()
            )
        }

        verify(exactly = 0) {
            remoteDevice.connectGatt(any(), any(), any())
        }
    }

    @Test
    fun `connect should emit error when BT is disabled`() = runTest {
        every { btAdapter.isEnabled } returns false

        controller.connectionEvents.test {
            controller.connect(device)

            assertEquals(
                ConnectionResult.Error("Bluetooth is not enabled"),
                awaitItem()
            )
        }

        verify(exactly = 0) {
            remoteDevice.connectGatt(any(), any(), any())
        }
    }

    @Test
    fun `connect should emit error when connectGatt throws an exception`() = runTest {
        every {
            remoteDevice.connectGatt(
                any(),
                any(),
                capture(callbackSlot)
            )
        } throws IllegalArgumentException("Failed to connect")

        controller.connect(device)

        controller.connectionEvents.test {
            assertEquals(
                ConnectionResult.Error("Device not found"),
                awaitItem()
            )
        }

        assertEquals(
            ConnectionState.DISCONNECTED,
            controller.connectionState.value
        )
    }

    @Test
    fun `onConnectionStateChange should trigger retry on failure after 500ms`() = runTest {
        val controllerWithScope = BleGattControllerImpl(
            context,
            btAdapter,
            backgroundScope
        )

        controllerWithScope.connectionState.test {
            assertEquals(ConnectionState.DISCONNECTED, awaitItem())

            controllerWithScope.connect(device)

            assertEquals(ConnectionState.CONNECTING, awaitItem())

            callbackSlot.captured.onConnectionStateChange(
                gatt,
                GATT_FAILURE,
                STATE_DISCONNECTED
            )

            assertEquals(ConnectionState.RETRYING, awaitItem())

            advanceTimeBy(200)

            expectNoEvents()

            advanceTimeBy(400)

            verify(exactly = 2) {
                remoteDevice.connectGatt(any(), any(), any())
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConnectionStateChange should emit error when retry attempts is over max limit`() = runTest {
        val controllerWithScope = BleGattControllerImpl(
            context,
            btAdapter,
            backgroundScope
        )

        controllerWithScope.connect(device)

        repeat(6) {
            callbackSlot.captured.onConnectionStateChange(
                gatt,
                GATT_FAILURE,
                STATE_DISCONNECTED
            )

            advanceTimeBy(600)
        }

        controllerWithScope.connectionEvents.test {
            assertEquals(
                ConnectionResult.ConnectionFailed("Connection failed"),
                awaitItem()
            )
        }

        assertEquals(
            ConnectionState.DISCONNECTED,
            controllerWithScope.connectionState.value
        )

        verify(exactly = 6) {
            remoteDevice.connectGatt(any(), any(), any())
        }
    }

    @Test
    fun `disconnect should disconnect connected device`() = runTest {
        controller.connect(device)

        callbackSlot.captured.onConnectionStateChange(
            gatt,
            GATT_SUCCESS,
            STATE_CONNECTED
        )
        callbackSlot.captured.onCharacteristicChanged(gatt, characteristic, ledStateBytes)

        controller.connectionState.test {
            assertEquals(
                ConnectionState.STATE_LOADED_AND_CONNECTED,
                awaitItem()
            )
        }

        controller.disconnect()

        verify(exactly = 1) { gatt.disconnect() }
    }

    @Test
    fun `disconnect should cancel ongoing connection attempt`() = runTest {
        controller.connect(device)

        controller.disconnect()

        controller.connectionEvents.test {
            assertEquals(ConnectionResult.ConnectionCanceled, awaitItem())
        }

        verify(exactly = 1) { gatt.close() }
        verify(exactly = 0) { gatt.disconnect() }
    }

    @Test
    fun `disconnect should cancel ongoing retry connection attempt`() = runTest {
        val controllerWithScope = BleGattControllerImpl(
            context,
            btAdapter,
            backgroundScope
        )

        controllerWithScope.connect(device)

        callbackSlot.captured.onConnectionStateChange(
            gatt,
            GATT_FAILURE,
            STATE_DISCONNECTED
        )

        assertEquals(
            ConnectionState.RETRYING,
            controllerWithScope.connectionState.value
        )

        controllerWithScope.connectionEvents.test {
            controllerWithScope.disconnect()

            assertEquals(ConnectionResult.ConnectionCanceled, awaitItem())
        }

        verify(exactly = 1) { gatt.close() }

        advanceTimeBy(600) // advance time by more than retry delay

        verify(exactly = 1) {
            remoteDevice.connectGatt(any(), any(), any())
        }
    }

    @Test
    fun `disconnect should cancel ongoing loading state attempt and disconnect`() = runTest {
        val controllerWithScope = BleGattControllerImpl(
            context,
            btAdapter,
            backgroundScope
        )

        controllerWithScope.connect(device)

        callbackSlot.captured.onConnectionStateChange(
            gatt,
            GATT_SUCCESS,
            STATE_CONNECTED
        )

        assertEquals(
            ConnectionState.LOADING_DEVICE_STATE,
            controllerWithScope.connectionState.value
        )

        controllerWithScope.connectionEvents.test {
            awaitItem()

            controllerWithScope.disconnect()

            assertEquals(ConnectionResult.ConnectionCanceled, awaitItem())
        }

        verify(exactly = 1) { gatt.close() }

        advanceTimeBy(600)

        verify(exactly = 1) {
            remoteDevice.connectGatt(any(), any(), any())
        }
    }

    @Test
    fun `disconnect should emit error when BLUETOOTH_CONNECT perms missing`() = runTest {
        every { context.hasPermission(any()) } returns false

        controller.disconnect()

        controller.connectionEvents.test {
            assertEquals(
                ConnectionResult.Error("Nearby devices permission missing!"),
                awaitItem()
            )
        }

        verify(exactly = 0) { gatt.disconnect() }
    }

    @Test
    fun `writeCharacteristic should call GATT writeCharacteristic`() = runTest {
        controller.connect(device)

        controller.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            commandBytes
        )

        verify(exactly = 1) {
            gatt.writeCharacteristic(characteristic)
            characteristic.value = commandBytes
        }
    }

    @Test
    fun `writeCharacteristic should emit error when BLUETOOTH_CONNECT perms missing`() = runTest {
        every { context.hasPermission(any()) } returns  false

        controller.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            commandBytes
        )

        controller.connectionEvents.test {
            assertEquals(
                ConnectionResult.Error("Nearby devices permission missing!"),
                awaitItem()
            )
        }

        verify(exactly = 0) {
            gatt.writeCharacteristic(characteristic)
        }
    }

    @Test
    fun `writeCharacteristic should emit error when BT stack throws an exception`() = runTest {
        controller.connect(device)

        every { gatt.writeCharacteristic(any()) } throws RuntimeException("BT error")
        every {
            gatt.writeCharacteristic(any(),any(),any())
        } throws RuntimeException("BT error")

        controller.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            commandBytes
        )

        controller.connectionEvents.test {
            assertEquals(
                ConnectionResult.Error("Error sending command"),
                awaitItem()
            )
        }
    }

    @Test
    fun `handle invalid device flow and ensure state is reset for subsequent connections`() = runTest {
        // First connection attempt with invalid service

        every { gatt.getService(SERVICE_UUID) } returns null
        controller.connect(device)

        callbackSlot.captured.onConnectionStateChange(
            gatt,
            GATT_SUCCESS,
            STATE_CONNECTED
        )
        callbackSlot.captured.onServicesDiscovered(gatt, GATT_SUCCESS)

        assertEquals(
            ConnectionState.INVALID_DEVICE,
            controller.connectionState.value
        )

        verify(exactly = 1) { gatt.disconnect() }

        controller.connectionEvents.test {
            callbackSlot.captured.onConnectionStateChange(
                gatt,
                GATT_SUCCESS,
                STATE_DISCONNECTED
            )

            assertEquals(
                ConnectionResult.InvalidDevice,
                expectMostRecentItem()
            )
        }

        verify(exactly = 1) { gatt.close() }

        // Second connection attempt with valid service

        every { gatt.getService(SERVICE_UUID) } returns service
        controller.connect(device)

        controller.connectionEvents.test {
            callbackSlot.captured.onConnectionStateChange(
                gatt,
                GATT_SUCCESS,
                STATE_DISCONNECTED
            )

            val event = expectMostRecentItem()
            assertEquals(ConnectionResult.Disconnected, event)
            assertNotEquals(ConnectionResult.InvalidDevice, event)
        }
    }

    @Test
    fun `onServicesDiscovered should close connection on GATT failure`() = runTest {
        controller.connect(device)

        callbackSlot.captured.onConnectionStateChange(
            gatt,
            GATT_SUCCESS,
            STATE_CONNECTED
        )
        callbackSlot.captured.onServicesDiscovered(gatt, GATT_FAILURE)

        verify(exactly = 1) { gatt.close() }

        controller.connectionState.test {
            assertEquals(
                ConnectionState.DISCONNECTED,
                awaitItem()
            )
        }
    }

    @Test
    fun `onCharacteristicChanged with insufficient bytes should not update state`() = runTest {
        val shortBytes = ByteArray(3)

        controller.connect(device)

        callbackSlot.captured.onCharacteristicChanged(gatt, characteristic, shortBytes)

        assertNull(controller.ledControllerState.value)
        assertNotEquals(
            ConnectionState.STATE_LOADED_AND_CONNECTED,
            controller.connectionState.value
        )
    }

    @Test
    fun `onDescriptorWrite should request controller state when CCCD is written successfully`() = runTest {
        every { descriptor.uuid } returns CCCD_UUID
        every { descriptor.characteristic } returns characteristic

        controller.connect(device)

        callbackSlot.captured.onDescriptorWrite(
            gatt,
            descriptor,
            GATT_SUCCESS
        )

        verify(exactly = 1) {
            characteristic.value = GET_INFO_COMMAND
            gatt.writeCharacteristic(characteristic)
        }
    }

    @Test
    fun `onDescriptorWrite should not request state if status is failure`() = runTest {
        controller.connect(device)

        callbackSlot.captured.onDescriptorWrite(
            gatt,
            descriptor,
            GATT_FAILURE
        )

        verify(exactly = 0) {
            gatt.writeCharacteristic(any())
            gatt.writeCharacteristic(any(), any(), any())
        }
    }
}