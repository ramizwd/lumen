package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.StaticLedColors
import javax.inject.Inject

class ChangeStaticColorUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {

    suspend operator fun invoke(color: StaticLedColors){
        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            color.commandBytes
        )
    }
}