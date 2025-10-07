package com.example.lumen.presentation.ble.discovery

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.BluetoothPermissionStatus
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.usecase.common.ObserveBluetoothStateUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.discovery.DiscoveryUseCases
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

/**
 * ViewModel responsible for managing scan UI state and
 * for invoking scan and connection operations.
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val discoveryUseCases: DiscoveryUseCases,
    private val connectionUseCases: ConnectionUseCases,
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
        observeBluetoothStateUseCase(),
        _uiState
    ) { scanResults, isScanning, bluetoothState, state ->
        state.copy(
            scanResults = scanResults,
            isScanning = isScanning,
            bluetoothState = bluetoothState,
            isBtDisabled = bluetoothState == BluetoothState.OFF
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
                val isScanning = stateValue.isScanning

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
        if (isBtAvailable()) {
            viewModelScope.launch {
                discoveryUseCases.startScanUseCase()
            }
        }
    }

    fun stopScan() {
        if (isBtAvailable()) {
            viewModelScope.launch {
                discoveryUseCases.stopScanUseCase()
            }
        }
    }

    fun connectToDevice(address: String) {
        viewModelScope.launch {
            val selectedDevice = uiState.value.scanResults.find { it.address == address }
            selectedDevice?.let { device ->
                _uiState.update { it.copy(deviceToConnect = device) }
                connectionUseCases.connectToDeviceUseCase(device)
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

    // Check BT permission and its state, trigger appropriate UI event and return a boolean
    private fun isBtAvailable(): Boolean {
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

    override fun onCleared() {
        super.onCleared()
        discoveryUseCases.stopScanUseCase()
    }
}