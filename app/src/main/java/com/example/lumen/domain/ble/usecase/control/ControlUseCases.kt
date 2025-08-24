package com.example.lumen.domain.ble.usecase.control

import javax.inject.Inject

/**
 * Wrapper class for BLE control use cases
 */
data class ControlUseCases @Inject constructor(
    val turnLedOnOffUseCase: TurnLedOnOffUseCase,
    val changeStaticColorUseCase: ChangeStaticColorUseCase,
    val changeBrightnessUseCase: ChangeBrightnessUseCase,
    val observeControllerStateUseCase: ObserveControllerStateUseCase,
)
