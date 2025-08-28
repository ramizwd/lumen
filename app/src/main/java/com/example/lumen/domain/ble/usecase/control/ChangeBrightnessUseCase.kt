package com.example.lumen.domain.ble.usecase.control

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.utils.toBrightnessCommandBytes
import javax.inject.Inject

class ChangeBrightnessUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ChangeBrightnessUseCase"
    }

    suspend operator fun invoke(value: Float) {
        Log.d(LOG_TAG, "Value: $value")

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            value.toBrightnessCommandBytes()
        )
    }
}