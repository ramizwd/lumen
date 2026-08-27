package com.example.lumen.presentation.ble.ledcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.R
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.IcModel
import com.example.lumen.domain.ble.model.LedConstants.EFFECT_CYCLE_VALUE
import com.example.lumen.domain.ble.model.LedConstants.LED_EFFECT_RANGE
import com.example.lumen.domain.ble.model.LedConstants.STATIC_COLOR_VALUE
import com.example.lumen.domain.ble.model.RgbSequence
import com.example.lumen.domain.ble.usecase.config.ConfigUseCases
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import com.example.lumen.domain.ble.usecase.prefs.PrefsUseCases
import com.example.lumen.presentation.common.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for managing UI state related to the connected device and its state,
 * also responsible for invoking control operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LedControlViewModel @Inject constructor(
    private val connectionUseCases: ConnectionUseCases,
    private val controlUseCases: ControlUseCases,
    private val configUseCases: ConfigUseCases,
    private val prefsUseCases: PrefsUseCases,
) : ViewModel() {
    companion object {
        private const val LOG_TAG = "LedControlViewModel"
    }

    private val brightnessChangeFlow =
        MutableSharedFlow<Float>(
            replay = 0,
            // makes sure the UI thread doesn't hang if the bg coroutine processing the brightness is busy
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    private val speedChangeFlow =
        MutableSharedFlow<Float>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    private val _uiState = MutableStateFlow(LedControlUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val selectedDevice = connectionUseCases.observeSelectedDeviceUseCase().first()
            _uiState.update {
                it.copy(
                    selectedDevice = selectedDevice,
                )
            }
        }

        viewModelScope.launch {
            val initState = controlUseCases.observeControllerStateUseCase().first()
            _uiState.update { state ->
                state.copy(
                    isLedOn = initState?.isOn ?: false,
                    ledHexColor = initState?.let { "${it.red}${it.green}${it.blue}" } ?: "ffffff",
                    ledEffectValue = initState?.preset ?: STATIC_COLOR_VALUE,
                    effectPickerTxt = getEffectPickerText(
                        initState?.preset ?: STATIC_COLOR_VALUE,
                    ),
                    brightnessValue = initState?.brightness ?: 0f,
                    speedValue = initState?.speed ?: 0f,
                    totalActivePixels = initState?.totalActivePixels ?: 0,
                    icModel = initState?.icModel ?: IcModel.WS2811,
                    rgbSeq = initState?.rgbSeq ?: RgbSequence.RGB,
                )
            }
        }

        viewModelScope.launch {
            uiState.value.selectedDevice?.let { device ->
                controlUseCases
                    .getCustomColorsUseCase(device.address)
                    .collect { colors ->
                        _uiState.update { it.copy(customColorSlots = colors) }
                        Timber.tag(LOG_TAG).d("Saved colors: $colors")
                    }
            }
        }

        viewModelScope.launch {
            controlUseCases
                .observeBrightnessUseCase(brightnessChangeFlow)
                .collect { value ->
                    controlUseCases.changeBrightnessUseCase(value)
                }
        }

        viewModelScope.launch {
            controlUseCases
                .observeEffectSpeedUseCase(speedChangeFlow)
                .collect { value ->
                    controlUseCases
                        .setEffectSpeedUseCase(value)
                        .onFailure {
                            _uiState.update {
                                it.copy(
                                    infoMessage =
                                        UiText.StringResource(
                                            R.string.error_adjusting_effect_speed,
                                        ),
                                )
                            }
                        }
                }
        }

        viewModelScope.launch {
            uiState.value.selectedDevice?.let { device ->
                prefsUseCases.getFavEffectsUseCase(device.address).collect { effects ->
                    _uiState.update { it.copy(favoriteEffects = effects) }
                }
            }
        }
    }

    fun onEvent(event: LedControlUiEvent) {
        when (event) {
            is LedControlUiEvent.ToggleRenameDeviceDialog -> {
                _uiState.update { it.copy(showRenameDeviceDialog = event.show) }
            }
            is LedControlUiEvent.TogglePixelControlDialog -> {
                _uiState.update { it.copy(showPixelControlDialog = event.show) }
            }
            is LedControlUiEvent.ToggleHardwareConfigDialog -> {
                _uiState.update { it.copy(showHardwareConfigDialog = event.show) }
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
        _uiState.update {
            it.copy(
                ledHexColor = hexColor,
                ledEffectValue = STATIC_COLOR_VALUE,
                effectPickerTxt = getEffectPickerText(STATIC_COLOR_VALUE),
            )
        }
        viewModelScope.launch {
            controlUseCases.setLedColorUseCase(hexColor)
        }
    }

    fun saveCustomColor(
        slotId: Int,
        hexColor: String,
    ) {
        viewModelScope.launch {
            uiState.value.selectedDevice?.let { device ->
                val colorSlot = CustomColorSlot(slotId, hexColor)
                controlUseCases.saveCustomColorUseCase(device.address, colorSlot)
            }
        }
    }

    fun setLedEffect(value: Int) {
        viewModelScope.launch {
            val res = controlUseCases.setLedEffectUseCase(value)
            res
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            ledEffectValue = value,
                            effectPickerTxt = getEffectPickerText(value),
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            infoMessage =
                                UiText.StringResource(R.string.error_setting_effect),
                        )
                    }
                }
        }
    }

    fun setEffectSpeed(value: Float) {
        _uiState.update { it.copy(speedValue = value) }
        viewModelScope.launch {
            speedChangeFlow.emit(value)
        }
    }

    fun setEffectCycle() {
        _uiState.update {
            it.copy(
                ledEffectValue = EFFECT_CYCLE_VALUE,
                effectPickerTxt = getEffectPickerText(EFFECT_CYCLE_VALUE),
            )
        }
        viewModelScope.launch {
            controlUseCases.setEffectCycleUseCase()
        }
    }

    fun addFavEffect(value: Int) {
        if (value !in LED_EFFECT_RANGE) return

        viewModelScope.launch {
            uiState.value.selectedDevice?.let { device ->
                val res = prefsUseCases.addFavEffectUseCase(value, device.address)
                res.onFailure {
                    _uiState.update {
                        it.copy(
                            infoMessage =
                                UiText.StringResource(R.string.error_adding_effect_to_fav),
                        )
                    }
                }
            }
        }
    }

    fun removeFavEffect(value: Int) {
        viewModelScope.launch {
            uiState.value.selectedDevice?.let { device ->
                prefsUseCases
                    .removeFavEffectUseCase(value, device.address)
                    .onFailure {
                        _uiState.update {
                            it.copy(
                                infoMessage =
                                    UiText.StringResource(
                                        R.string.error_removing_effect_from_fav,
                                    ),
                            )
                        }
                    }
            }
        }
    }

    fun changeBrightness(value: Float) {
        _uiState.update { it.copy(brightnessValue = value) }
        viewModelScope.launch {
            brightnessChangeFlow.emit(value)
        }
    }

    fun setDeviceName(name: String) {
        viewModelScope.launch {
            val res = configUseCases.setDeviceNameUseCase(name)
            res
                .onSuccess {
                    _uiState.update {
                        it.copy(infoMessage = UiText.StringResource(R.string.device_renamed))
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            infoMessage =
                                UiText.StringResource(R.string.error_renaming_device),
                        )
                    }
                }
        }
    }

    fun setLedNum(pxlCount: Int) {
        viewModelScope.launch {
            val res = configUseCases.setLedNumUseCase(pxlCount)
            res
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            infoMessage = UiText.StringResource(R.string.active_pixel_set),
                            totalActivePixels = pxlCount,
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            infoMessage =
                                UiText.StringResource(R.string.error_setting_pixel_count),
                        )
                    }
                }
        }
    }

    fun setIcModel(icModel: IcModel) {
        _uiState.update { it.copy(icModel = icModel) }
        viewModelScope.launch {
            configUseCases.setIcModelUseCase(icModel)
        }
    }

    fun setRgbSequence(rgbSeq: RgbSequence) {
        _uiState.update { it.copy(rgbSeq = rgbSeq) }
        viewModelScope.launch {
            configUseCases.setRgbSequenceUseCase(rgbSeq)
        }
    }

    fun disconnectFromDevice() {
        viewModelScope.launch {
            connectionUseCases.disconnectUseCase()
        }
    }

    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    private fun getEffectPickerText(effectValue: Int): UiText =
        when (effectValue) {
            STATIC_COLOR_VALUE -> UiText.StringResource(R.string.static_color)
            EFFECT_CYCLE_VALUE -> UiText.StringResource(R.string.cycle)
            else -> UiText.DynamicString(effectValue.toString())
        }

    override fun onCleared() {
        // TODO here until figuring out persistent controller state
        connectionUseCases.disconnectUseCase()
    }
}
