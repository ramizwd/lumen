package com.example.lumen.presentation.navigation

/**
 * Class for providing the navigation routes
 */
sealed class Screen(val route: String) {
    object DiscoverDevicesScreen: Screen("discover_devices_screen")
    object LedControlScreen: Screen("led_control_screen")
}