package com.example.lumen.presentation.ble.ledcontrol

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.presentation.common.utils.UiText

/**
 * Device related UI states
 * [selectedDevice] holds the connected device info
 * [customColorSlots] list of custom colors set by the user
 * [isLedOn] Boolean than indicates if the LED controller is on/off
 * [ledHexColor] Holds the current hex color of the LED controller
 * [brightnessValue] Holds the current brightness value of the LED controller
 * [totalActivePixels] Holds the active LED strip pixel count
 * [infoMessage] holds general info messages related to write operations
 * [showRenameDeviceDialog] toggles rename device dialog
 * [showPixelControlDialog] toggles pixel control dialog
 */
data class LedControlUiState(
    val selectedDevice: BleDevice? = null,
    val customColorSlots: List<CustomColorSlot> = emptyList(),
    val isLedOn: Boolean = false,
    val ledHexColor: String = "ffffff",
    val brightnessValue: Float = 0f,
    val totalActivePixels: Int = 0,
    val infoMessage: UiText? = null,
    val showRenameDeviceDialog: Boolean = false,
    val showPixelControlDialog: Boolean = false,
)
