package com.example.lumen.presentation.ble.led_control

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.lumen.presentation.common.components.TextFieldDialog
import com.example.lumen.presentation.common.utils.DeviceConfiguration
import com.example.lumen.presentation.common.utils.showToast
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.AppConstants.MAX_DEVICE_CHAR

@Composable
fun LedControlScreen(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: LedControlViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfig = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val currToastRef: MutableState<Toast?> = remember { mutableStateOf(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deviceName = uiState.selectedDevice?.name ?: "Unknown"
    val textFieldState = rememberTextFieldState(initialText = deviceName)

    LaunchedEffect(key1 = uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            showToast(
                context = context,
                message = msg,
                duration = Toast.LENGTH_SHORT,
                currentToastRef = currToastRef
            )
        }
        viewModel.clearInfoMessage()
    }

    if (uiState.showRenameDeviceDialog) {
        TextFieldDialog(
            state = textFieldState,
            title = "Rename Device",
            initialText = deviceName,
            maxChar = MAX_DEVICE_CHAR,
            supportingText = "$MAX_DEVICE_CHAR characters max",
            onConfirmation = {
                viewModel.setDeviceName(it)
                viewModel.onEvent(LedControlUiEvent.ToggleRenameDeviceDialog(false))
            },
            onDismissRequest = {
                viewModel.onEvent(LedControlUiEvent.ToggleRenameDeviceDialog(false))
            }
        )
    }

    LedControlContent(
        deviceConfig = deviceConfig,
        uiState = uiState,
        deviceName = deviceName,
        rootNavController = rootNavController,
        context = context,
        currToastRef = currToastRef,
        onTurnLedOnClick = viewModel::turnLedOn,
        onTurnLedOffClick = viewModel::turnLedOff,
        setLedColor = viewModel::setLedColor,
        onSaveCustomColorSlot = viewModel::saveCustomColor,
        onChangeBrightness = viewModel::changeBrightness,
        onDisconnectClick = viewModel::disconnectFromDevice,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
fun LedControlContent(
    deviceConfig: DeviceConfiguration,
    uiState: LedControlUiState,
    deviceName: String,
    rootNavController: NavHostController,
    context: Context,
    currToastRef: MutableState<Toast?>,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedColor: (String) -> Unit,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onDisconnectClick: () -> Unit,
    onEvent: (LedControlUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                onActionClick = { onDisconnectClick() },
                onClickTitle = {
                    showToast(
                        context = context,
                        message = if (uiState.isLedOn) "Long press to rename"
                        else "Turn the device on to rename it",
                        duration = Toast.LENGTH_SHORT,
                        currentToastRef = currToastRef
                    )
                },
                onLongClickTitle = {
                    if (uiState.isLedOn) {
                        onEvent(LedControlUiEvent.ToggleRenameDeviceDialog(true))
                    } else {
                        showToast(
                            context = context,
                            message = "Turn the device on to rename it",
                            duration = Toast.LENGTH_LONG,
                            currentToastRef = currToastRef
                        )
                    }
                }
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
    val toastRef: MutableState<Toast?> = remember { mutableStateOf(null) }

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
                deviceName = "Test",
                rootNavController = rememberNavController(),
                context = LocalContext.current,
                currToastRef = toastRef,
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = { },
                onSaveCustomColorSlot = { _, _ -> },
                onChangeBrightness = { },
                onDisconnectClick = {},
                onEvent = { _ ->  }
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun LedControlContentLandscapePreview() {
    val toastRef: MutableState<Toast?> = remember { mutableStateOf(null) }

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
                deviceName = "Test",
                rootNavController = rememberNavController(),
                context = LocalContext.current,
                currToastRef = toastRef,
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = { },
                onSaveCustomColorSlot = { _, _ -> },
                onChangeBrightness = { },
                onDisconnectClick = {},
                onEvent = { _ ->  }

            )
        }
    }
}

@Preview(widthDp = 1200, heightDp = 800)
@Composable
fun LedControlContentTabletLandscapePreview() {
    val toastRef: MutableState<Toast?> = remember { mutableStateOf(null) }

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
                deviceName = "Test",
                rootNavController = rememberNavController(),
                context = LocalContext.current,
                currToastRef = toastRef,
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = { },
                onSaveCustomColorSlot = { _, _ -> },
                onChangeBrightness = { },
                onDisconnectClick = {},
                onEvent = { _ ->  }
            )
        }
    }
}