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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
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

    private val _uiState = MutableStateFlow(LedControlUiState())
    private val _brightnessChangeFlow = MutableSharedFlow<Float>()

    val uiState = combine(
        connectionUseCases.observeSelectedDeviceUseCase(),
        controlUseCases.observeControllerStateUseCase(),
        _uiState,
    ) { selectedDevice, controllerState, state ->
        state.copy(
            selectedDevice = selectedDevice,
            controllerState = controllerState,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _uiState.value
    )

    init {
        viewModelScope.launch {
            uiState
                .mapNotNull { it.selectedDevice?.address }
                .distinctUntilChanged()
                .collectLatest { deviceAddress ->
                    controlUseCases.getCustomColorsUseCase(deviceAddress).collect { colors ->
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
        viewModelScope.launch {
            controlUseCases.turnLedOnOffUseCase(true)
        }
    }

    fun turnLedOff() {
        viewModelScope.launch {
            controlUseCases.turnLedOnOffUseCase(false)
        }
    }

    fun setLedColor(hexColor: String) {
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