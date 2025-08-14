package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for controlling BLE GATT operations
 */
interface BleGattController {
    val connectionState: StateFlow<ConnectionState>

    fun connect(address: String)
    fun disconnect()
}
