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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lumen.presentation.ble.BleViewModel
import com.example.lumen.presentation.ble.DiscoverDevicesScreen
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
                val vm = hiltViewModel<BleViewModel>()
                val state by vm.state.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiscoverDevicesScreen(
                        innerPadding,
                        state = state,
                        onStartScanClick = vm::startScan,
                        onStopScanClick = vm::stopScan,
                        onConnectToDevice = vm::connectToDevice,
                        onDisconnectClick = vm::disconnectFromDevice,
                        onTurnLedOnClick = vm::turnLedOn,
                        onTurnLedOffClick = vm::turnLedOff
                    )
                }
            }
        }
    }
}
