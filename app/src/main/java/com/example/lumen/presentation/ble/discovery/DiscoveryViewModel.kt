package com.example.lumen.presentation.ble.discovery

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.BluetoothPermissionStatus
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.model.DeviceListType
import com.example.lumen.domain.ble.model.ScanState
import com.example.lumen.domain.ble.usecase.common.ObserveBluetoothStateUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.discovery.DiscoveryUseCases
import com.example.lumen.domain.ble.usecase.prefs.PrefsUseCases
import com.example.lumen.presentation.common.model.DeviceContent
import com.example.lumen.presentation.common.model.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.find

/**
 * ViewModel responsible for managing scan UI state and
 * for invoking scan and connection operations.
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val discoveryUseCases: DiscoveryUseCases,
    private val connectionUseCases: ConnectionUseCases,
    private val prefsUseCases: PrefsUseCases,
    observeBluetoothStateUseCase: ObserveBluetoothStateUseCase,
): ViewModel() {

    companion object {
        private const val LOG_TAG = "DiscoveryViewModel"
    }

    private val _snackbarEvent = Channel<SnackbarEvent>(Channel.BUFFERED)
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow(DiscoveryUiState())

    val uiState = combine(
        discoveryUseCases.observeScanResultsUseCase(),
        discoveryUseCases.observeIsScanningUseCase(),
        prefsUseCases.getFavoriteDeviceAddressesUseCase(),
        prefsUseCases.getDeviceListPreferenceUseCase(),
        _uiState
    ) { scanResults, scanState, favAddresses, listType, state ->
        val devices = scanResults.map { device ->
            val isFavorite = favAddresses.contains(device.address)
            DeviceContent(device, isFavorite)
        }
        val favDevices = devices.filter { it.isFavorite }

        val deviceList = when (listType) {
            DeviceListType.ALL_DEVICES -> devices
            DeviceListType.FAVORITE_DEVICES -> favDevices
        }

        val emptyScanResTxt = getEmptyScanResultTxt(
            scanResults.isEmpty(),
            favDevices.isEmpty(),
            scanState,
            listType
        )

        state.copy(
            scanResults = deviceList,
            scanState = scanState,
            emptyScanResultTxt = emptyScanResTxt,
            selectedListType = listType,
        )
    }.combine(
        observeBluetoothStateUseCase(),
    ) { currentState, bluetoothState ->
        currentState.copy(
            bluetoothState = bluetoothState,
            isBtDisabled = bluetoothState == BluetoothState.OFF,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _uiState.value
    )

    init {
        discoveryUseCases.observeScanErrorsUseCase().onEach { error ->
            _uiState.update { it.copy(
                errorMessage = error
            ) }
        }.launchIn(viewModelScope)

        uiState
            .distinctUntilChangedBy {
                Pair(it.bluetoothState, it.btPermissionStatus)
            }
            .onEach { stateValue ->
                val btState = stateValue.bluetoothState
                val btPermStatus = stateValue.btPermissionStatus
                val isScanning = stateValue.scanState == ScanState.SCANNING

                when {
                    btState == BluetoothState.ON && !isScanning &&
                            btPermStatus == BluetoothPermissionStatus.GRANTED -> {
                        startScan()
                    }
                    btState == BluetoothState.TURNING_OFF && isScanning -> {
                        stopScan()
                    }
                }
            }.launchIn(viewModelScope)

        connectionUseCases.observeConnectionEventsUseCase().onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    _uiState.update { it.copy(
                        infoMessage = null,
                        errorMessage = null,
                    ) }
                }
                ConnectionResult.Disconnected -> {
                    _uiState.update { it.copy(infoMessage = "Disconnected") }
                }
                ConnectionResult.InvalidDevice -> {
                    _uiState.update { it.copy(infoMessage = "Invalid device") }
                }
                ConnectionResult.ConnectionCanceled -> {
                    _uiState.update { it.copy(infoMessage = "Connection canceled") }
                }

                is ConnectionResult.Error -> {
                    _uiState.update { it.copy(
                        errorMessage = result.message
                    ) }
                }
                is ConnectionResult.ConnectionFailed -> {
                    _snackbarEvent.send(
                        SnackbarEvent(
                            message = result.message,
                            actionLabel = "RETRY",
                            duration = SnackbarDuration.Long,
                        )
                    )
                }

            }
        }.catch { throwable ->
            Timber.tag(LOG_TAG)
                .e(throwable, "Connection event observation error")
        }.launchIn(viewModelScope)
    }

    fun onBtPermissionResult(granted: Boolean, showRationale: Boolean) {
        when {
            granted -> {
                _uiState.update {
                    it.copy(btPermissionStatus = BluetoothPermissionStatus.GRANTED)
                }
            }
            showRationale -> {
                _uiState.update {
                    it.copy(btPermissionStatus = BluetoothPermissionStatus.DENIED_RATIONALE_REQUIRED)
                }
            }
            else -> {
                _uiState.update {
                    it.copy(btPermissionStatus = BluetoothPermissionStatus.DENIED_PERMANENTLY)
                }
            }
        }
    }

    fun onEvent(event: DiscoverDevicesUiEvent) {
        when (event) {
            is DiscoverDevicesUiEvent.ToggleEnableBtDialog -> {
                _uiState.update { it.copy(showEnableBtDialog = event.show) }
            }
            is DiscoverDevicesUiEvent.ToggleOpenSettingsDialog -> {
                _uiState.update { it.copy(showOpenSettingsDialog = event.show) }
            }
            is DiscoverDevicesUiEvent.TogglePermissionDialog -> {
                _uiState.update { it.copy(showPermissionDialog = event.show) }
            }
        }
    }

    fun startScan() {
        if (handleScanPreconditions()) {
            viewModelScope.launch {
                discoveryUseCases.startScanUseCase()
            }
        }
    }

    fun stopScan() {
        if (handleScanPreconditions()) {
            viewModelScope.launch {
                discoveryUseCases.stopScanUseCase()
            }
        }
    }

    fun connectToDevice(address: String) {
        viewModelScope.launch {
            val selectedDeviceItem = uiState.value.scanResults.find { it.device.address == address }
            selectedDeviceItem?.let { deviceItem ->
                _uiState.update { it.copy(deviceToConnect = deviceItem.device) }
                connectionUseCases.connectToDeviceUseCase(deviceItem.device)
            }
        }
    }

    fun retryConnection() {
        viewModelScope.launch {
            uiState.value.deviceToConnect?.let { device ->
                connectionUseCases.connectToDeviceUseCase(device)
            } ?: _uiState.update { it.copy(errorMessage = "No device to retry connection for") }
        }
    }

    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun addFavDevice(address: String) {
        viewModelScope.launch {
            prefsUseCases.addFavoriteDeviceAddressUseCase(address)
        }
    }

    fun removeFavDevice(address: String) {
        viewModelScope.launch {
            prefsUseCases.removeFavoriteDeviceAddressUseCase(address)
        }
    }

    fun selectDeviceListType(listType: DeviceListType) {
        viewModelScope.launch {
            when (listType) {
                DeviceListType.ALL_DEVICES -> {
                    if (uiState.value.selectedListType != DeviceListType.ALL_DEVICES) {
                        Timber.tag(LOG_TAG).d("Save All Devices list type")
                        prefsUseCases.saveDeviceListPreferenceUseCase(
                            DeviceListType.ALL_DEVICES
                        )
                    }
                }
                DeviceListType.FAVORITE_DEVICES -> {
                    if (uiState.value.selectedListType != DeviceListType.FAVORITE_DEVICES) {
                        Timber.tag(LOG_TAG).d("Save Fav Devices list type")
                        prefsUseCases.saveDeviceListPreferenceUseCase(
                            DeviceListType.FAVORITE_DEVICES
                        )
                    }
                }
            }
        }
    }

    // Check BT permission and its state, trigger appropriate UI event if preconditions are not met
    private fun handleScanPreconditions(): Boolean {
        return when {
            uiState.value.btPermissionStatus == BluetoothPermissionStatus.DENIED_RATIONALE_REQUIRED -> {
                onEvent(DiscoverDevicesUiEvent.TogglePermissionDialog(true))
                false
            } uiState.value.btPermissionStatus == BluetoothPermissionStatus.DENIED_PERMANENTLY -> {
                onEvent(DiscoverDevicesUiEvent.ToggleOpenSettingsDialog(true))
                false
            }
            uiState.value.isBtDisabled-> {
                onEvent(DiscoverDevicesUiEvent.ToggleEnableBtDialog(true))
                false
            }
            else -> true
        }
    }

    private fun getEmptyScanResultTxt(
        isNoDevices: Boolean,
        isNoFavDevices: Boolean,
        scanState: ScanState,
        listType: DeviceListType,
    ): String? {
        val scanStateMsg = when (scanState) {
            ScanState.SCANNING -> "Searching..."
            ScanState.SCAN_PAUSED -> "Start scanning to find nearby devices."
            ScanState.SCAN_AUTO_PAUSED -> "No devices found."
        }

        val favScanStateMsg = when (scanState) {
            ScanState.SCANNING -> "Searching for favorites..."
            ScanState.SCAN_PAUSED -> "Start scanning to find favorite devices."
            ScanState.SCAN_AUTO_PAUSED -> "No favorite devices found."
        }

        return when (listType) {
            DeviceListType.ALL_DEVICES -> {
                if (isNoDevices) scanStateMsg else null
            }
            DeviceListType.FAVORITE_DEVICES -> {
                if (isNoFavDevices) favScanStateMsg else null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        discoveryUseCases.stopScanUseCase()
    }
}