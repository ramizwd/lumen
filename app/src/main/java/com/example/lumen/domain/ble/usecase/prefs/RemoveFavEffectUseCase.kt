package com.example.lumen.domain.ble.usecase.prefs

import com.example.lumen.domain.ble.ColorPreferenceManager
import javax.inject.Inject

class RemoveFavEffectUseCase @Inject constructor(
    private val colorPreferenceManager: ColorPreferenceManager,
) {
    suspend operator fun invoke(
        value: Int,
        deviceAddress: String,
    ): Result<Unit> =
        runCatching {
            colorPreferenceManager.removeFavEffect(value, deviceAddress)
        }
}
