package com.example.lumen.presentation.ble.discovery

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.BluetoothPermissionStatus
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ScanState

/**
 * Data class for the BLE discovery and connection UI states
 * [scanResults] holds BLE scan result list of type BleDevice
 * [bluetoothState] Hold current state of Bluetooth
 * [btPermissionStatus] indicates the permission status of Bluetooth
 * [deviceToConnect] storing device that we want to connect to for retrying connection
 * [isBtDisabled] Indicates if Bluetooth off or on
 * [errorMessage] holds error messages related to GATT operations
 * [infoMessage] holds success or general info messages related to GATT operations
 * [showEnableBtDialog] indicates whether to show the enable Bluetooth dialog
 * [showPermissionDialog] indicates whether to show the permission rationale dialog
 * [showOpenSettingsDialog] indicates whether to show the grant permission through settings dialog
 */
data class DiscoveryUiState(
    val scanResults: List<BleDevice> = emptyList(),
    val scanState: ScanState = ScanState.SCAN_PAUSED,
    val bluetoothState: BluetoothState = BluetoothState.UNKNOWN,
    val emptyScanResultTxt: String? = null,
    val btPermissionStatus: BluetoothPermissionStatus = BluetoothPermissionStatus.UNKNOWN,
    val deviceToConnect: BleDevice? = null,
    val isBtDisabled: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val showEnableBtDialog: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val showOpenSettingsDialog: Boolean = false,
)
