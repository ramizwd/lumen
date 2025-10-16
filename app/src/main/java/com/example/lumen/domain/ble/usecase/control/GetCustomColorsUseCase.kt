package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.CustomColorSlot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCustomColorsUseCase @Inject constructor(
    private val colorPreferenceManager: ColorPreferenceManager
) {
    operator fun invoke(deviceAddress: String): Flow<List<CustomColorSlot>> {
        return colorPreferenceManager.getCustomColors(deviceAddress)
    }
}