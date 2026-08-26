package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_CYCLE_COMMAND
import javax.inject.Inject

class SetEffectCycleUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    suspend operator fun invoke() {
        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            SET_CYCLE_COMMAND,
        )
    }
}
