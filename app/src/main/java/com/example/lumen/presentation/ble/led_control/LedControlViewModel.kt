package com.example.lumen.presentation.ble.led_control

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.StaticLedColors
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to the connected device and its state,
 * also responsible for invoking device control operations.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class LedControlViewModel @Inject constructor(
    connectionUseCases: ConnectionUseCases,
    private val controlUseCases: ControlUseCases,
): ViewModel() {

    companion object {
        private const val LOG_TAG = "LedControlViewModel"
    }

    private val _state = MutableStateFlow(LedControlUiState())

    val state = combine(
        connectionUseCases.observeConnectedDeviceUseCase(),
        controlUseCases.observeControllerStateUseCase(),
        _state,
    ) { connectedDevice, controllerState, state ->
        Log.d(LOG_TAG, "collected state: $controllerState")

        state.copy(
            connectedDevice = connectedDevice,
            controllerState = controllerState,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    private val _brightnessChangeFlow = MutableSharedFlow<Int>()

    init {
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