package com.example.lumen.domain.ble.model

object LedConstants {
    // Brightness value range that the LED controller use
    val BRIGHTNESS_RANGE = 0f..255f

    // Active LED range allowed
    val ACTIVE_PIXELS_RANGE = 1..1024

    // Available LED effect range
    val LED_EFFECT_RANGE = 1..120

    val EFFECT_SPEED_RANGE = 0f..255f

    // Indicates if the controller is set to a static color
    const val STATIC_COLOR_VALUE = 121

    // Indicates if the controller is set to effects auto cycle
    const val EFFECT_CYCLE_VALUE = 0

    val CUSTOM_COLOR_SLOTS_RANGE = 1..7
}
