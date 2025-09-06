package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.utils.toBrightnessCommandBytes
import timber.log.Timber
import javax.inject.Inject

class ChangeBrightnessUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ChangeBrightnessUseCase"
    }

    suspend operator fun invoke(value: Float) {
        Timber.tag(LOG_TAG).d("Value: $value")

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            value.toBrightnessCommandBytes()
        )
    }
}