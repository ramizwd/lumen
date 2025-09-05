package com.example.lumen.presentation.ble.discovery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.discovery.DiscoveryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to scan and connection,
 * also responsible for invoking scan and connection operations.
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val discoveryUseCases: DiscoveryUseCases,
    private val connectionUseCases: ConnectionUseCases,
): ViewModel() {

    companion object {
        private const val LOG_TAG = "DiscoveryViewModel"
    }

    private val _state = MutableStateFlow(DiscoveryUiState())

    val state = combine(
        discoveryUseCases.observeScanResultsUseCase(),
        discoveryUseCases.observeIsScanningUseCase(),
        connectionUseCases.observeConnectionStateUseCase(),
        _state.onStart { startScan() } //TODO: causing scan to fail if BT is not available. Need to start when BT is ready
    ) { scanResults, isScanning, connectionState, state ->
        state.copy(
            scanResults = scanResults,
            isScanning = isScanning,
            connectionState = connectionState,
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

        connectionUseCases.observeConnectionEventsUseCase().onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update { it.copy(infoMessage = null) }
                }
                ConnectionResult.Disconnected -> {
                    _state.update { it.copy(infoMessage = "Disconnected") }
                }

                is ConnectionResult.Error -> {
                    _state.update { it.copy(
                        errorMessage = result.message, shouldShowRetryConnection = false
                    ) }
                }

                is ConnectionResult.ConnectionFailed -> {
                    _state.update { it.copy(
                        errorMessage = result.message, shouldShowRetryConnection = true
                    ) }
                }
            }
        }.catch { throwable ->
            Log.e(LOG_TAG, "Connection event error: ${throwable.localizedMessage}")
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