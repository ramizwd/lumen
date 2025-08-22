package com.example.lumen.utils

import com.example.lumen.domain.ble.model.GattConstants.BRIGHTNESS_SUFFIX_HEX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN

/**
 * Ensures the value is within the brightness range and
 * returns the brightness hex.
 */
fun Int.toBrightnessHex(): String {
    val brightness = this.coerceIn(BRIGHTNESS_MIN, BRIGHTNESS_MAX)
    val hex = String.format("%02X", brightness)

    return "${hex}${BRIGHTNESS_SUFFIX_HEX}"
}