package com.example.lumen.utils

import com.example.lumen.domain.ble.model.GattConstants.BRIGHTNESS_SUFFIX_HEX
import com.example.lumen.domain.ble.model.GattConstants.COLOR_SUFFIX_HEX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN

/**
 * Ensures the value is within the brightness range and converts it into hex string.
 * @return the byte array representing brightness.
 */
fun Float.toBrightnessCommandBytes(): ByteArray {
    val brightness = this.coerceIn(BRIGHTNESS_MIN, BRIGHTNESS_MAX).toInt()
    val hex = String.format("%02X", brightness)

    return "${hex}${BRIGHTNESS_SUFFIX_HEX}".hexToByteArray()
}

/**
 * Converts hex color to byte array.
 * Format: RR GG BB and 1E (command byte)
 */
fun String.hexToColorCommandBytes(): ByteArray {
    val commandColor = this + COLOR_SUFFIX_HEX
    return commandColor.hexToByteArray()
}
