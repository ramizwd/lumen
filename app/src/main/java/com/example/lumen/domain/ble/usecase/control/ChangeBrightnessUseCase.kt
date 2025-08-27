package com.example.lumen.domain.ble.usecase.control

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.utils.toBrightnessHex
import javax.inject.Inject

class ChangeBrightnessUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ChangeBrightnessUseCase"
    }

    suspend operator fun invoke(value: Float) {
        Log.d(LOG_TAG, "Value: $value")

        val commandHex = value.toBrightnessHex()
        Log.d(LOG_TAG, "Hex: $commandHex")

        bleGattController.writeCharacteristic(
            serviceUUID = SERVICE_UUID,
            charaUUID = CHARACTERISTIC_UUID,
            data = commandHex.hexToByteArray()
        )
    }
}