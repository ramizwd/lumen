package com.example.lumen.data.ble

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.CustomColorSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Manges custom colors with DataStore
 */
class ColorPreferenceManagerImpl(
    private val dataStore: DataStore<Preferences>
): ColorPreferenceManager {

    companion object {
        private const val LOG_TAG = "ColorPreferenceManagerImpl"
        private const val DEFAULT_COLOR = "ffffff"
        private const val NUM_OF_COLOR_SLOTS = 7
        private const val MIN_COLOR_SLOT_ID = 1
    }

    override fun getCustomColors(deviceAddress: String): Flow<List<CustomColorSlot>> {
        return dataStore.data
            .catch { e ->
                if (e is IOException){
                    Timber.tag(LOG_TAG).e(e, "Error reading from DataStore")
                    emit(emptyPreferences())
                } else {
                    Timber.tag(LOG_TAG).e(e, "Unexpected error occurred")
                    emit(emptyPreferences())
                }
            }
            .map { preferences ->
                (MIN_COLOR_SLOT_ID..NUM_OF_COLOR_SLOTS).map { id ->
                    val hexColor = preferences[getSlotKey(deviceAddress, id)] ?:
                    DEFAULT_COLOR
                    CustomColorSlot(id, hexColor)
                }
            }
    }

    override suspend fun saveCustomColor(deviceAddress: String, slot: CustomColorSlot) {
        require(slot.id in MIN_COLOR_SLOT_ID..NUM_OF_COLOR_SLOTS)
        {"Slot ID must be between $MIN_COLOR_SLOT_ID and $NUM_OF_COLOR_SLOTS"}
        val key = getSlotKey(deviceAddress, slot.id)

        try {
            dataStore.edit { preferences ->
                preferences[key] = slot.hexColor
            }
        } catch(e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error writing to DataStore")
        }
    }

    private fun getSlotKey(deviceAddress: String, slotId: Int): Preferences.Key<String> {
        return stringPreferencesKey("${deviceAddress}_slot_${slotId}_color")
    }
}