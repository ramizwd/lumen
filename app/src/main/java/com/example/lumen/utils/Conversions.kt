package com.example.lumen.utils

import com.example.lumen.domain.ble.model.GattConstants.BRIGHTNESS_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.COLOR_SUFFIX_HEX
import com.example.lumen.domain.ble.model.LedConstants.BRIGHTNESS_RANGE

/**
 * Ensures the value is within the brightness range and converts it into byte.
 * @return the byte array representing brightness.
 */
fun Float.toBrightnessCommandBytes(): ByteArray {
    val brightnessInt = this.coerceIn(BRIGHTNESS_RANGE).toInt()

    return byteArrayOf(
        brightnessInt.toByte(),
        BRIGHTNESS_COMMAND[0],
        BRIGHTNESS_COMMAND[1],
        BRIGHTNESS_COMMAND[2],
    )
}

/**
 * Calculates the percentage of a value relative to a maximum.
 * Uses toInt() to ensure consistency with the LED controller's integer-based state.
 */
fun Float.calculatePercentage(max: Float): Float = (this.toInt() / max) * 100

/**
 * Formats a float as a percentage string, hiding the decimal if it's 0 or 100
 */
fun Float.formatAsPercentage(): String =
    if (this == 0f || this == 100f) {
        "%.0f".format(this)
    } else {
        "%.1f".format(this)
    }

/**
 * Converts hex color to byte array.
 * Format: RR GG BB and 1E (command byte)
 */
fun String.hexToColorCommandBytes(): ByteArray {
    val commandColor = this + COLOR_SUFFIX_HEX
    return commandColor.hexToByteArray()
}
