package com.example.lumen.utils

import com.example.lumen.domain.ble.model.GattConstants.BRIGHTNESS_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.COLOR_SUFFIX_HEX
import com.example.lumen.domain.ble.model.LedConstants.BRIGHTNESS_RANGE

/**
 * Ensures the value is within the brightness range and converts it into byte.
 * @return the byte array representing brightness.
 */
fun Float.toBrightnessCommandBytes(): ByteArray {
    val brightnessInt = this.coerceIn(BRIGHTNESS_RANGE.start, BRIGHTNESS_RANGE.endInclusive).toInt()

    return byteArrayOf(
        brightnessInt.toByte(),
        BRIGHTNESS_COMMAND[0],
        BRIGHTNESS_COMMAND[1],
        BRIGHTNESS_COMMAND[2],
    )
}

/**
 * Converts hex color to byte array.
 * Format: RR GG BB and 1E (command byte)
 */
fun String.hexToColorCommandBytes(): ByteArray {
    val commandColor = this + COLOR_SUFFIX_HEX
    return commandColor.hexToByteArray()
}
