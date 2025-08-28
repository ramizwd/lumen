package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.PresetLedColors
import javax.inject.Inject

class SetPresetColorUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {

    suspend operator fun invoke(color: PresetLedColors){
        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            color.commandBytes
        )
    }
}