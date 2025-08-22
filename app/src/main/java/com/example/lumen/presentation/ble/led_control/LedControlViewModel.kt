package com.example.lumen.presentation.ble.led_control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.StaticLedColors
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to the connected device,
 * also responsible for invoking device control operations.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class LedControlViewModel @Inject constructor(
    private val connectionUseCases: ConnectionUseCases,
    private val controlUseCases: ControlUseCases,
): ViewModel() {

    private val _connectedDevice = MutableStateFlow<BleDevice?>(null)
    val connectedDevice: StateFlow<BleDevice?> = _connectedDevice.asStateFlow()

    private val _brightnessChangeFlow = MutableSharedFlow<Int>()

    init {
        viewModelScope.launch {
            connectionUseCases.observeConnectedDeviceUseCase().collect { device ->
                _connectedDevice.value = device
            }
        }

        // Collects the most recent value emitted within 150 milliseconds and pass it down
        // the flow. This throttling helps prevent overflowing the GATT operation queue.
        viewModelScope.launch {
            _brightnessChangeFlow
                .sample(150L)
                .collect { value ->
                    controlUseCases.changeBrightnessUseCase(value)
                }
        }
    }

    fun turnLedOn() {
        viewModelScope.launch {
            controlUseCases.turnLedOnOffUseCase(true)
        }
    }

    fun turnLedOff() {
        viewModelScope.launch {
            controlUseCases.turnLedOnOffUseCase(false)
        }
    }

    fun changeStaticColor(color: StaticLedColors) {
        viewModelScope.launch {
            controlUseCases.changeStaticColorUseCase(color)
        }
    }

    fun changeBrightness(value: Int) {
        viewModelScope.launch {
            _brightnessChangeFlow.emit(value)
        }
    }
}