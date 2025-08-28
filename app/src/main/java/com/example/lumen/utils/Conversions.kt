package com.example.lumen.utils

import androidx.compose.ui.graphics.Color
import com.example.lumen.domain.ble.model.GattConstants.BRIGHTNESS_SUFFIX_HEX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN
import androidx.core.graphics.toColorInt

/**
 * Ensures the value is within the brightness range and converts it into hex string.
 * @return the hex string representing brightness.
 */
fun Float.toBrightnessHex(): String {
    val brightness = this.coerceIn(BRIGHTNESS_MIN, BRIGHTNESS_MAX).toInt()
    val hex = String.format("%02X", brightness)

    return "${hex}${BRIGHTNESS_SUFFIX_HEX}"
}

/**
 * Converts 6 digit hex color string to Compose Color object.
 * @return [Color] object.
 */
fun String.hexToComposeColor(): Color {
    if (this.length != 6) {
        throw IllegalArgumentException("Hex string must be 6 characters long. Found: $this")
    }

    val colorInt = "#$this".toColorInt()
    return Color(colorInt)
}