package com.example.lumen.domain.ble.usecase.prefs

import com.example.lumen.domain.ble.BleDevicePreferenceManager
import com.example.lumen.domain.ble.model.DeviceListType
import javax.inject.Inject

class SaveDeviceListPreferenceUseCase @Inject constructor(
    private val bleDevicePreferenceManager: BleDevicePreferenceManager
){
    suspend operator fun invoke(listType: DeviceListType) {
        bleDevicePreferenceManager.saveDeviceListPreference(listType)
    }
}