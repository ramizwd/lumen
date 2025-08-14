package com.example.lumen.domain.ble.usecase

import com.example.lumen.domain.ble.BleGattController
import javax.inject.Inject

class DisconnectUseCase @Inject constructor(
    private val bleGattController: BleGattController
){
    operator fun invoke() {
        bleGattController.disconnect()
    }
}