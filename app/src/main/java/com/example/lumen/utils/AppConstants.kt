package com.example.lumen.utils

object AppConstants {
    const val SOURCE_CODE_URL = "https://github.com/ramizwd/lumen"

    // Seems like the controller supports max 10 chars, anymore than that and it will freak out due to buffer overflow
    const val MAX_DEVICE_CHAR = 10
}
