package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.CustomColorSlot
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing custom set colors
 */
interface ColorPreferenceManager {
    fun getCustomColors(deviceAddress: String): Flow<List<CustomColorSlot>>
    suspend fun saveCustomColor(deviceAddress: String, slot: CustomColorSlot)
}