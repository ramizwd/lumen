package com.example.lumen.presentation.ble.ledcontrol.navigation

import kotlinx.serialization.Serializable

/**
 * Provides routes for LED control screens
 */
sealed class Screen {
    @Serializable data object ColorPickerScreen : Screen()

    @Serializable data object ControlScreen : Screen()
}
