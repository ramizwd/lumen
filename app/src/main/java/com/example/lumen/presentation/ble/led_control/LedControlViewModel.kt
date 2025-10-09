package com.example.lumen.presentation.ble.led_control

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.domain.ble.usecase.common.ObserveBluetoothStateUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import com.example.lumen.utils.hexToComposeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to the connected device and its state,
 * also responsible for invoking device control operations.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class LedControlViewModel @Inject constructor(
    private val connectionUseCases: ConnectionUseCases,
    private val controlUseCases: ControlUseCases,
    observeBluetoothStateUseCase: ObserveBluetoothStateUseCase,
): ViewModel() {

    companion object {
        private const val LOG_TAG = "LedControlViewModel"
    }

    private val _uiState = MutableStateFlow(LedControlUiState())
    private val _brightnessChangeFlow = MutableSharedFlow<Float>()

    val uiState = combine(
        connectionUseCases.observeSelectedDeviceUseCase(),
        controlUseCases.observeControllerStateUseCase(),
        connectionUseCases.observeConnectionStateUseCase(),
        _uiState,
    ) { connectedDevice, controllerState, connectionState, state ->
        val initialLedColor = controllerState?.let {
            val red = it.red
            val green = it.green
            val blue = it.blue
            val hexColor = "$red$green$blue"
            try {
                hexColor.hexToComposeColor()
            } catch (e: IllegalArgumentException) {
                Timber.tag(LOG_TAG).e(
                    e,"Error converting to Compose color"
                )
                Color.White
            }
        } ?: Color.White

        state.copy(
            selectedDevice = connectedDevice,
            controllerState = controllerState,
            connectionState = connectionState,
            initialLedColor = initialLedColor,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _uiState.value
    )

    init {
        viewModelScope.launch {
            // Collects the most recent value emitted within 150 milliseconds and pass it down
            // the flow. This throttling helps prevent overflowing the GATT operation queue.
            _brightnessChangeFlow
                .sample(150L)
                .collect { value ->
                    controlUseCases.changeBrightnessUseCase(value)
                }
        }

        observeBluetoothStateUseCase().onEach { btState ->
            when (btState) {
                BluetoothState.ON -> {
                    Timber.tag(LOG_TAG).d("BT on")
                }
                BluetoothState.OFF -> {
                    Timber.tag(LOG_TAG).d("BT off")
                }
                BluetoothState.TURNING_ON -> {
                    Timber.tag(LOG_TAG).d("BT turning on...")
                }
                BluetoothState.TURNING_OFF -> {
                    Timber.tag(LOG_TAG).d("BT turning off...")

                    if (uiState.value.connectionState == ConnectionState.STATE_LOADED_AND_CONNECTED) {
                        Timber.tag(LOG_TAG).i("Disconnecting...")
                        disconnectFromDevice()
                    }
                }
                BluetoothState.UNKNOWN -> {
                    Timber.tag(LOG_TAG).d("BT state unknown")
                }
            }
        }.catch { throwable ->
            Timber.tag(LOG_TAG).e(throwable, "BT state observation error")
        }.launchIn(viewModelScope)
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

    fun setPresetColor(color: PresetLedColors) {
        viewModelScope.launch {
            controlUseCases.setPresetColorUseCase(color)
        }
    }

    fun setHsvColor(hexColor: String) {
        viewModelScope.launch {
            controlUseCases.setHsvColorUseCase(hexColor)
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