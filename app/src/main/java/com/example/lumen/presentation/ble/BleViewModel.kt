package com.example.lumen.presentation.ble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.usecase.BleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for invoking BLE operations and retrieving results
 */
@HiltViewModel
class BleViewModel @Inject constructor(
    private val bleUseCases: BleUseCases
): ViewModel() {

    private val _state = MutableStateFlow(BleUiState())

    val state = combine(
        bleUseCases.observeScanResultsUseCase(),
        bleUseCases.observeIsScanningUseCase(),
        bleUseCases.observeConnectionUseCase(),
        bleUseCases.observeConnectedDeviceUseCase(),
        _state
    ) { scanResults, isScanning, connectionState, connectedDevice, state ->
        state.copy(
            scanResults = scanResults,
            isScanning = isScanning,
            connectionState = connectionState,
            connectedDevice = connectedDevice,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    fun startScan() {
        viewModelScope.launch {
            bleUseCases.startScanUseCase()
        }
    }

    fun stopScan() {
        viewModelScope.launch {
            bleUseCases.stopScanUseCase()
        }
    }

    fun connectToDevice(address: String) {
        viewModelScope.launch {
            bleUseCases.connectToDeviceUseCase(address)
        }
    }

    fun disconnectFromDevice() {
        viewModelScope.launch {
            bleUseCases.disconnectUseCase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleUseCases.stopScanUseCase()
        bleUseCases.disconnectUseCase()
    }
}