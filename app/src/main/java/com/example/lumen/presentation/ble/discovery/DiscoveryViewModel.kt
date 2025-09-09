package com.example.lumen.presentation.ble.discovery

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.model.ConnectionState
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to scan and connection,
 * also responsible for invoking scan and connection operations.
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val discoveryUseCases: DiscoveryUseCases,
    private val connectionUseCases: ConnectionUseCases,
    observeBluetoothStateUseCase: ObserveBluetoothStateUseCase
): ViewModel() {

    companion object {
        private const val LOG_TAG = "DiscoveryViewModel"
    }

    private val _snackbarEvent = Channel<SnackbarEvent>(Channel.BUFFERED)
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _state = MutableStateFlow(DiscoveryUiState())

    val state = combine(
        discoveryUseCases.observeScanResultsUseCase(),
        discoveryUseCases.observeIsScanningUseCase(),
        observeBluetoothStateUseCase(),
        connectionUseCases.observeConnectionStateUseCase(),
        _state
    ) { scanResults, isScanning, bluetoothState, connectionState, state ->
        state.copy(
            scanResults = scanResults,
            isScanning = isScanning,
            bluetoothState = bluetoothState,
            connectionState = connectionState,
            showEnableBtDialog = bluetoothState == BluetoothState.OFF
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    init {
        discoveryUseCases.observeScanErrorsUseCase().onEach { error ->
            _state.update { it.copy(
                errorMessage = error
            ) }
        }.launchIn(viewModelScope)

        observeBluetoothStateUseCase().onEach { btState ->
            when (btState) {
                BluetoothState.ON -> {
                    Timber.tag(LOG_TAG).d("BT on")
                    startScan()
                }
                BluetoothState.OFF -> {
                    Timber.tag(LOG_TAG).d("BT off")
                }
                BluetoothState.TURNING_ON -> {
                    Timber.tag(LOG_TAG).d("BT turning on...")
                }
                BluetoothState.TURNING_OFF -> {
                    Timber.tag(LOG_TAG).d("BT turning off...")
                    if (state.value.connectionState == ConnectionState.CONNECTED) {
                        Timber.tag(LOG_TAG).i("Disconnecting...")
                        disconnectFromDevice()
                    }

                    if(state.value.isScanning){
                        Timber.tag(LOG_TAG).i("Stopping scan...")
                        stopScan()
                    }
                }
                BluetoothState.UNKNOWN -> {
                    Timber.tag(LOG_TAG).d("BT state unknown")
                }
            }
        }.catch { throwable ->
            Timber.tag(LOG_TAG)
                .e(throwable, "BT state observation error")
        }.launchIn(viewModelScope)

        connectionUseCases.observeConnectionEventsUseCase().onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update { it.copy(
                        infoMessage = null,
                        errorMessage = null,
                        showRetryConnection = false
                    ) }
                }
                ConnectionResult.Disconnected -> {
                    _state.update { it.copy(infoMessage = "Disconnected") }
                }

                is ConnectionResult.Error -> {
                    _state.update { it.copy(
                        errorMessage = result.message, showRetryConnection = false
                    ) }
                }
                is ConnectionResult.ConnectionFailed -> {
                    _state.update { it.copy(showRetryConnection = true) }
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

    fun startScan() {
        viewModelScope.launch {
            discoveryUseCases.startScanUseCase()
        }
    }

    fun stopScan() {
        viewModelScope.launch {
            discoveryUseCases.stopScanUseCase()
        }
    }

    fun connectToDevice(address: String) {
        viewModelScope.launch {
            val selectedDevice = state.value.scanResults.find { it.address == address }
            selectedDevice?.let { device ->
                _state.update { it.copy(deviceToConnect = device) }
                connectionUseCases.connectToDeviceUseCase(device)
            }
        }
    }

    fun retryConnection() {
        viewModelScope.launch {
            state.value.deviceToConnect?.let { device ->
                connectionUseCases.connectToDeviceUseCase(device)
            } ?: _state.update { it.copy(errorMessage = "No device to retry connection for") }
        }
    }

    fun disconnectFromDevice() {
        viewModelScope.launch {
            connectionUseCases.disconnectUseCase()
        }
    }

    fun clearInfoMessage() {
        _state.update { it.copy(infoMessage = null) }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        discoveryUseCases.stopScanUseCase()
    }
}