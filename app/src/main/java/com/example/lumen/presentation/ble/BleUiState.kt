package com.example.lumen.presentation.ble

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState

/**
 * Data class for the BLE UI state
 * [scanResults] holds BLE scan result list of type BleDevice
 * [isScanning] Boolean for indicating if BLE is scanning
 * [connectionState] indicates the connection state of the GATT client
 */
data class BleUiState(
    val scanResults: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val connectedDevice: BleDevice? = null,
)
