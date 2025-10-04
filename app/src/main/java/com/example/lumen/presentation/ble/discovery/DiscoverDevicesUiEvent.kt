package com.example.lumen.presentation.ble.discovery

/**
 * Represents UI events of DiscoverDevicesScreen
 */
sealed interface DiscoverDevicesUiEvent {
    data class ToggleEnableBtDialog(val show: Boolean) : DiscoverDevicesUiEvent
    data class TogglePermissionDialog(val show: Boolean) : DiscoverDevicesUiEvent
    data class ToggleOpenSettingsDialog(val show: Boolean) : DiscoverDevicesUiEvent
}