package com.example.lumen.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.discovery.DiscoveryViewModel
import com.example.lumen.presentation.ble.led_control.LedControlScreen
import com.example.lumen.presentation.ble.led_control.LedControlViewModel
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.permissionArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object MainActivity {
        private const val LOG_TAG = "MainActivity"
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
            Log.d(LOG_TAG, "BLE not available.")
            return
        }

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            Log.d(LOG_TAG, "request res: ${it.resultCode}")
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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                                    onChangeStaticColorClick = ledControlViewModel::changeStaticColor,
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
                    }
                }
            }
        }
    }
}
