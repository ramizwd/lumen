package com.example.lumen.domain.ble.usecase.prefs

import com.example.lumen.domain.ble.ColorPreferenceManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavEffectsUseCase @Inject constructor(
    private val colorPreferenceManager: ColorPreferenceManager,
) {
    operator fun invoke(deviceAddress: String): Flow<Set<Int>> =
        colorPreferenceManager.getFavEffects(deviceAddress)
}
