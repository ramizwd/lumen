package com.example.lumen.domain.ble.usecase.control

import javax.inject.Inject

/**
 * Wrapper class for BLE control use cases
 */
data class ControlUseCases @Inject constructor(
    val turnLedOnOffUseCase: TurnLedOnOffUseCase,
    val setLedColorUseCase: SetLedColorUseCase,
    val changeBrightnessUseCase: ChangeBrightnessUseCase,
    val observeBrightnessUseCase: ObserveBrightnessUseCase,
    val observeEffectSpeedUseCase: ObserveEffectSpeedUseCase,
    val observeControllerStateUseCase: ObserveControllerStateUseCase,
    val saveCustomColorUseCase: SaveCustomColorUseCase,
    val getCustomColorsUseCase: GetCustomColorsUseCase,
    val setLedEffectUseCase: SetLedEffectUseCase,
    val setEffectSpeedUseCase: SetEffectSpeedUseCase,
    val setEffectCycleUseCase: SetEffectCycleUseCase,
    val changeWhiteLedBrightnessUseCase: ChangeWhiteLedBrightnessUseCase,
    val observeWhiteLedBrightnessUseCase: ObserveWhiteLedBrightnessUseCase,
)
