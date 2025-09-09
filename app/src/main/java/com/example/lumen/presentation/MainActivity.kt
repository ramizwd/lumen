package com.example.lumen.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.discovery.DiscoveryViewModel
import com.example.lumen.presentation.ble.led_control.LedControlScreen
import com.example.lumen.presentation.ble.led_control.LedControlViewModel
import com.example.lumen.presentation.common.utils.showToast
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.permissionArray
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object MainActivity {
        private const val LOG_TAG = "MainActivityLog"
    }

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if BLE is available, return if not
        val bluetoothLeAvailable = packageManager.
        hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        if (!bluetoothLeAvailable){
            Timber.tag(LOG_TAG).d("BLE not available.")
            return
        }

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            Timber.tag(LOG_TAG).d("request res: ${it.resultCode}")
        }

        // Launcher to request Bluetooth permissions
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth =
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true

            // If permissions are granted but Bluetooth not enabled, prompt to enable it
            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        permissionLauncher.launch(
            permissionArray
        )

        setContent {
            LumenTheme {
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
                            if (discoveryState.connectionState == ConnectionState.CONNECTED) {
                                snackbarJob?.cancel()
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
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
                    if (discoveryState.errorMessage != null &&
                        !discoveryState.showRetryConnection) {
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

                    when (discoveryState.connectionState) {
                        ConnectionState.CONNECTING -> {
                            Text(text = "CONNECTING...")
                        }
                        ConnectionState.CONNECTED -> {
                            if (controlState.controllerState != null) {
                                LedControlScreen(
                                    innerPadding,
                                    state = controlState,
                                    onDisconnectClick = discoveryViewModel::disconnectFromDevice,
                                    onTurnLedOnClick = ledControlViewModel::turnLedOn,
                                    onTurnLedOffClick = ledControlViewModel::turnLedOff,
                                    onChangePresetColorClick = ledControlViewModel::changePresetColor,
                                    onSetHsvColor = ledControlViewModel::setHsvColor,
                                    onChangeBrightness = ledControlViewModel::changeBrightness,
                                )
                            } else {
                                Text(text = "Loading state...")
                            }
                        }
                        ConnectionState.DISCONNECTING ->
                            Text(text = "DISCONNECTING...")
                        ConnectionState.DISCONNECTED -> {
                            DiscoverDevicesScreen(
                                innerPadding,
                                state = discoveryState,
                                onStartScan = discoveryViewModel::startScan,
                                onStopScan = discoveryViewModel::stopScan,
                                onConnectToDevice = discoveryViewModel::connectToDevice,
                            )
                        }
                        ConnectionState.RETRYING -> Text(text = "RETRYING CONNECTION...")
                        ConnectionState.WRONG_DEVICE -> Text(text = "WRONG DEVICE, DISCONNECTING...")
                    }
                }
            }
        }
    }
}
