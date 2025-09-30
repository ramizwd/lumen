package com.example.lumen.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.ble.discovery.DiscoveryViewModel
import com.example.lumen.presentation.ble.led_control.LedControlViewModel
import com.example.lumen.presentation.common.utils.showToast
import com.example.lumen.presentation.navigation.LumenNavHost
import com.example.lumen.presentation.navigation.Screen
import com.example.lumen.presentation.theme.LumenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object MainActivity {
        private const val LOG_TAG = "MainActivityLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bluetoothLeAvailable = packageManager.hasSystemFeature(
            PackageManager.FEATURE_BLUETOOTH_LE
        )

        if (!bluetoothLeAvailable){
            Timber.tag(LOG_TAG).d("BLE not available.")
            return
        }

        setContent {
            LumenTheme {
                val navController = rememberNavController()

                val discoveryViewModel = hiltViewModel<DiscoveryViewModel>()
                val discoveryState by discoveryViewModel.state.collectAsStateWithLifecycle()

                val ledControlViewModel = hiltViewModel<LedControlViewModel>()
                val controlState by ledControlViewModel.state.collectAsStateWithLifecycle()

                val currentToastRef: MutableState<Toast?> = remember {
                    mutableStateOf(null)
                }

                val snackbarHostState = remember { SnackbarHostState() }
                var snackbarJob: Job? by remember { mutableStateOf(null) }

                LaunchedEffect(key1 = Unit) {
                    launch {
                        discoveryViewModel.snackbarEvent.collect { event ->
                            snackbarJob?.cancel()

                            snackbarJob = launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = event.message,
                                    actionLabel = event.actionLabel,
                                    duration = event.duration
                                )

                                when (result) {
                                    SnackbarResult.Dismissed -> {
                                        discoveryViewModel.clearErrorMessage()
                                    }
                                    SnackbarResult.ActionPerformed -> {
                                        discoveryViewModel.retryConnection()
                                    }
                                }
                            }
                        }
                    }

                    launch {
                        discoveryViewModel.state.collect { uiState ->
                            if (discoveryState.connectionState == ConnectionState.CONNECTING ||
                                discoveryState.connectionState == ConnectionState.CONNECTED) {
                                snackbarJob?.cancel()
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                        }
                    }
                }

                LaunchedEffect(discoveryState.connectionState) {
                    if (discoveryState.connectionState == ConnectionState.CONNECTED &&
                        navController.currentDestination?.route != Screen.LedControlScreen.route) {
                        navController.navigate(Screen.LedControlScreen.route) {
                            popUpTo(Screen.DiscoverDevicesScreen.route) {
                                inclusive = true
                            }
                        }
                    } else if (discoveryState.connectionState == ConnectionState.DISCONNECTED &&
                        navController.currentDestination?.route != Screen.DiscoverDevicesScreen.route) {
                        navController.navigate(Screen.DiscoverDevicesScreen.route) {
                            popUpTo(Screen.LedControlScreen.route) { inclusive = true }
                        }
                    }
                }

                LaunchedEffect(key1 = discoveryState.infoMessage) {
                    discoveryState.infoMessage?.let { msg ->
                        showToast(
                            context = applicationContext,
                            message = msg,
                            duration = Toast.LENGTH_SHORT,
                            currentToastRef = currentToastRef
                        )
                        discoveryViewModel.clearInfoMessage()
                    }
                }

                LaunchedEffect(key1 = discoveryState.errorMessage) {
                    discoveryState.errorMessage?.let { msg ->
                        showToast(
                            context = applicationContext,
                            message = discoveryState.errorMessage!!,
                            duration = Toast.LENGTH_SHORT,
                            currentToastRef = currentToastRef
                        )
                        discoveryViewModel.clearErrorMessage()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    LumenNavHost(
                        innerPadding = innerPadding,
                        navController = navController,
                        discoveryUiState = discoveryState,
                        onStartScan = discoveryViewModel::startScan,
                        onStopScan = discoveryViewModel::stopScan,
                        onConnectToDevice = discoveryViewModel::connectToDevice,
                        ledControlUiState = controlState,
                        onDisconnectClick = discoveryViewModel::disconnectFromDevice,
                        onTurnLedOnClick = ledControlViewModel::turnLedOn,
                        onTurnLedOffClick = ledControlViewModel::turnLedOff,
                        onChangePresetColorClick = ledControlViewModel::changePresetColor,
                        onSetHsvColor = ledControlViewModel::setHsvColor,
                        onChangeBrightness = ledControlViewModel::changeBrightness,
                    )
                }
            }
        }
    }
}
