package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.WHITE_BRIGHTNESS_COMMAND
import com.example.lumen.domain.ble.model.LedConstants.BRIGHTNESS_RANGE
import timber.log.Timber
import javax.inject.Inject

class ChangeWhiteLedBrightnessUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    companion object {
        private const val LOG_TAG = "ChangeWhiteLedBrightnessUseCase"
    }

    suspend operator fun invoke(value: Float): Result<Unit> {
        val brightnessInt = value.coerceIn(BRIGHTNESS_RANGE).toInt()

        val command = byteArrayOf(
            brightnessInt.toByte(),
            WHITE_BRIGHTNESS_COMMAND[0],
            WHITE_BRIGHTNESS_COMMAND[1],
            WHITE_BRIGHTNESS_COMMAND[2],
        )

        Timber
            .tag(LOG_TAG)
            .d("Setting speed to $brightnessInt: ${command.contentToString()}")

        return bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            command,
        )
    }
}
