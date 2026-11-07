package com.example.lumen.presentation.navigation

sealed class NavigationEvent {
    object ToControl : NavigationEvent()

    object ToDiscovery : NavigationEvent()
}
