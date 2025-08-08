package com.example.lumen.presentation.ble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.BleController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for invoking BLE operations and retrieving results
 * [scanResults] holds BLE scan result of type BleDevice
 * [isScanning] Boolean for indicating if BLE is scanning
 */
@HiltViewModel
class BleViewModel @Inject constructor(
    private val bleController: BleController
): ViewModel() {

    val scanResults = bleController.scanResults
    val isScanning = bleController.isScanning

    fun startScan() {
        viewModelScope.launch {
            bleController.startScan()
        }
    }

    fun stopScan() {
        viewModelScope.launch {
            bleController.stopScan()
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleController.stopScan()
    }
}