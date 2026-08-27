package com.example.lumen.domain.ble.usecase.prefs

import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.LedConstants.LED_EFFECT_RANGE
import timber.log.Timber
import javax.inject.Inject

class AddFavEffectUseCase @Inject constructor(
    private val colorPreferenceManager: ColorPreferenceManager,
) {
    companion object {
        private const val LOG_TAG = "AddFavEffectUseCase"
    }

    suspend operator fun invoke(
        value: Int,
        deviceAddress: String,
    ): Result<Unit> =
        try {
            require(value in LED_EFFECT_RANGE) {
                "Effect value must be within the range"
            }

            colorPreferenceManager.addFavEffect(value, deviceAddress)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Failed to add effect to fav")
            Result.failure(e)
        }
}
