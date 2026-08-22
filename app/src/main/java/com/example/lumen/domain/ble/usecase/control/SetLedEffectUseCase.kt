package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_EFFECT_COMMAND
import timber.log.Timber
import javax.inject.Inject

class SetLedEffectUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "SetLedEffectUseCase"
    }

    suspend operator fun invoke(value: Int) {
        val command = byteArrayOf(value.toByte()) + SET_EFFECT_COMMAND

        Timber.tag(LOG_TAG).d("Setting effect number: ${command.contentToString()}")

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            command
        )
    }
}
