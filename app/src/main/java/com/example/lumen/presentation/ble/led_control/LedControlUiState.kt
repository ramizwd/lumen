package com.example.lumen.presentation.ble.led_control

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.LedControllerState

/**
 * Device related UI states
 * [selectedDevice] holds the connected device info
 * [controllerState] holds the LED controller's current state
 * [connectionState] indicates the connection state of the GATT client
 */
data class LedControlUiState(
    val selectedDevice: BleDevice? = null,
    val controllerState: LedControllerState? = null,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
)
