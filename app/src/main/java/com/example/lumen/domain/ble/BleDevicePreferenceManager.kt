package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.DeviceListType
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing BLE device and device list preferences
 */
interface BleDevicePreferenceManager {
    fun getFavDeviceAddresses(): Flow<Set<String>>
    suspend fun addFavDeviceAddress(address: String)
    suspend fun removeFavDeviceAddress(address: String)

    fun getDeviceListPreference(): Flow<DeviceListType>
    suspend fun saveDeviceListPreference(listType: DeviceListType)
}