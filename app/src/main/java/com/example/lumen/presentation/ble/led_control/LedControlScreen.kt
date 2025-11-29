package com.example.lumen.presentation.ble.led_control

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.presentation.ble.led_control.navigation.BottomNavBar
import com.example.lumen.presentation.ble.led_control.navigation.BottomNavItem
import com.example.lumen.presentation.ble.led_control.navigation.LedControlNavHost
import com.example.lumen.presentation.ble.led_control.navigation.NavRail
import com.example.lumen.presentation.ble.led_control.navigation.TopAppBar
import com.example.lumen.presentation.common.utils.DeviceConfiguration
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun LedControlScreen(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: LedControlViewModel = hiltViewModel(),
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfig = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedControlContent(
        deviceConfig = deviceConfig,
        uiState = uiState,
        rootNavController = rootNavController,
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
    deviceConfig: DeviceConfiguration,
    uiState: LedControlUiState,
    rootNavController: NavHostController,
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
        topBar = {
            TopAppBar(
                title = deviceName,
                onNavIconClick = { rootNavController.popBackStack() },
                onActionClick = { onDisconnectClick() }
            )
        },
        bottomBar = {
            if (deviceConfig == DeviceConfiguration.TABLET_PORTRAIT ||
                deviceConfig == DeviceConfiguration.MOBILE_PORTRAIT) {
                BottomNavBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    windowInsets = NavigationBarDefaults.windowInsets,
                )
            }
        }
    ) { contentPadding ->
        if (deviceConfig == DeviceConfiguration.TABLET_LANDSCAPE ||
            deviceConfig == DeviceConfiguration.MOBILE_LANDSCAPE) {
            Row(modifier = Modifier.padding(top = contentPadding.calculateTopPadding())) {
                NavRail(
                    navController = navController,
                    currentDestination = currentDestination,
                    windowInsets = NavigationBarDefaults.windowInsets,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )

                LedControlNavHost(
                    uiState = uiState,
                    onTurnLedOnClick = onTurnLedOnClick,
                    onTurnLedOffClick = onTurnLedOffClick,
                    setLedColor = setLedColor,
                    onSaveCustomColorSlot = onSaveCustomColorSlot,
                    onChangeBrightness = onChangeBrightness,
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
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
                deviceConfig = DeviceConfiguration.MOBILE_PORTRAIT,
                uiState = uiState,
                rootNavController = rememberNavController(),
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = { },
                onSaveCustomColorSlot = { _, _ -> },
                onChangeBrightness = { },
                onDisconnectClick = {},
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun LedControlContentLandscapePreview() {
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
                deviceConfig = DeviceConfiguration.MOBILE_LANDSCAPE,
                uiState = uiState,
                rootNavController = rememberNavController(),
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = { },
                onSaveCustomColorSlot = { _, _ -> },
                onChangeBrightness = { },
                onDisconnectClick = {},
            )
        }
    }
}

@Preview(widthDp = 1200, heightDp = 800)
@Composable
fun LedControlContentTabletLandscapePreview() {
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
                deviceConfig = DeviceConfiguration.TABLET_LANDSCAPE,
                uiState = uiState,
                rootNavController = rememberNavController(),
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = { },
                onSaveCustomColorSlot = { _, _ -> },
                onChangeBrightness = { },
                onDisconnectClick = {},
            )
        }
    }
}