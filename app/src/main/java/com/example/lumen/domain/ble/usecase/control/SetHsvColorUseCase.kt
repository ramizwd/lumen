package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.COLOR_SUFFIX_HEX
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import javax.inject.Inject

class SetHsvColorUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {

    suspend operator fun invoke(hexColor: String) {
        val commandHex = hexColor + COLOR_SUFFIX_HEX

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            commandHex.hexToByteArray()
        )
    }
}