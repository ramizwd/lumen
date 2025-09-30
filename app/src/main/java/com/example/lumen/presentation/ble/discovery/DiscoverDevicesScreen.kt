package com.example.lumen.presentation.ble.discovery

import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.ble.discovery.components.DeviceList
import com.example.lumen.presentation.ble.discovery.components.ScanButton
import com.example.lumen.presentation.common.components.BluetoothPermissionTextProvider
import com.example.lumen.presentation.common.components.EnableBluetoothTextProvider
import com.example.lumen.presentation.common.components.OpenAppSettingsTextProvider
import com.example.lumen.presentation.common.components.PermissionAlertDialog
import com.example.lumen.presentation.common.utils.showToast
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.btPermissionArray
import com.example.lumen.utils.hasBluetoothPermissions
import com.example.lumen.utils.shouldShowBluetoothRationale

@Composable
fun DiscoverDevicesScreen(
    innerPadding: PaddingValues,
    state: DiscoveryUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnectToDevice: (String) -> Unit,
) {
    val isScanning = state.isScanning
    val scanResult = state.scanResults
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current

    val currentToastRef: MutableState<Toast?> = remember { mutableStateOf(null) }

    var showEnableBtDialog by rememberSaveable { mutableStateOf(state.isBtDisabled) }
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }
    var showOpenSettingsDialog by rememberSaveable { mutableStateOf(false) }

    var hasBtPermissions by remember { mutableStateOf(context.hasBluetoothPermissions()) }
    var showBtPermissionRationale by remember {
        mutableStateOf(activity?.shouldShowBluetoothRationale() == true)
    }

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasBtPermissions = context.hasBluetoothPermissions()
        showBtPermissionRationale = activity?.shouldShowBluetoothRationale() == true
        showPermissionDialog = false
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    LaunchedEffect(state.isBtDisabled) {
        if (hasBtPermissions) showEnableBtDialog = state.isBtDisabled
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    if (!hasBtPermissions) {
                        bluetoothPermissionLauncher.launch(btPermissionArray)
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (state.connectionState) {
            ConnectionState.CONNECTING -> Text(text = "CONNECTING...")
            ConnectionState.DISCONNECTING -> Text(text = "DISCONNECTING...")
            ConnectionState.DISCONNECTED -> Text(text = "DISCONNECTED")
            ConnectionState.RETRYING -> Text(text = "RETRYING...")
            ConnectionState.WRONG_DEVICE -> Text(text = "WRONG_DEVICE, DISCONNECTING...")
            else -> {}
        }

        // Permission rationale
        if (showPermissionDialog) {
            PermissionAlertDialog(
                onConfirmation = {
                    bluetoothPermissionLauncher.launch(btPermissionArray)
                    showPermissionDialog = false
                },
                onDismissRequest = { showPermissionDialog = false },
                permissionTextProvider = BluetoothPermissionTextProvider()
            )
        }

        // Prompt to enable permission through app settings after permanent denial
        if (showOpenSettingsDialog) {
            PermissionAlertDialog(
                onConfirmation = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )

                    try {
                        context.startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        showToast(
                            context = context,
                            message = "Could not open app settings. Try again",
                            duration = Toast.LENGTH_SHORT,
                            currentToastRef = currentToastRef
                        )
                    }
                    showOpenSettingsDialog = false
                },
                onDismissRequest = { showOpenSettingsDialog = false },
                permissionTextProvider = OpenAppSettingsTextProvider()
            )
        }

        // If permission enabled but BT is off, prompt to enable
        if (hasBtPermissions && showEnableBtDialog) {
            PermissionAlertDialog(
                onConfirmation = {
                    try {
                        enableBluetoothLauncher.launch(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        )
                        showEnableBtDialog = false
                    } catch (_: SecurityException) {
                        showToast(
                            context = context,
                            message = "Nearby devices permission missing!",
                            duration = Toast.LENGTH_SHORT,
                            currentToastRef = currentToastRef
                        )
                    }
                },
                onDismissRequest = { showEnableBtDialog = false },
                permissionTextProvider = EnableBluetoothTextProvider()
            )
        }

        if (scanResult.isEmpty()) {
            if (!isScanning) {
                Text(text = "Start scanning to find nearby devices.")
            } else {
                Text(text = "Searching...")
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            DeviceList(
                scanResults = scanResult,
                onDeviceClick = {
                    onConnectToDevice(it.address)
                },
                onStartScan = onStartScan,
            )
        }

        ScanButton(
            onClick = {
                when {
                    !hasBtPermissions -> {
                        if (showBtPermissionRationale) {
                            showPermissionDialog = true
                        } else {
                            showOpenSettingsDialog = true
                        }
                    }
                    state.isBtDisabled -> {
                        showEnableBtDialog = true
                    }
                    else -> {
                        if (isScanning) onStopScan() else onStartScan()
                    }
                }
            },
            isScanning = isScanning
        )
    }
}

@PreviewLightDark
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

@PreviewLightDark
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

@PreviewLightDark
@Composable
fun DiscoverDevicesScreenNoDevicesFoundPreview() {
    LumenTheme {
        Surface {
            val state = DiscoveryUiState(
                scanResults = emptyList(),
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