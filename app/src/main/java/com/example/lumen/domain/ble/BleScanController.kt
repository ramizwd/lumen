package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ScanState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for controlling BLE scan operations
 */
interface BleScanController {
    val scanResults: StateFlow<List<BleDevice>>
    val scanState: StateFlow<ScanState>
    val errors: SharedFlow<String>

    suspend fun startScan()
    fun stopScan()
}
