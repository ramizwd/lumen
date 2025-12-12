package com.example.lumen.presentation.ble.led_control

/**
 * UI events of LedControlScreen
 */
sealed interface LedControlUiEvent {
    data class ToggleRenameDeviceDialog(val show: Boolean): LedControlUiEvent
}