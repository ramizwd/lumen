package com.example.lumen.presentation.ble.discovery

import com.example.lumen.domain.ble.model.BluetoothPermissionStatus
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.DeviceListType
import com.example.lumen.domain.ble.model.ScanState
import com.example.lumen.presentation.common.model.DeviceContent

/**
 * Data class for the BLE discovery and connection UI states
 * [scanResults] holds BLE scan result list of type BleDevice
 * [scanState] Indicates scanning state
 * [emptyScanResultTxt] Holds the text that represent empty scan result
 * [bluetoothState] Hold current state of Bluetooth
 * [selectedListType] Holds the current selected type of device list
 * [btPermissionStatus] indicates the permission status of Bluetooth
 * [isBtDisabled] Indicates if Bluetooth off or on
 * [errorMessage] holds error messages related to GATT operations
 * [infoMessage] holds success or general info messages related to GATT operations
 * [showEnableBtDialog] indicates whether to show the enable Bluetooth dialog
 * [showPermissionDialog] indicates whether to show the permission rationale dialog
 * [showOpenSettingsDialog] indicates whether to show the grant permission through settings dialog
 */
data class DiscoveryUiState(
    val scanResults: List<DeviceContent> = emptyList(),
    val scanState: ScanState = ScanState.SCAN_PAUSED,
    val emptyScanResultTxt: String? = null,
    val bluetoothState: BluetoothState = BluetoothState.UNKNOWN,
    val selectedListType: DeviceListType = DeviceListType.ALL_DEVICES,
    val btPermissionStatus: BluetoothPermissionStatus = BluetoothPermissionStatus.UNKNOWN,
    val isBtDisabled: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val showEnableBtDialog: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val showOpenSettingsDialog: Boolean = false,
)
