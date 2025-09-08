package com.example.lumen.presentation.ble.discovery

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionState

/**
 * Data class for the BLE discovery and connection UI states
 * [scanResults] holds BLE scan result list of type BleDevice
 * [isScanning] Boolean for indicating if BLE is scanning
 * [bluetoothState] Hold current state of Bluetooth
 * [connectionState] indicates the connection state of the GATT client
 * [deviceToConnect] storing device that we want to connect to for retrying connection
 * [showRetryConnection] to show the retry connection snackbar
 * [errorMessage] holds error messages related to GATT operations
 * [infoMessage] holds success or general info messages related to GATT operations
 */
data class DiscoveryUiState(
    val scanResults: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    val bluetoothState: BluetoothState = BluetoothState.UNKNOWN,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val deviceToConnect: BleDevice? = null,
    val showRetryConnection: Boolean = false,
    val showEnableBtDialog: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
)
