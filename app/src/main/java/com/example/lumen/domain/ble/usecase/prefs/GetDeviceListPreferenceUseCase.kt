package com.example.lumen.domain.ble.usecase.prefs

import com.example.lumen.domain.ble.BleDevicePreferenceManager
import com.example.lumen.domain.ble.model.DeviceListType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeviceListPreferenceUseCase @Inject constructor(
    private val bleDevicePreferenceManager: BleDevicePreferenceManager
) {
    operator fun invoke(): Flow<DeviceListType> {
        return bleDevicePreferenceManager.getDeviceListPreference()
    }
}