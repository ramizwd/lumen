package com.example.lumen.presentation.ble.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.discovery.DiscoveryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    private val _state = MutableStateFlow(DiscoveryUiState())

    val state = combine(
        discoveryUseCases.observeScanResultsUseCase(),
        discoveryUseCases.observeIsScanningUseCase(),
        connectionUseCases.observeConnectionUseCase(),
        _state
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
            selectedDevice?.let {
                connectionUseCases.connectToDeviceUseCase(selectedDevice)
            }
        }
    }

    fun disconnectFromDevice() {
        viewModelScope.launch {
            connectionUseCases.disconnectUseCase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        discoveryUseCases.stopScanUseCase()
        connectionUseCases.disconnectUseCase()
    }
}