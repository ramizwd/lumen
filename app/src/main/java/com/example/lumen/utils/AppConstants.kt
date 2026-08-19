package com.example.lumen.utils

object AppConstants {
    const val SOURCE_CODE_URL = "https://github.com/ramizwd/lumen"

    // Brightness value range that the LED controller use
    const val BRIGHTNESS_MIN: Float = 0f
    const val BRIGHTNESS_MAX: Float = 255f

    // Seems like the controller supports max 10 chars, anymore than that and it will freak out due to buffer overflow
    const val MAX_DEVICE_CHAR = 10

    // Min/max allowed active LED count
    const val MIN_LED_NUM = 1
    const val MAX_LED_NUM = 1024
}
