package com.example.lumen.data.ble

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.lumen.domain.ble.BleDevicePreferenceManager
import com.example.lumen.domain.ble.model.DeviceListType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Handles favoring BLE devices and selected device list preferences
 */
class BleDevicePreferenceManagerImpl(
    private val dataStore: DataStore<Preferences>
): BleDevicePreferenceManager {

    companion object {
        private val FAVORITE_DEVICES_KEY = stringSetPreferencesKey("favorite_devices")
        private val SELECTED_LIST_KEY = stringPreferencesKey("selected_device_list")
    }

    override fun getFavDeviceAddresses(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[FAVORITE_DEVICES_KEY] ?: emptySet()
        }
    }

    override suspend fun addFavDeviceAddress(address: String) {
        dataStore.edit { preferences ->
            val currFavAddresses= preferences[FAVORITE_DEVICES_KEY] ?: emptySet()
            preferences[FAVORITE_DEVICES_KEY] = currFavAddresses + address
        }
    }

    override suspend fun removeFavDeviceAddress(address: String) {
        dataStore.edit { preferences ->
            val currFavAddresses = preferences[FAVORITE_DEVICES_KEY] ?: emptySet()
            preferences[FAVORITE_DEVICES_KEY] = currFavAddresses - address
        }
    }

    override fun getDeviceListPreference(): Flow<DeviceListType> {
        return dataStore.data.map { preferences ->
            val listTypeString = preferences[SELECTED_LIST_KEY] ?: DeviceListType.ALL_DEVICES.name
            DeviceListType.valueOf(listTypeString)
        }
    }

    override suspend fun saveDeviceListPreference(listType: DeviceListType) {
        dataStore.edit { preferences ->
            preferences[SELECTED_LIST_KEY] = listType.name
        }
    }
}