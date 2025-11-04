package com.example.lumen.presentation.ble.led_control

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lumen.R
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.presentation.ble.led_control.navigation.BottomNavItem
import com.example.lumen.presentation.ble.led_control.navigation.LedControlNavHost
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun LedControlScreen(
    modifier: Modifier = Modifier,
    viewModel: LedControlViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedControlContent(
        uiState = uiState,
        onTurnLedOnClick = viewModel::turnLedOn,
        onTurnLedOffClick = viewModel::turnLedOff,
        setLedColor = viewModel::setLedColor,
        onSaveCustomColorSlot = viewModel::saveCustomColor,
        onChangeBrightness = viewModel::changeBrightness,
        onDisconnectClick = viewModel::disconnectFromDevice,
        modifier = modifier,
    )
}

@Composable
fun LedControlContent(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedColor: (String) -> Unit,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val deviceName = uiState.selectedDevice?.name ?: "Unknown"

    val navController = rememberNavController()
    val startDestination = BottomNavItem.COLORS.route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                BottomNavItem.entries.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.route::class.qualifiedName
                    } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = if (selected) painterResource( item.iconSelected)
                                else painterResource( item.icon),
                                contentDescription = item.contentDescription
                            )
                        },
                        label = { Text(text = item.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        Text(text = deviceName)
        Button(onClick = onDisconnectClick) {
            Text(text = "Disconnect")
        }

        LedControlNavHost(
            uiState = uiState,
            onTurnLedOnClick = onTurnLedOnClick,
            onTurnLedOffClick = onTurnLedOffClick,
            setLedColor = setLedColor,
            onSaveCustomColorSlot = onSaveCustomColorSlot,
            onChangeBrightness = onChangeBrightness,
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@PreviewLightDark
@Composable
fun LedControlContentPreview() {
    LumenTheme {
        Surface {
            val connDevice = BleDevice(
                name = "Test device",
                address = "00:11:22:33:44:55"
            )
            val customColorsList = listOf(
                CustomColorSlot(1, "ffffff"),
                CustomColorSlot(2, "ffffff"),
                CustomColorSlot(3, "32a852"),
                CustomColorSlot(4, "ffffff"),
                CustomColorSlot(5, "ffffff"),
                CustomColorSlot(6, "bc77d1"),
                CustomColorSlot(7, "ffffff"),
            )

            val uiState = LedControlUiState(
                selectedDevice = connDevice,
                customColorSlots = customColorsList,
            )

            LedControlContent(
                uiState = uiState,
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = { },
                onSaveCustomColorSlot = { slotId, color -> },
                onChangeBrightness = { },
                onDisconnectClick = {},
            )
        }
    }
}