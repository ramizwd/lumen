package com.example.lumen.presentation.ble.ledcontrol

import com.example.lumen.R
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
 * [isLedOn] Boolean that indicates if the LED controller is on/off
 * [hasWhiteLed] Indicates if the IC type has white LEDs
 * [ledHexColor] Holds the current hex color of the LED controller
 * [ledEffectValue] Holds the current effect value
 * [effectPickerTxt] Holds the current effect picker text
 * [favoriteEffects] Holds a set of favorite integers of effects
 * [brightnessValue] Holds the current brightness value of the LED controller
 * [whiteLedBrightnessValue] Holds the current brightness for the white LED
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
    val hasWhiteLed: Boolean = false,
    val ledHexColor: String = "ffffff",
    val ledEffectValue: Int = STATIC_COLOR_VALUE,
    val effectPickerTxt: UiText = UiText.StringResource(R.string.static_color),
    val favoriteEffects: Set<Int> = emptySet(),
    val brightnessValue: Float = 0f,
    val whiteLedBrightnessValue: Float = 0f,
    val speedValue: Float = 0f,
    val totalActivePixels: Int = 0,
    val icModel: IcModel = IcModel.WS2811,
    val rgbSeq: RgbSequence = RgbSequence.RGB,
    val infoMessage: UiText? = null,
    val showRenameDeviceDialog: Boolean = false,
    val showPixelControlDialog: Boolean = false,
    val showHardwareConfigDialog: Boolean = false,
)
