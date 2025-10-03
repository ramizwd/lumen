package com.example.lumen.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.discovery.DiscoveryUiState
import com.example.lumen.presentation.ble.led_control.LedControlScreen
import com.example.lumen.presentation.ble.led_control.LedControlUiState
import com.example.lumen.presentation.common.components.ConnectionIndicator

/**
 * Top-level navigation graph
 */
@Composable
fun LumenNavHost(
    innerPadding: PaddingValues,
    navController: NavHostController,
    discoveryUiState: DiscoveryUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnectToDevice: (String) -> Unit,
    ledControlUiState: LedControlUiState,
    onDisconnectClick: () -> Unit,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangePresetColorClick: (PresetLedColors) -> Unit,
    onSetHsvColor: (String) -> Unit,
    onChangeBrightness: (Float) -> Unit,
    ) {
    NavHost(navController = navController, startDestination = DiscoverDevicesScreen) {

        composable<DiscoverDevicesScreen> {
            if (discoveryUiState.connectionState == ConnectionState.DISCONNECTED) {
                DiscoverDevicesScreen(
                    innerPadding = innerPadding,
                    state = discoveryUiState,
                    onStartScan = onStartScan,
                    onStopScan = onStopScan,
                    onConnectToDevice = onConnectToDevice,
                )
            } else {
                ConnectionIndicator(connectionState = discoveryUiState.connectionState)
            }
        }

        composable<LedControlScreen> {
            if (ledControlUiState.controllerState != null) {
                LedControlScreen(
                    innerPadding = innerPadding,
                    state = ledControlUiState,
                    onDisconnectClick = onDisconnectClick,
                    onTurnLedOnClick = onTurnLedOnClick,
                    onTurnLedOffClick = onTurnLedOffClick,
                    onChangePresetColorClick = onChangePresetColorClick,
                    onSetHsvColor = onSetHsvColor,
                    onChangeBrightness = onChangeBrightness,
                )
            }
        }
    }
}