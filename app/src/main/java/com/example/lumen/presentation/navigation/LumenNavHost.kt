package com.example.lumen.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.led_control.LedControlScreen
import com.example.lumen.presentation.ble.led_control.LedControlViewModel
import com.example.lumen.presentation.common.components.ConnectionIndicator

/**
 * Top-level navigation graph
 */
@Composable
fun LumenNavHost(
    innerPadding: PaddingValues,
) {
    val navController = rememberNavController()

    val ledControlViewModel = hiltViewModel<LedControlViewModel>()
    val ledControlUiState by ledControlViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ledControlUiState.connectionState) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        if (ledControlUiState.connectionState == ConnectionState.STATE_LOADED_AND_CONNECTED &&
            currentRoute != LedControlScreen::class.qualifiedName) {
            navController.navigate(LedControlScreen) {
                popUpTo(DiscoverDevicesScreen) { inclusive = true }
                launchSingleTop = true
            }
        } else if (ledControlUiState.connectionState == ConnectionState.DISCONNECTED &&
            currentRoute != DiscoverDevicesScreen::class.qualifiedName) {
            navController.navigate(DiscoverDevicesScreen) {
                popUpTo(LedControlScreen) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = DiscoverDevicesScreen) {

        composable<DiscoverDevicesScreen> {
            if (ledControlUiState.connectionState == ConnectionState.DISCONNECTED) {
                DiscoverDevicesScreen()
            } else {
                ConnectionIndicator(connectionState = ledControlUiState.connectionState)
            }
        }

        composable<LedControlScreen> {
            if (ledControlUiState.controllerState != null) {
                LedControlScreen(
                    innerPadding = innerPadding,
                    viewModel = ledControlViewModel,
                )
            }
        }
    }
}