package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_EFFECT_COMMAND
import com.example.lumen.domain.ble.model.LedConstants.LED_EFFECT_RANGE
import timber.log.Timber
import javax.inject.Inject

class SetLedEffectUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    companion object {
        private const val LOG_TAG = "SetLedEffectUseCase"
    }

    suspend operator fun invoke(value: Int): Result<Unit> =
        try {
            require(value in LED_EFFECT_RANGE) {
                "Effect value must be within the range"
            }

            val command = byteArrayOf(
                value.toByte(),
                SET_EFFECT_COMMAND[0],
                SET_EFFECT_COMMAND[1],
                SET_EFFECT_COMMAND[3],
            )

            Timber.tag(LOG_TAG).d("Setting effect number: ${command.contentToString()}")

            bleGattController.writeCharacteristic(
                SERVICE_UUID,
                CHARACTERISTIC_UUID,
                command,
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Failed to set LED effect")
            Result.failure(e)
        }
}
