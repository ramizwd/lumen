package com.example.lumen.domain.ble.usecase.prefs

import com.example.lumen.domain.ble.BleDevicePreferenceManager
import javax.inject.Inject

class RemoveFavDeviceAddressUseCase @Inject constructor(
    private val bleDevicePreferenceManager: BleDevicePreferenceManager
){
    suspend operator fun invoke(address: String) {
        bleDevicePreferenceManager.removeFavDeviceAddress(address)
    }
}