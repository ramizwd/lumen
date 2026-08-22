package com.example.lumen.domain.ble.model

/**
 * Represents the LED controller's state
 */
data class LedControllerState(
    val isOn: Boolean,
    val preset: Int,
    val speed: Byte,
    val brightness: Float,
    val icModel: IcModel,
    val rgbSeq: RgbSequence,
    val totalActivePixels: Int,
    val red: String,
    val green: String,
    val blue: String,
    val whiteLedBrightness: Byte,
)
