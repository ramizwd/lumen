package com.example.lumen.utils

object AppConstants {

    // Brightness value range that the LED controller use
    const val BRIGHTNESS_MIN: Float = 0f
    const val BRIGHTNESS_MAX: Float = 255f

    // Seems like the controller supports max 10 chars, anymore than that and it will freak out due to buffer overflow
    const val MAX_DEVICE_CHAR = 10
}