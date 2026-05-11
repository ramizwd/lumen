package com.example.lumen.presentation.ble.ledcontrol

/**
 * UI events of LedControlScreen
 */
sealed interface LedControlUiEvent {
    data class ToggleRenameDeviceDialog(
        val show: Boolean,
    ) : LedControlUiEvent
}
