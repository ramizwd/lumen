package com.example.lumen.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.lumen.data.mapper.toBleDevice
import com.example.lumen.domain.ble.BleController
import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Class that implements [BleController] interface.
 * Handles the low-level Android Bluetooth API interactions
 */
@SuppressLint("MissingPermission")
class BleControllerImpl(
    private val context: Context
): BleController {

    companion object BleControllerImpl {
        private const val LOG_TAG = "BleControllerImpl"
        private const val REPOT_DELAY: Long = 0
        private const val SCAN_PERIOD_MILLIS: Long = 30_000 // Scan for 30 seconds
    }

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val bluetoothLeScanner by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private var scanJob: Job? = null
    private val bleScanScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _scanResults = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scanResults: StateFlow<List<BleDevice>>
        get() = _scanResults.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean>
        get() = _isScanning.asStateFlow()

    override suspend fun startScan() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            Log.d(LOG_TAG, "BLUETOOTH_SCAN permission missing!")
            return
        }

        if (bluetoothLeScanner == null) {
            Log.d(LOG_TAG, "BLE Scanner not available.")
            return
        }

        if (_isScanning.value) {
            stopScan()
        }

        // Clear prev results
        _scanResults.value = emptyList()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(REPOT_DELAY)
            .build()

        try {
            bluetoothLeScanner?.startScan(null, settings, leScanCallBack)
            _isScanning.value = true
            Log.d(LOG_TAG, "BLE Scan started...")

            // Start a coroutine to stop scanning after a period
            scanJob = bleScanScope.launch {
                delay(SCAN_PERIOD_MILLIS)
                stopScan()
                Log.d(LOG_TAG, "Ble scan stopped automatically")
            }
        } catch (e: SecurityException) {
            Log.d(LOG_TAG, "SecurityException during scan start: ${e.message}")
            _isScanning.value = false
        } catch (e: Exception) {
            Log.d(LOG_TAG, "Exception during scan start: ${e.message}")
            _isScanning.value = false
        }
    }

    override fun stopScan() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            Log.d(LOG_TAG, "BLUETOOTH_SCAN permission missing!")
            return
        }

        if (bluetoothLeScanner == null) {
            Log.d(LOG_TAG, "BLE Scanner not available.")
            return
        }

        try {
            bluetoothLeScanner?.stopScan(leScanCallBack)
            _isScanning.value = false
            scanJob?.cancel()
            scanJob = null
            Log.d(LOG_TAG, "BLE Scan stopped...")
        } catch (e: SecurityException) {
            Log.d(LOG_TAG, "SecurityException during scan start: ${e.message}")
        } catch (e: Exception) {
            Log.d(LOG_TAG, "Exception during scan start: ${e.message}")
        }
    }

    // Anonymous object for receiving and processing the scan results
    private val leScanCallBack: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            // Convert the results to BleDevice model and update the result with new device
            _scanResults.update { devices ->
                val newDevice = result.toBleDevice()
                if (newDevice in devices) devices else devices + newDevice
            }
        }

        // Used if setReportDelay() is set to a non-zero value
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.d(LOG_TAG, "onBatchScanResults: ${results?.size} results")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(LOG_TAG, "BLE Scan failed with error: $errorCode")
            _isScanning.value = false
            scanJob?.cancel()
            scanJob = null
        }
    }

    // Helper function to check if permission is granted
    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}