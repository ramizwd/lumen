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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.BluetoothPermissionStatus
import com.example.lumen.domain.ble.model.DeviceListType
import com.example.lumen.domain.ble.model.ScanState
import com.example.lumen.presentation.ble.discovery.components.DeviceList
import com.example.lumen.presentation.ble.discovery.components.ScanButton
import com.example.lumen.presentation.common.components.BluetoothPermissionTextProvider
import com.example.lumen.presentation.common.components.ChoiceChipRow
import com.example.lumen.presentation.common.components.EnableBluetoothTextProvider
import com.example.lumen.presentation.common.components.OpenAppSettingsTextProvider
import com.example.lumen.presentation.common.components.PermissionAlertDialog
import com.example.lumen.presentation.common.components.RadarScanAnimation
import com.example.lumen.presentation.common.model.DeviceContent
import com.example.lumen.presentation.common.utils.showToast
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing
import com.example.lumen.utils.btPermissionArray
import com.example.lumen.utils.hasBluetoothPermissions
import com.example.lumen.utils.shouldShowBluetoothRationale
import kotlinx.coroutines.launch

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
            isScanning = uiState.scanState == ScanState.SCANNING,
            scanResults = uiState.scanResults,
            emptyScanResultTxt = uiState.emptyScanResultTxt,
            currSelectedListType = uiState.selectedListType,
            onStartScan = viewModel::startScan,
            onStopScan = viewModel::stopScan,
            onFavDevice = viewModel::addFavDevice,
            onRemoveFavDevice = viewModel::removeFavDevice,
            onConnectToDevice = viewModel::connectToDevice,
            onSelectListFilter = viewModel::selectDeviceListType,
            modifier = modifier,
        )
    }
}

@Composable
fun DiscoverDevicesContent(
    innerPadding: PaddingValues,
    isScanning: Boolean,
    scanResults: List<DeviceContent>,
    emptyScanResultTxt: String?,
    currSelectedListType: DeviceListType,
    onSelectListFilter: (DeviceListType) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onFavDevice: (String) -> Unit,
    onRemoveFavDevice: (String) -> Unit,
    onConnectToDevice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.smallIncreased
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.spacing.large,
                    end = MaterialTheme.spacing.large
                ),
        ) {
            ChoiceChipRow(
                modifier = Modifier
                    .weight(1f),
                choices = DeviceListType.entries.map { it.displayName },
                selectedChoice = currSelectedListType.displayName,
                onChoiceSelected = { selected ->
                    val selectedEnum = DeviceListType.entries.first { it.displayName == selected }
                    onSelectListFilter(selectedEnum)
                },
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.smallIncreased
                )
            ) {
                RadarScanAnimation(isScanning = isScanning)

                ScanButton(
                    onStartScan = onStartScan,
                    onStopScan = onStopScan,
                    isScanning = isScanning,
                )
            }
        }

        DeviceList(
            scanResults = scanResults,
            emptyScanResultTxt = emptyScanResultTxt,
            onStartScan = onStartScan,
            onFavDevice = onFavDevice,
            onRemoveDevice = onRemoveFavDevice,
            onDeviceClick = { address ->
                onConnectToDevice(address)
            },
        )
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentPreview() {
    LumenTheme {
        Surface {
            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = false,
                scanResults = emptyList(),
                emptyScanResultTxt = "Start scanning to find nearby devices.",
                currSelectedListType = DeviceListType.ALL_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentSearchingPreview() {
    LumenTheme {
        Surface {
            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = true,
                scanResults = emptyList(),
                emptyScanResultTxt = "Searching...",
                currSelectedListType = DeviceListType.ALL_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentDevicesFoundPreview() {
    LumenTheme {
        Surface {
            val mockScanResults = listOf(
                DeviceContent(BleDevice(
                    name = "LED 1",
                    address = "00:11:22:33:44:55"),
                    isFavorite = true
                ),
                DeviceContent(BleDevice(
                    name = "Test Device 2",
                    address = "A:BB:CC:DD:EE:FF"),
                    isFavorite = false),
                DeviceContent(BleDevice(
                    name = null,
                    address = "FF:EE:DD:CC:BB:AA"),
                    isFavorite = true),
            )

            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = true,
                scanResults = mockScanResults,
                emptyScanResultTxt = null,
                currSelectedListType = DeviceListType.ALL_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
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
            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = false,
                scanResults = emptyList(),
                emptyScanResultTxt = "No devices found.",
                currSelectedListType = DeviceListType.ALL_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentFavDevicesPreview() {
    LumenTheme {
        Surface {
            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = false,
                scanResults = emptyList(),
                emptyScanResultTxt = "Start scanning to find favorite devices.",
                currSelectedListType = DeviceListType.FAVORITE_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentSearchingFavDevicesPreview() {
    LumenTheme {
        Surface {
            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = true,
                scanResults = emptyList(),
                emptyScanResultTxt = "Searching for favorites...",
                currSelectedListType = DeviceListType.FAVORITE_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentFavDevicesFoundPreview() {
    LumenTheme {
        Surface {
            val mockScanResults = listOf(
                DeviceContent(BleDevice(
                    name = "LED 1",
                    address = "00:11:22:33:44:55"),
                    isFavorite = true
                ),
                DeviceContent(BleDevice(
                    name = "Test Device 2",
                    address = "A:BB:CC:DD:EE:FF"),
                    isFavorite = true),
                DeviceContent(BleDevice(
                    name = null,
                    address = "FF:EE:DD:CC:BB:AA"),
                    isFavorite = true),
            )

            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = false,
                scanResults = mockScanResults,
                emptyScanResultTxt = null,
                currSelectedListType = DeviceListType.FAVORITE_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DiscoverDevicesContentFavDevicesNoDevicesPreview() {
    LumenTheme {
        Surface {
            DiscoverDevicesContent(
                innerPadding = PaddingValues(),
                isScanning = false,
                scanResults = emptyList(),
                emptyScanResultTxt = "No favorite devices found.",
                currSelectedListType = DeviceListType.FAVORITE_DEVICES,
                onSelectListFilter = {},
                onStartScan = {},
                onStopScan = {},
                onFavDevice = {},
                onRemoveFavDevice = {},
                onConnectToDevice = {},
                modifier = Modifier,
            )
        }
    }
}