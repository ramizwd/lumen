package com.example.lumen.data.ble

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.lumen.domain.ble.BleDevicePreferenceManager
import com.example.lumen.domain.ble.model.DeviceListType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Handles favoring BLE devices and selected device list preferences
 */
class BleDevicePreferenceManagerImpl(
    private val dataStore: DataStore<Preferences>
): BleDevicePreferenceManager {

    companion object {
        private const val LOG_TAG = "BleDevicePreferenceManagerImpl"
        private val FAVORITE_DEVICES_KEY = stringSetPreferencesKey("favorite_devices")
        private val SELECTED_LIST_KEY = stringPreferencesKey("selected_device_list")
    }

    override fun getFavDeviceAddresses(): Flow<Set<String>> {
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
                preferences[FAVORITE_DEVICES_KEY] ?: emptySet()
        }
    }

    override suspend fun addFavDeviceAddress(address: String) {
        try {
            dataStore.edit { preferences ->
                val currFavAddresses= preferences[FAVORITE_DEVICES_KEY] ?: emptySet()
                preferences[FAVORITE_DEVICES_KEY] = currFavAddresses + address
            }
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error writing to DataStore")
        }
    }

    override suspend fun removeFavDeviceAddress(address: String) {
        try {
            dataStore.edit { preferences ->
                val currFavAddresses = preferences[FAVORITE_DEVICES_KEY] ?: emptySet()
                preferences[FAVORITE_DEVICES_KEY] = currFavAddresses - address
            }
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error writing to DataStore")
        }
    }

    override fun getDeviceListPreference(): Flow<DeviceListType> {
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
                val listTypeString = preferences[SELECTED_LIST_KEY] ?: DeviceListType.ALL_DEVICES.name
                DeviceListType.valueOf(listTypeString)
        }
    }

    override suspend fun saveDeviceListPreference(listType: DeviceListType) {
        try {
            dataStore.edit { preferences ->
                preferences[SELECTED_LIST_KEY] = listType.name
            }
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Error writing to DataStore")
        }
    }
}