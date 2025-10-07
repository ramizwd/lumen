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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.BluetoothPermissionStatus
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
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DiscoverDevicesScreen(
    modifier: Modifier = Modifier,
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current

    val currentToastRef: MutableState<Toast?> = remember { mutableStateOf(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = context.hasBluetoothPermissions()
        val showRationale = activity?.shouldShowBluetoothRationale() == true

        viewModel.onBtPermissionResult(granted, showRationale)
        viewModel.onEvent(DiscoverDevicesUiEvent.TogglePermissionDialog(false))
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    LaunchedEffect(key1 = uiState.isBtDisabled, key2 = uiState.btPermissionStatus) {
        if (uiState.btPermissionStatus == BluetoothPermissionStatus.GRANTED) {
            viewModel.onEvent(
                DiscoverDevicesUiEvent.ToggleEnableBtDialog(uiState.isBtDisabled)
            )
        }
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    if (uiState.btPermissionStatus != BluetoothPermissionStatus.GRANTED) {
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

    LaunchedEffect(key1 = Unit) {
        launch {
            viewModel.snackbarEvent.collect { event ->
                val result = snackbarHostState.showSnackbar(
                    message = event.message,
                    actionLabel = event.actionLabel,
                    duration = event.duration
                )

                when (result) {
                    SnackbarResult.Dismissed -> {
                        viewModel.clearErrorMessage()
                    }
                    SnackbarResult.ActionPerformed -> {
                        viewModel.retryConnection()
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            showToast(
                context = context,
                message = msg,
                duration = Toast.LENGTH_SHORT,
                currentToastRef = currentToastRef
            )
            viewModel.clearInfoMessage()
        }
    }

    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            showToast(
                context = context,
                message = uiState.errorMessage!!,
                duration = Toast.LENGTH_SHORT,
                currentToastRef = currentToastRef
            )
            viewModel.clearErrorMessage()
        }
    }

    // Permission rationale
    if (uiState.showPermissionDialog) {
        PermissionAlertDialog(
            onConfirmation = {
                bluetoothPermissionLauncher.launch(btPermissionArray)
                viewModel.onEvent(
                    DiscoverDevicesUiEvent.TogglePermissionDialog(false)
                )
            },
            onDismissRequest = {
                viewModel.onEvent(
                    DiscoverDevicesUiEvent.TogglePermissionDialog(false)
                )
            },
            permissionTextProvider = BluetoothPermissionTextProvider()
        )
    }

    // Prompt to enable permission through app settings after permanent denial
    if (uiState.btPermissionStatus == BluetoothPermissionStatus.DENIED_PERMANENTLY &&
        uiState.showOpenSettingsDialog) {
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
                viewModel.onEvent(
                    DiscoverDevicesUiEvent.ToggleOpenSettingsDialog(false)
                )
            },
            onDismissRequest = {
                viewModel.onEvent(
                    DiscoverDevicesUiEvent.ToggleOpenSettingsDialog(false)
                )
            },
            permissionTextProvider = OpenAppSettingsTextProvider()
        )
    }

    // If permission granted but BT is off, prompt to enable
    if (uiState.btPermissionStatus == BluetoothPermissionStatus.GRANTED &&
        uiState.showEnableBtDialog) {
        PermissionAlertDialog(
            onConfirmation = {
                try {
                    enableBluetoothLauncher.launch(
                        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    )
                    viewModel.onEvent(
                        DiscoverDevicesUiEvent.ToggleEnableBtDialog(false)
                    )
                } catch (_: SecurityException) {
                    Timber.tag("DiscoverDevicesScreen").i("Show toast")
                    showToast(
                        context = context,
                        message = "Nearby devices permission missing!",
                        duration = Toast.LENGTH_SHORT,
                        currentToastRef = currentToastRef
                    )
                }
            },
            onDismissRequest = {
                viewModel.onEvent(
                    DiscoverDevicesUiEvent.ToggleEnableBtDialog(false)
                )
            },
            permissionTextProvider = EnableBluetoothTextProvider()
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        DiscoverDevicesContent(
            innerPadding = innerPadding,
            uiState = uiState,
            onStartScan = viewModel::startScan,
            onStopScan = viewModel::stopScan,
            onConnectToDevice = viewModel::connectToDevice,
            modifier = modifier,
        )
    }
}

@Composable
fun DiscoverDevicesContent(
    innerPadding: PaddingValues,
    uiState: DiscoveryUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnectToDevice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isScanning = uiState.isScanning
    val scanResult = uiState.scanResults

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (scanResult.isEmpty()) {
            if (!isScanning) {
                Text(text = "Start scanning to find nearby devices.")
            } else {
                Text(text = "Searching...")
            }
        }

        Box(modifier = modifier.weight(1f)) {
            DeviceList(
                scanResults = scanResult,
                onDeviceClick = {
                    onConnectToDevice(it.address)
                },
                onStartScan = onStartScan,
            )
        }

        ScanButton(
            onClick = { if (uiState.isScanning) onStopScan() else onStartScan() },
            isScanning = isScanning
        )
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentWithDevicesPreview() {
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
            )

            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                uiState = state,
                onStartScan = {},
                onStopScan = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentWithoutDevicesPreview() {
    LumenTheme {
        Surface {
            val state = DiscoveryUiState(
                scanResults = emptyList(),
                isScanning = false,
            )

            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                uiState = state,
                onStartScan = {},
                onStopScan = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentNoDevicesFoundPreview() {
    LumenTheme {
        Surface {
            val state = DiscoveryUiState(
                scanResults = emptyList(),
                isScanning = true,
            )

            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                uiState = state,
                onStartScan = {},
                onStopScan = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}