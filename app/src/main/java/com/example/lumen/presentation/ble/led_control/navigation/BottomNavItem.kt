package com.example.lumen.presentation.ble.led_control.navigation

import androidx.annotation.DrawableRes
import com.example.lumen.R

enum class BottomNavItem(
    val route: Screen,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
    val label: String,
    val contentDescription: String,
) {
    COLORS(
        Screen.ColorPickerScreen,
        R.drawable.palette_24px,
        R.drawable.palette_filled_24px,
        "Colors",
        "Navigate to colors"
    ),
    CONTROLS(
        Screen.ControlScreen,
        R.drawable.discover_tune_24px,
        R.drawable.discover_tune_semibold_24px,
        "Controls",
        "Navigate to controls"
    )
}