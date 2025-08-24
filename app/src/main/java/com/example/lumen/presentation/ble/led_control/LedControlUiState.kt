package com.example.lumen.presentation.ble.led_control

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.LedControllerState

/**
 * Device related UI states
 * [connectedDevice] holds the connected device info
 * [controllerState] holds the LED controller's current state
 */
data class LedControlUiState(
    val connectedDevice: BleDevice? = null,
    val controllerState: LedControllerState? = null
)
