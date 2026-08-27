package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_SPEED_COMMAND
import com.example.lumen.domain.ble.model.LedConstants.EFFECT_SPEED_RANGE
import timber.log.Timber
import javax.inject.Inject

class SetEffectSpeedUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    companion object {
        private const val LOG_TAG = "SetEffectSpeedUseCase"
    }

    suspend operator fun invoke(value: Float): Result<Unit> =
        try {
            val coercedValue = value.coerceIn(EFFECT_SPEED_RANGE)

            val command = byteArrayOf(
                coercedValue.toInt().toByte(),
                SET_SPEED_COMMAND[0],
                SET_SPEED_COMMAND[1],
                SET_SPEED_COMMAND[2],
            )

            Timber
                .tag(LOG_TAG)
                .d("Setting speed to $coercedValue: ${command.contentToString()}")

            bleGattController.writeCharacteristic(
                SERVICE_UUID,
                CHARACTERISTIC_UUID,
                command,
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Failed to set speed")
            Result.failure(e)
        }
}
