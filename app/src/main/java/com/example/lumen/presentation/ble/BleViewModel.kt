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
        _state
    ) { scanResults, isScanning, state ->
        state.copy(
            scanResults = scanResults,
            isScanning = isScanning
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

    override fun onCleared() {
        super.onCleared()
        bleUseCases.stopScanUseCase()
    }
}