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
import com.example.lumen.presentation.MainViewModel
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.led_control.LedControlScreen
import com.example.lumen.presentation.common.components.LoadingOverlay

/**
 * Top-level navigation graph
 */
@Composable
fun LumenNavHost(
    innerPadding: PaddingValues,
) {
    val navController = rememberNavController()

    val mainViewModel = hiltViewModel<MainViewModel>()
    val connectionState by mainViewModel.connectionState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = connectionState) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        if (connectionState == ConnectionState.STATE_LOADED_AND_CONNECTED &&
            currentRoute != LedControlScreen::class.qualifiedName) {
            navController.navigate(LedControlScreen) {
                popUpTo(DiscoverDevicesScreen) { inclusive = true }
                launchSingleTop = true
            }
        } else if (connectionState == ConnectionState.DISCONNECTED &&
            currentRoute != DiscoverDevicesScreen::class.qualifiedName) {
            navController.navigate(DiscoverDevicesScreen) {
                popUpTo(LedControlScreen) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = DiscoverDevicesScreen) {

        composable<DiscoverDevicesScreen> {
            DiscoverDevicesScreen()

            val loadingText = when (connectionState) {
                ConnectionState.CONNECTING -> "Connecting..."
                ConnectionState.LOADING_DEVICE_STATE -> "Initializing..."
                ConnectionState.RETRYING -> "Connection failed, retrying..."
                ConnectionState.INVALID_DEVICE -> "Invalid device, disconnecting..."
                else -> ""
            }

            val showLoading = when (connectionState) {
                ConnectionState.CONNECTING,
                ConnectionState.LOADING_DEVICE_STATE,
                ConnectionState.INVALID_DEVICE,
                ConnectionState.RETRYING -> true
                else -> false
            }

            LoadingOverlay(
                text = loadingText,
                isVisible = showLoading,
                onDismiss = mainViewModel::disconnect,
            )
        }

        composable<LedControlScreen> {
            LedControlScreen(
                innerPadding = innerPadding,
            )
        }
    }
}