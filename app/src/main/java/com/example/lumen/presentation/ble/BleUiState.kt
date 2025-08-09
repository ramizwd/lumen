package com.example.lumen.presentation.ble

import com.example.lumen.domain.ble.model.BleDevice

/**
 * Data class for the BLE UI state
 * [scanResults] holds BLE scan result list of type BleDevice
 * [isScanning] Boolean for indicating if BLE is scanning
 */
data class BleUiState(
    val scanResults: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false
)
