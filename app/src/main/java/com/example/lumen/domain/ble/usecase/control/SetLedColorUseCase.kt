package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.utils.hexToColorCommandBytes
import javax.inject.Inject

class SetLedColorUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {

    suspend operator fun invoke(hexColor: String) {
        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            hexColor.hexToColorCommandBytes()
        )
    }
}