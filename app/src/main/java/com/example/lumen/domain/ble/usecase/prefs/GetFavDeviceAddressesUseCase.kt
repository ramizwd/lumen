package com.example.lumen.domain.ble.usecase.prefs

import com.example.lumen.domain.ble.BleDevicePreferenceManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavDeviceAddressesUseCase @Inject constructor(
    private val bleDevicePreferenceManager: BleDevicePreferenceManager
){
    operator fun invoke(): Flow<Set<String>> {
        return bleDevicePreferenceManager.getFavDeviceAddresses()
    }
}