package com.example.lumen.presentation.ble.discovery

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.ble.discovery.components.DeviceList
import com.example.lumen.presentation.ble.discovery.components.ScanButton
import com.example.lumen.presentation.common.components.PermissionAlertDialog
import com.example.lumen.presentation.common.utils.showToast
import com.example.lumen.presentation.theme.LumenTheme
import timber.log.Timber

private const val LOG_TAG = "DiscoverDevicesScreen"

@Composable
fun DiscoverDevicesScreen(
    innerPadding: PaddingValues,
    state: DiscoveryUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnectToDevice: (String) -> Unit,
) {
    val isScanning = state.isScanning
    val context = LocalContext.current.applicationContext
    val currentToastRef: MutableState<Toast?> = remember { mutableStateOf(null) }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            DeviceList(
                scanResults = state.scanResults,
                onDeviceClick = {
                    onConnectToDevice(it.address)
                },
                onStartScan = onStartScan,
            )
        }
        ScanButton(
            isEnabled = state.bluetoothState == BluetoothState.ON,
            onStartScan = onStartScan,
            onStopScan = onStopScan,
            isScanning = isScanning
        )

        if (state.showEnableBtDialog) {
            PermissionAlertDialog(
                onConfirmation = {
                    try {
                        enableBluetoothLauncher.launch(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        )
                    } catch (e: SecurityException) {
                        Timber.tag(LOG_TAG)
                            .e(e, "BLUETOOTH_CONNECT permission missing!")

                        showToast(
                            context = context,
                            message = "Nearby devices permission missing!",
                            duration = Toast.LENGTH_LONG,
                            currentToastRef = currentToastRef
                        )
                    }
                },
                dialogTitle = "Bluetooth is off",
                dialogText = "Please enable Bluetooth to start scanning.",
            )
        }
    }
}

@PreviewLightDark()
@Composable
fun DiscoverDevicesScreenWithDevicesPreview() {
    LumenTheme {
        Surface {
            val mockScanResults = listOf(
                BleDevice(name = "LED 1", address = "00:11:22:33:44:55"),
                BleDevice(name = "Test Device 2", address = "A:BB:CC:DD:EE:FF"),
                BleDevice(name = null, address = "FF:EE:DD:CC:BB:AA")
            )

            val state = DiscoveryUiState(
                scanResults = mockScanResults,
                isScanning = true,
                connectionState = ConnectionState.DISCONNECTED
            )

            DiscoverDevicesScreen(
                innerPadding = PaddingValues(),
                state = state,
                onStartScan = {},
                onStopScan = {},
                onConnectToDevice = {},
            )
        }
    }
}

@PreviewLightDark()
@Composable
fun DiscoverDevicesScreenWithoutDevicesPreview() {
    LumenTheme {
        Surface {
            val state = DiscoveryUiState(
                scanResults = emptyList(),
                isScanning = false,
                connectionState = ConnectionState.CONNECTED
            )

            DiscoverDevicesScreen(
                innerPadding = PaddingValues(),
                state = state,
                onStartScan = {},
                onStopScan = {},
                onConnectToDevice = {},
            )
        }
    }
}