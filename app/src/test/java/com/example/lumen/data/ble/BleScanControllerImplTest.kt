package com.example.lumen.data.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import app.cash.turbine.test
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ScanState
import com.example.lumen.utils.hasPermission
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [BleScanControllerImpl]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BleScanControllerImplTest {

    private val deviceAddress = "00:11:22:33:44:55"
    private val deviceName = "Test"
    private val callbackSlot = slot<ScanCallback>()

    private lateinit var mockBtDevice: BluetoothDevice
    private lateinit var mockScanResult: ScanResult

    private lateinit var context: Context
    private lateinit var btAdapter: BluetoothAdapter
    private lateinit var bleScanner: BluetoothLeScanner

    private lateinit var controller: BleScanControllerImpl

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic("com.example.lumen.utils.PermissionsKt")

        context = mockk()
        btAdapter = mockk()
        bleScanner = mockk()

        val mockSettings = mockk<ScanSettings>()
        val mockBuilder = mockk<ScanSettings.Builder>(relaxed = true)

        mockkConstructor(ScanSettings.Builder::class)
        every { anyConstructed<ScanSettings.Builder>().setScanMode(any()) } returns mockBuilder
        every { anyConstructed<ScanSettings.Builder>().setReportDelay(any()) } returns mockBuilder
        every { anyConstructed<ScanSettings.Builder>().build() } returns mockSettings

        every { context.hasPermission(any()) } returns true
        every { btAdapter.isEnabled } returns true
        every { btAdapter.bluetoothLeScanner } returns bleScanner

        every {
            bleScanner.startScan(
                any(),
                any(),
                capture(callbackSlot))
        } just Runs
        every { bleScanner.stopScan(any<ScanCallback>()) } just Runs

        mockBtDevice = mockk(relaxed = true) {
            every { address } returns deviceAddress
            every { name } returns deviceName
        }

        mockScanResult = mockk {
            every { device } returns mockBtDevice
            every { isConnectable } returns true
        }

        controller = BleScanControllerImpl(
            context,
            btAdapter,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `startScan updates state to SCANNING and calls scanner`() = runTest {
        controller.scanState.test {
            assertEquals(ScanState.SCAN_PAUSED, awaitItem())

            controller.startScan()

            assertEquals(ScanState.SCANNING, awaitItem())

            verify(exactly = 1) {
                bleScanner.startScan(
                    any(),
                    any(),
                    any<ScanCallback>()
                )
            }
        }
    }

    @Test
    fun `startScan should emit error when BLUETOOTH_SCAN permission is missing`() = runTest {
        every { context.hasPermission(any()) } returns false

        controller.errors.test {
            controller.startScan()

            assertEquals("Nearby devices permission missing!" ,awaitItem())
        }
    }

    @Test
    fun `startScan should emit error when BT is disabled`() = runTest {
        every { btAdapter.isEnabled } returns false

        controller.errors.test {
            controller.startScan()

            assertEquals("Bluetooth is not enabled", awaitItem())
        }
    }

    @Test
    fun `startScan should emit error when BLE is not available`() = runTest {
        every { btAdapter.bluetoothLeScanner } returns null

        controller.errors.test {
            controller.startScan()

            assertEquals("BLE scanning not supported", awaitItem())
        }
    }

    @Test
    fun `startScan resets scan if already scanning`() = runTest {
        controller.startScan()

        assertEquals(ScanState.SCANNING, controller.scanState.value)

        controller.startScan()

        verify(exactly = 1) { bleScanner.stopScan(any<ScanCallback>()) }
        verify(exactly = 2) {
            bleScanner.startScan(
                any(),
                any(),
                any<ScanCallback>()
            )
        }
    }

    @Test
    fun `startScan resets scan result state when a new scan starts`() = runTest {
        controller.startScan()

        callbackSlot.captured.onScanResult(
            ScanSettings.CALLBACK_TYPE_ALL_MATCHES, mockScanResult
        )

        controller.scanResults.test {
            assertTrue(awaitItem().isNotEmpty())

            controller.startScan()

            assertEquals(emptyMap<String, BleDevice>(), awaitItem())
        }
    }

    @Test
    fun `startScan should emit error when hardware startScan throws exception`() = runTest {
        every {
            bleScanner.startScan(
                any(),
                any(),
                any<ScanCallback>()
            )
        } throws RuntimeException("Hardware failure")

        controller.scanState.test {
            assertEquals(ScanState.SCAN_PAUSED, awaitItem())

            controller.errors.test {
                controller.startScan()

                assertEquals("Scan failed", awaitItem())
            }

            assertEquals(ScanState.SCAN_PAUSED, controller.scanState.value)
            expectNoEvents()
        }
    }

    @Test
    fun `startScan stops scan if scan period is over 30 seconds`() = runTest {
        val controllerWithScope = BleScanControllerImpl(
            context,
            btAdapter,
            backgroundScope
        )

        controllerWithScope.scanState.test {
            assertEquals(ScanState.SCAN_PAUSED, awaitItem())

            controllerWithScope.startScan()

            assertEquals(ScanState.SCANNING, awaitItem())

            advanceTimeBy(29_000)
            runCurrent()

            expectNoEvents()

            advanceTimeBy(2_000)
            runCurrent()

            assertEquals(ScanState.SCAN_AUTO_PAUSED, expectMostRecentItem())

            verify(exactly = 1) { bleScanner.stopScan(any<ScanCallback>()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ScanCallback onScanResult ignores non-connectable devices`() = runTest {
        every { mockScanResult.isConnectable } returns false

        controller.startScan()

        controller.scanResults.test {
            assertEquals(emptyMap<String, BleDevice>(), awaitItem())

            callbackSlot.captured.onScanResult(
                ScanSettings.CALLBACK_TYPE_ALL_MATCHES, mockScanResult
            )

            expectNoEvents()

            assertEquals(0, controller.scanResults.value.size)
        }
    }

    @Test
    fun `ScanCallback onScanResult updates scanResults map with found devices`() = runTest {
        controller.startScan()

        controller.scanResults.test {
            assertEquals(emptyMap<String, BleDevice>(), awaitItem())

            callbackSlot.captured.onScanResult(
                ScanSettings.CALLBACK_TYPE_ALL_MATCHES, mockScanResult
            )

            val updatedMap = awaitItem()

            assertTrue(updatedMap.containsKey(deviceAddress))
            assertEquals(deviceAddress, updatedMap[deviceAddress]?.address)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ScanCallback onScanResult updates existing device with new data`() = runTest {
        val updatedName =  "Updated name"

        val updatedDevice = mockk<BluetoothDevice>(relaxed = true) {
            every { address } returns deviceAddress
            every { name } returns updatedName
        }

        val updatedScanResult = mockk<ScanResult> {
            every { device } returns updatedDevice
            every { isConnectable } returns true
        }

        controller.startScan()

        controller.scanResults.test {
            awaitItem()

            callbackSlot.captured.onScanResult(
                ScanSettings.CALLBACK_TYPE_ALL_MATCHES, mockScanResult
            )

            assertEquals(deviceName, awaitItem()[deviceAddress]?.name)

            callbackSlot.captured.onScanResult(
                ScanSettings.CALLBACK_TYPE_ALL_MATCHES, updatedScanResult
            )

            val updatedMap = awaitItem()

            assertEquals(1, updatedMap.size)
            assertEquals(updatedName, updatedMap[deviceAddress]?.name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `scanResult state does NOT duplicate found devices`() = runTest {
        controller.startScan()

        controller.scanResults.test {
            assertEquals(emptyMap<String, BleDevice>(), awaitItem())

            callbackSlot.captured.onScanResult(
                ScanSettings.CALLBACK_TYPE_ALL_MATCHES, mockScanResult
            )

            callbackSlot.captured.onScanResult(
                ScanSettings.CALLBACK_TYPE_ALL_MATCHES, mockScanResult
            )

            val updatedResult = expectMostRecentItem()

            assertEquals(1, updatedResult.size)
            assertTrue(updatedResult.containsKey(deviceAddress))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stopScan stops ongoing scan and updates state to SCAN_PAUSED`() = runTest {
        controller.startScan()

        controller.scanState.test {
            assertEquals(ScanState.SCANNING ,awaitItem())

            controller.stopScan()

            assertEquals(ScanState.SCAN_PAUSED ,awaitItem())

            verify(exactly = 1) { bleScanner.stopScan(any<ScanCallback>()) }
        }
    }

    @Test
    fun `stopScan should emit error when BLUETOOTH_SCAN permission is missing`() = runTest {
        every { context.hasPermission(any()) } returns false

        controller.errors.test {
            controller.stopScan()

            assertEquals("Nearby devices permission missing!" ,awaitItem())
        }
    }

    @Test
    fun `stopScan should emit error when BT is disabled`() = runTest {
        every { btAdapter.isEnabled } returns false

        controller.startScan()

        controller.errors.test {
            controller.stopScan()

            assertEquals("Bluetooth is not enabled", awaitItem())
        }
    }

    @Test
    fun `stopScan should emit error when BLE is not available`() = runTest {
        every { btAdapter.bluetoothLeScanner } returns null

        controller.startScan()

        controller.errors.test {
            controller.stopScan()

            assertEquals("BLE scanning not supported", awaitItem())
        }
    }

    @Test
    fun `stopScan should not call hardware scanner when there are no ongoing scans`() = runTest {
        assertEquals(ScanState.SCAN_PAUSED, controller.scanState.value)

        controller.stopScan()

        verify(exactly = 0) { bleScanner.stopScan(any<ScanCallback>()) }
    }

    @Test
    fun `stopScan should emit error when hardware stopScan throws exception`() = runTest {
        every {
            bleScanner.stopScan(any<ScanCallback>())
        } throws RuntimeException("Hardware failure")

        controller.startScan()

        controller.scanState.test {
            assertEquals(ScanState.SCANNING, awaitItem())

            controller.errors.test {
                controller.stopScan()

                assertEquals("Pausing scan failed", awaitItem())
            }

            assertEquals(ScanState.SCAN_PAUSED, awaitItem())
            expectNoEvents()
        }
    }
}