package com.example.lumen.presentation.ble.led_control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.StaticLedColors
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to the connected device,
 * also responsible for invoking device control operations.
 */
@HiltViewModel
class LedControlViewModel @Inject constructor(
    private val connectionUseCases: ConnectionUseCases,
    private val controlUseCases: ControlUseCases,
): ViewModel() {

    private val _connectedDevice = MutableStateFlow<BleDevice?>(null)
    val connectedDevice: StateFlow<BleDevice?> = _connectedDevice.asStateFlow()

    init {
        viewModelScope.launch {
            connectionUseCases.observeConnectedDeviceUseCase().collect { device ->
                _connectedDevice.value = device
            }
        }
    }

    fun turnLedOn() {
        controlUseCases.turnLedOnOffUseCase(true)
    }

    fun turnLedOff() {
        controlUseCases.turnLedOnOffUseCase(false)
    }

    fun changeStaticColor(color: StaticLedColors) {
        controlUseCases.changeStaticColorUseCase(color)
    }
}