package com.example.lumen.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.theme.LumenTheme

class MainActivity : ComponentActivity() {

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
            Log.d("BLE_DBG", "BLE not available.")
            return
        }

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {}

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
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        )

        setContent {
            LumenTheme {
                val context = LocalContext.current
                val vm: BleViewModel = viewModel(
                    factory = BleViewModel.BluetoothViewModelFactory(context.applicationContext)
                )

                val scanResults by vm.scanResults.collectAsState()
                val isScanning by vm.isScanning.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiscoverDeviceScreen(
                        innerPadding,
                        isScanning = isScanning,
                        scanResults = scanResults,
                        onStartScanClick = {
                            vm.startScan()
                        },
                        onStopScanClick = {
                          vm.stopScan()
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun DiscoverDeviceScreen(
    innerPadding: PaddingValues,
    isScanning: Boolean,
    scanResults: List<BleDevice>,
    onStartScanClick: () -> Unit,
    onStopScanClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = if (isScanning) onStopScanClick else onStartScanClick) {
            Text(if (isScanning) "Stop Scan" else "Start Scan")
        }

        Text(
            text = "Devices:",
            modifier = Modifier.padding(top = 16.dp)
        )

        if (scanResults.isEmpty()) {
            Text("No devices found")
        } else {
            LazyColumn {
                items(scanResults) { device ->
                    DeviceItem(device = device)
                }
            }
        }
    }
}

@Composable
fun DeviceItem(device: BleDevice) {
    Column (
     modifier = Modifier
         .fillMaxSize()
         .padding(vertical = 4.dp)
    ){
        Text("Name: ${device.name ?: "Unknown"}")
        Text("Address: ${device.address}")
    }
}


@PreviewLightDark()
@Composable
fun DiscoverDeviceScreenWithDevicesPreview() {
    LumenTheme {
        Surface {
            val mockScanResults = listOf(
                BleDevice(name = "LED 1", address = "00:11:22:33:44:55"),
                BleDevice(name = "Test Device 2", address = "A:BB:CC:DD:EE:FF"),
                BleDevice(name = null, address = "FF:EE:DD:CC:BB:AA")
            )

            DiscoverDeviceScreen(
                innerPadding = PaddingValues(),
                isScanning = false,
                scanResults = mockScanResults,
                onStartScanClick = {},
                onStopScanClick = {},
            )
        }
    }
}

@PreviewLightDark()
@Composable
fun DiscoverDeviceScreenWithoutDevicesPreview() {
    LumenTheme {
        Surface {
            DiscoverDeviceScreen(
                innerPadding = PaddingValues(),
                isScanning = false,
                scanResults = emptyList(),
                onStartScanClick = {},
                onStopScanClick = {},
            )
        }
    }
}

@Preview(heightDp = 100, uiMode = UI_MODE_NIGHT_YES)
@Preview(heightDp = 100, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun DeviceItemPreview() {
    LumenTheme {
        Surface {
            DeviceItem(
                device = BleDevice(
                    name = "LED Test", address = "00:11:22:33:44:55"
                )
            )
        }
    }
}