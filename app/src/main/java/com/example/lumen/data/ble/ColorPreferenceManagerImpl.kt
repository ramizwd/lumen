package com.example.lumen.data.ble

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.LedConstants.CUSTOM_COLOR_SLOTS_RANGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Manges custom colors and favorite effects with DataStore
 */
class ColorPreferenceManagerImpl(
    private val dataStore: DataStore<Preferences>,
) : ColorPreferenceManager {
    companion object {
        private const val LOG_TAG = "ColorPreferenceManagerImpl"
        private const val DEFAULT_COLOR = "ffffff"
    }

    override fun getCustomColors(deviceAddress: String): Flow<List<CustomColorSlot>> =
        dataStore.data
            .catch { e ->
                if (e is IOException) {
                    Timber.tag(LOG_TAG).e(e, "Error reading from DataStore")
                    emit(emptyPreferences())
                } else {
                    Timber.tag(LOG_TAG).e(e, "Unexpected error occurred")
                    emit(emptyPreferences())
                }
            }.map { preferences ->
                val key = getCustomColorsKey(deviceAddress)
                val colorSet = preferences[key] ?: emptySet()
                val colorMap = colorSet.associate { entry ->
                    val parts = entry.split(":")
                    parts[0].toInt() to parts[1]
                }
                CUSTOM_COLOR_SLOTS_RANGE.map { id ->
                    CustomColorSlot(id, colorMap[id] ?: DEFAULT_COLOR)
                }
            }

    override suspend fun saveCustomColor(
        deviceAddress: String,
        slot: CustomColorSlot,
    ) {
        val key = getCustomColorsKey(deviceAddress)

        try {
            dataStore.edit { preferences ->
                val current = preferences[key] ?: emptySet()
                val updated = current.filterNot { it.startsWith("${slot.id}:") } +
                    "${slot.id}:${slot.hexColor}"
                preferences[key] = updated.toSet()
            }
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error writing to DataStore")
        }
    }

    // Effects

    override fun getFavEffects(deviceAddress: String): Flow<Set<Int>> =
        dataStore.data
            .catch { e ->
                if (e is IOException) {
                    Timber.tag(LOG_TAG).e(e, "Error reading from DataStore")
                    emit(emptyPreferences())
                } else {
                    Timber.tag(LOG_TAG).e(e, "Unexpected error occurred")
                    emit(emptyPreferences())
                }
            }.map { preferences ->
                val key = getFavEffectsKey(deviceAddress)
                val effectStrings = preferences[key] ?: emptySet()
                effectStrings.mapNotNull { it.toIntOrNull() }.toSet()
            }

    override suspend fun addFavEffect(
        value: Int,
        deviceAddress: String,
    ) {
        try {
            val key = getFavEffectsKey(deviceAddress)
            dataStore.edit { preferences ->
                val current = preferences[key] ?: emptySet()
                preferences[key] = current + value.toString()
            }
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error writing to DataStore")
        }
    }

    override suspend fun removeFavEffect(
        value: Int,
        deviceAddress: String,
    ) {
        try {
            val key = getFavEffectsKey(deviceAddress)
            dataStore.edit { preferences ->
                val current = preferences[key] ?: emptySet()
                preferences[key] = current - value.toString()
            }
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error writing to DataStore")
        }
    }

    // Helpers

    private fun getCustomColorsKey(deviceAddress: String): Preferences.Key<Set<String>> =
        stringSetPreferencesKey("custom_colors_$deviceAddress")

    private fun getFavEffectsKey(deviceAddress: String): Preferences.Key<Set<String>> =
        stringSetPreferencesKey("fav_effects_$deviceAddress")
}
