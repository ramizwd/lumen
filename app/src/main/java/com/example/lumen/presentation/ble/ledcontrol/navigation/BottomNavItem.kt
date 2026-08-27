package com.example.lumen.presentation.ble.ledcontrol.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.lumen.R

enum class BottomNavItem(
    val route: Screen,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
    @StringRes val label: Int,
    @StringRes val contentDescription: Int,
) {
    COLORS(
        Screen.ColorPickerScreen,
        R.drawable.palette_24px,
        R.drawable.palette_filled_24px,
        R.string.colors,
        R.string.navigate_to_colors,
    ),
    EFFECTS(
        Screen.EffectsScreen,
        R.drawable.wand_shine_24px,
        R.drawable.wand_shine_filled_24px,
        R.string.effects,
        R.string.navigate_to_effects,
    ),
    CONTROLS(
        Screen.ControlScreen,
        R.drawable.discover_tune_24px,
        R.drawable.discover_tune_semibold_24px,
        R.string.controls,
        R.string.navigate_to_controls,
    ),
}
