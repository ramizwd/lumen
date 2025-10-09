package com.example.lumen.domain.ble.usecase.control

import javax.inject.Inject

/**
 * Wrapper class for BLE control use cases
 */
data class ControlUseCases @Inject constructor(
    val turnLedOnOffUseCase: TurnLedOnOffUseCase,
    val setLedColorUseCase: SetLedColorUseCase,
    val changeBrightnessUseCase: ChangeBrightnessUseCase,
    val observeControllerStateUseCase: ObserveControllerStateUseCase,
)
