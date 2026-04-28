package com.example.lumen.data.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import app.cash.turbine.test
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.GattConstants
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
    private lateinit var characteristic: BluetoothGattCharacteristic

    private lateinit var controller: BleGattControllerImpl

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic("com.example.lumen.utils.PermissionsKt")

        context = mockk()
        btAdapter = mockk()
        remoteDevice = mockk()
        gatt = mockk(relaxed = true)
        characteristic = mockk(relaxed = true)

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

        every { characteristic.uuid } returns GattConstants.CHARACTERISTIC_UUID

        val mockService: BluetoothGattService = mockk()
        every { gatt.getService(any()) } returns mockService
        every { mockService.getCharacteristic(any()) } returns characteristic

        controller = BleGattControllerImpl(context, btAdapter)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `connect should update state and call appropriate methods`() = runTest {
        controller.connectionState.test {
            assertEquals(ConnectionState.DISCONNECTED, awaitItem())

            controller.connect(device)

            assertEquals(ConnectionState.CONNECTING, awaitItem())

            callbackSlot.captured.onConnectionStateChange(
                gatt,
                BluetoothGatt.GATT_SUCCESS,
                BluetoothProfile.STATE_CONNECTED
            )

            assertEquals(ConnectionState.LOADING_DEVICE_STATE, awaitItem())

            verify(exactly = 1) {
                remoteDevice.connectGatt(any(), false, any())
            }
            verify(exactly = 1) { gatt.discoverServices() }
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

            assertEquals(
                ConnectionState.DISCONNECTED,
                controller.connectionState.value
            )
        }
    }

    @Test
    fun `connect should trigger retry on failure after 500ms`() = runTest {
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
                BluetoothGatt.GATT_FAILURE,
                BluetoothProfile.STATE_DISCONNECTED
            )

            assertEquals(ConnectionState.RETRYING, awaitItem())

            advanceTimeBy(200)

            expectNoEvents()

            advanceTimeBy(400)

            verify(exactly = 2) {
                remoteDevice.connectGatt(any(), false, any())
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `disconnect should disconnect connected device`() = runTest {
        controller.connect(device)

        callbackSlot.captured.onConnectionStateChange(
            gatt,
            BluetoothGatt.GATT_SUCCESS,
            BluetoothProfile.STATE_CONNECTED
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
    fun `disconnect should emit error when BLUETOOTH_CONNECT perms missing`() = runTest {
        every { context.hasPermission(any()) } returns false

        controller.disconnect()

        controller.connectionEvents.test {
            assertEquals(
                ConnectionResult.Error("Nearby devices permission missing!"),
                awaitItem()
            )
        }
    }

    @Test
    fun `writeCharacteristic should call gatt writeCharacteristic`() = runTest {
        controller.connect(device)

        controller.writeCharacteristic(
            GattConstants.SERVICE_UUID,
            GattConstants.CHARACTERISTIC_UUID,
            commandBytes
        )

        verify(exactly = 1) {
            gatt.writeCharacteristic(characteristic)
        }
        verify { characteristic.value = commandBytes }
    }

    @Test
    fun `writeCharacteristic should emit error when BLUETOOTH_CONNECT perms missing`() = runTest {
        every { context.hasPermission(any()) } returns  false

        controller.writeCharacteristic(
            GattConstants.SERVICE_UUID,
            GattConstants.CHARACTERISTIC_UUID,
            commandBytes
        )

        controller.connectionEvents.test {
            assertEquals(
                ConnectionResult.Error("Nearby devices permission missing!"),
                awaitItem()
            )
        }
    }

    @Test
    fun `onConnectionStateChange should handle invalid device and disconnect`() = runTest {
        every { gatt.getService(GattConstants.SERVICE_UUID) } returns null

        controller.connect(device)

        callbackSlot.captured.onConnectionStateChange(
            gatt,
            BluetoothGatt.GATT_SUCCESS,
            BluetoothProfile.STATE_CONNECTED
        )
        callbackSlot.captured.onServicesDiscovered(gatt, BluetoothGatt.GATT_SUCCESS)

        assertEquals(
            ConnectionState.INVALID_DEVICE,
            controller.connectionState.value
        )

        verify(exactly = 1) { gatt.disconnect() }

        controller.connectionEvents.test {
            awaitItem()

            callbackSlot.captured.onConnectionStateChange(
                gatt,
                BluetoothGatt.GATT_SUCCESS,
                BluetoothProfile.STATE_DISCONNECTED
            )

            assertEquals(
                ConnectionResult.InvalidDevice,
                awaitItem()
            )
        }
    }
}