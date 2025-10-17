package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.CustomColorSlot
import timber.log.Timber
import javax.inject.Inject

class SaveCustomColorUseCase @Inject constructor(
    private val colorPreferenceManager: ColorPreferenceManager
) {
    companion object {
        private const val LOG_TAG = "SaveCustomColorUseCase"
    }

    suspend operator fun invoke(deviceAddress: String, slot: CustomColorSlot) {
        try {
            colorPreferenceManager.saveCustomColor(deviceAddress, slot)
        } catch (e: IllegalArgumentException) {
            Timber.tag(LOG_TAG).e(e, "Error saving color")
        }
    }
}