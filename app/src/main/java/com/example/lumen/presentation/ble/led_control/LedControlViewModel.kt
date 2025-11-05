package com.example.lumen.presentation.ble.led_control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to the connected device and its state,
 * also responsible for invoking control operations.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class LedControlViewModel @Inject constructor(
    private val connectionUseCases: ConnectionUseCases,
    private val controlUseCases: ControlUseCases,
): ViewModel() {

    companion object {
        private const val LOG_TAG = "LedControlViewModel"
    }

    private val _brightnessChangeFlow = MutableSharedFlow<Float>()

    private val _uiState = MutableStateFlow(LedControlUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val selectedDevice = connectionUseCases.observeSelectedDeviceUseCase().first()
            _uiState.update { it.copy(
                selectedDevice = selectedDevice
            ) }
        }

        viewModelScope.launch {
            val initState = controlUseCases.observeControllerStateUseCase().first()
            _uiState.update { state ->
                state.copy(
                    isLedOn = initState?.isOn ?: false,
                    ledHexColor = initState?.let { "${it.red}${it.green}${it.blue}" } ?: "ffffff",
                    brightnessValue = initState?.brightness ?: 0f,
                    pixelCount = initState?.pixelCount ?: 0
                ) }
        }

        viewModelScope.launch {
            uiState.value.selectedDevice?.let { device ->
                controlUseCases.getCustomColorsUseCase(device.address)
                    .collect { colors ->
                        _uiState.update { it.copy(customColorSlots = colors) }
                        Timber.tag(LOG_TAG).d("Saved colors: $colors")
                    }
            }
        }

        viewModelScope.launch {
            // Collects the most recent value emitted within 150 milliseconds and pass it down
            // the flow. This throttling helps prevent overflowing the GATT operation queue.
            _brightnessChangeFlow
                .sample(250L)
                .collect { value ->
                    controlUseCases.changeBrightnessUseCase(value)
                }
        }
    }

    fun turnLedOn() {
        _uiState.update { it.copy(isLedOn = true) }
        viewModelScope.launch {
            controlUseCases.turnLedOnOffUseCase(true)
        }
    }

    fun turnLedOff() {
        _uiState.update { it.copy(isLedOn = false) }
        viewModelScope.launch {
            controlUseCases.turnLedOnOffUseCase(false)
        }
    }

    fun setLedColor(hexColor: String) {
        _uiState.update { it.copy(ledHexColor = hexColor) }
        viewModelScope.launch {
            controlUseCases.setLedColorUseCase(hexColor)
        }
    }

    fun saveCustomColor(slotId: Int, hexColor: String) {
        viewModelScope.launch {
            uiState.value.selectedDevice?.let { device ->
                val colorSlot = CustomColorSlot(slotId, hexColor)
                controlUseCases.saveCustomColorUseCase(device.address, colorSlot)
            }
        }
    }

    fun changeBrightness(value: Float) {
        _uiState.update { it.copy(brightnessValue = value) }
        viewModelScope.launch {
            _brightnessChangeFlow.emit(value)
        }
    }

    fun disconnectFromDevice() {
        viewModelScope.launch {
            connectionUseCases.disconnectUseCase()
        }
    }
}