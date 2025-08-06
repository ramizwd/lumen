package com.example.lumen.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lumen.data.ble.BleControllerImpl
import com.example.lumen.domain.ble.BleController
import kotlinx.coroutines.launch

/**
 * ViewModel for invoking BLE operations and retrieving results
 * [scanResults] holds BLE scan result of type BleDevice
 * [isScanning] Boolean for indicating if BLE is scanning
 */
class BleViewModel(private val bleController: BleController): ViewModel() {

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

    // Creates an instance of BleViewModel and provides its dependencies
    class BluetoothViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BleViewModel(BleControllerImpl(context.applicationContext)) as T
            }
            throw IllegalArgumentException("Unknown Viewmodel class")
        }
    }
}