package com.example.lumen.domain.ble.usecase.control

import javax.inject.Inject

/**
 * Wrapper class for BLE controls use cases
 */
data class ControlUseCases @Inject constructor(
    val turnLedOnUseCase: TurnLedOnUseCase,
    val turnLedOffUseCase: TurnLedOffUseCase,
)
