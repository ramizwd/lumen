package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.CustomColorSlot
import javax.inject.Inject

class SaveCustomColorUseCase @Inject constructor(
    private val colorPreferenceManager: ColorPreferenceManager
) {
    suspend operator fun invoke(deviceAddress: String, slot: CustomColorSlot) {
        colorPreferenceManager.saveCustomColor(deviceAddress, slot)
    }
}