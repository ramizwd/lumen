package com.example.lumen.domain.ble.model

/**
 * Represents the LED controller's state
 */
data class LedControllerState(
    val isOn: Boolean,
    val preset: Byte,
    val speed: Byte,
    val brightness: Float,
    val icModel: Byte,
    val channel: Byte,
    val pixelCount: Int,
    val red: String,
    val green: String,
    val blue: String,
    val whiteLedBrightness: Byte
)
