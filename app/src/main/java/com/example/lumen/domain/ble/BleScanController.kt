package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for controlling BLE scan operations
 */
interface BleScanController {
    val scanResults: StateFlow<List<BleDevice>>
    val isScanning: StateFlow<Boolean>

    suspend fun startScan()
    fun stopScan()
}
