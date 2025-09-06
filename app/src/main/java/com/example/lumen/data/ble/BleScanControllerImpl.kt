package com.example.lumen.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.example.lumen.data.mapper.toBleDevice
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.utils.hasPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Class that implements [BleScanController] interface.
 * Handles the low-level Android Bluetooth API scan interactions
 */
@SuppressLint("MissingPermission")
class BleScanControllerImpl(
    private val context: Context
): BleScanController {

    companion object {
        private const val LOG_TAG = "BleControllerImpl"

        private const val REPOT_DELAY_MILLIS: Long = 0
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

    private val _errors = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    override suspend fun startScan() {
        if (!context.hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            Timber.tag(LOG_TAG).e("BLUETOOTH_SCAN permission missing!")
            return
        }

        if (_isScanning.value) {
            stopScan()
        }

        // Clear prev results
        _scanResults.value = emptyList()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(REPOT_DELAY_MILLIS)
            .build()

        bluetoothLeScanner?.let { scanner ->
            try {
                scanner.startScan(null, settings, leScanCallback)
                _isScanning.value = true
                Timber.tag(LOG_TAG).d("BLE Scan started...")

                // Start a coroutine to stop scanning after a period
                scanJob = bleScanScope.launch {
                    delay(SCAN_PERIOD_MILLIS)
                    stopScan()
                    Timber.tag(LOG_TAG).i("Ble scan stopped automatically")
                }
            } catch (e: Exception) {
                Timber.tag(LOG_TAG).e(e,"Exception during scan start")
                _errors.tryEmit("Scan failed")
                _isScanning.value = false
            }
        } ?: run {
            Timber.tag(LOG_TAG).e("BLE Scanner not initialized - startScan()")
            _errors.tryEmit("Scan failed")
        }
    }

    override fun stopScan() {
        if (!context.hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            Timber.tag(LOG_TAG).e("BLUETOOTH_SCAN permission missing!")
            return
        }

        if (!_isScanning.value) {
            return
        }

        bluetoothLeScanner?.let { scanner ->
            try {
                scanner.stopScan(leScanCallback)
                _isScanning.value = false
                scanJob?.cancel()
                scanJob = null
                Timber.tag(LOG_TAG).i("BLE Scan stopped...")
            } catch (e: Exception) {
                Timber.tag(LOG_TAG).e(e,"Exception during scan stop")
                _errors.tryEmit("Stopping scan failed")
            }
        } ?: run {
            Timber.tag(LOG_TAG).e("BLE Scanner not initialized - stopScan()")
            _errors.tryEmit("Stopping scan failed")
        }
    }

    // Anonymous object for receiving and processing the scan results
    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            if (result.isConnectable) {
                // Convert the results to BleDevice model and update the result with new device
                _scanResults.update { devices ->
                    val newDevice = result.toBleDevice()
                    if (newDevice in devices) devices else devices + newDevice
                }
            }
        }

        // Used if setReportDelay() is set to a non-zero value
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Timber.tag(LOG_TAG).d("onBatchScanResults: ${results?.size} results")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.tag(LOG_TAG).e("BLE Scan failed with error: $errorCode")
            _errors.tryEmit("Stopping scan failed")
            _isScanning.value = false
            scanJob?.cancel()
            scanJob = null
        }
    }
}