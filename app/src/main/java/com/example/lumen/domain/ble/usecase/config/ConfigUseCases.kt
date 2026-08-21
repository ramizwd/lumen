package com.example.lumen.domain.ble.usecase.config

import javax.inject.Inject

/**
 * Wrapper class for device configuration use cases
 */
data class ConfigUseCases @Inject constructor(
    val setDeviceNameUseCase: SetDeviceNameUseCase,
    val setLedNumUseCase: SetLedNumUseCase,
    val setIcModelUseCase: SetIcModelUseCase,
    val setRgbSequenceUseCase: SetRgbSequenceUseCase,
)
