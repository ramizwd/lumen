package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.CustomColorSlot
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing custom set colors and favorite effects
 */
interface ColorPreferenceManager {
    fun getCustomColors(deviceAddress: String): Flow<List<CustomColorSlot>>

    suspend fun saveCustomColor(
        deviceAddress: String,
        slot: CustomColorSlot,
    )

    fun getFavEffects(deviceAddress: String): Flow<Set<Int>>

    suspend fun addFavEffect(
        value: Int,
        deviceAddress: String,
    )

    suspend fun removeFavEffect(
        value: Int,
        deviceAddress: String,
    )
}
