package com.example.lumen.domain.ble.usecase.control

import javax.inject.Inject

/**
 * Wrapper class for BLE control use cases
 */
data class ControlUseCases @Inject constructor(
    val turnLedOnOffUseCase: TurnLedOnOffUseCase,
    val setPresetColorUseCase: SetPresetColorUseCase,
    val setHsvColorUseCase: SetHsvColorUseCase,
    val changeBrightnessUseCase: ChangeBrightnessUseCase,
    val observeControllerStateUseCase: ObserveControllerStateUseCase,
)
