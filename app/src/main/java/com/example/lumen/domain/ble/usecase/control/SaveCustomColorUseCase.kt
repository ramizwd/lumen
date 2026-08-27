package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.LedConstants.CUSTOM_COLOR_SLOTS_RANGE
import timber.log.Timber
import javax.inject.Inject

class SaveCustomColorUseCase @Inject constructor(
    private val colorPreferenceManager: ColorPreferenceManager,
) {
    companion object {
        private const val LOG_TAG = "SaveCustomColorUseCase"
    }

    suspend operator fun invoke(
        deviceAddress: String,
        slot: CustomColorSlot,
    ) {
        try {
            require(slot.id in CUSTOM_COLOR_SLOTS_RANGE) {
                "Slot ID must be within range $CUSTOM_COLOR_SLOTS_RANGE"
            }
            colorPreferenceManager.saveCustomColor(deviceAddress, slot)
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error saving color")
        }
    }
}
