package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for controlling BLE operations
 */
interface BleController {
    val scanResults: StateFlow<List<BleDevice>>
    val isScanning: StateFlow<Boolean>

    fun startScan()
    fun stopScan()
}
