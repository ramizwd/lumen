package com.example.lumen.presentation.ble.led_control

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.LedControllerState

/**
 * Device related UI states
 * [selectedDevice] holds the connected device info
 * [controllerState] holds the LED controller's current state
 * [customColorSlots] list of custom colors set by the user
 */
data class LedControlUiState(
    val selectedDevice: BleDevice? = null,
    val controllerState: LedControllerState? = null,
    val customColorSlots: List<CustomColorSlot> = emptyList()
)
