package com.example.lumen.presentation.ble.ledcontrol

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.IcModel
import com.example.lumen.domain.ble.model.LedConstants.STATIC_COLOR_VALUE
import com.example.lumen.domain.ble.model.RgbSequence
import com.example.lumen.presentation.common.utils.UiText

/**
 * Device related UI states
 * [selectedDevice] holds the connected device info
 * [customColorSlots] list of custom colors set by the user
 * [isLedOn] Boolean than indicates if the LED controller is on/off
 * [ledHexColor] Holds the current hex color of the LED controller
 * [brightnessValue] Holds the current brightness value of the LED controller
 * [speedValue] Holds the current speed value of an LED effect
 * [totalActivePixels] Holds the active LED strip pixel count
 * [icModel] Holds the current IC model
 * [rgbSeq] Holds the current RGB sequence
 * [infoMessage] holds general info messages related to write operations
 * [showRenameDeviceDialog] toggles rename device dialog
 * [showPixelControlDialog] toggles pixel control dialog
 * [showHardwareConfigDialog] toggles hardware config dialog (IC model & RGB seq)
 */
data class LedControlUiState(
    val selectedDevice: BleDevice? = null,
    val customColorSlots: List<CustomColorSlot> = emptyList(),
    val isLedOn: Boolean = false,
    val ledHexColor: String = "ffffff",
    val ledEffectValue: Int = STATIC_COLOR_VALUE,
    val brightnessValue: Float = 0f,
    val speedValue: Float = 0f,
    val totalActivePixels: Int = 0,
    val icModel: IcModel = IcModel.WS2811,
    val rgbSeq: RgbSequence = RgbSequence.RGB,
    val infoMessage: UiText? = null,
    val showRenameDeviceDialog: Boolean = false,
    val showPixelControlDialog: Boolean = false,
    val showHardwareConfigDialog: Boolean = false,
)
