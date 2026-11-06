package com.example.lumen.domain.ble.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.WHITE_BRIGHTNESS_COMMAND
import com.example.lumen.domain.ble.model.LedConstants.BRIGHTNESS_RANGE
import com.example.lumen.domain.ble.model.LedSetting
import timber.log.Timber
import javax.inject.Inject

class ChangeWhiteLedBrightnessUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    companion object {
        private const val LOG_TAG = "ChangeWhiteLedBrightnessUseCase"
    }

    suspend operator fun invoke(value: Float): Result<Unit> {
        val brightness = value.coerceIn(BRIGHTNESS_RANGE)

        val command = byteArrayOf(
            brightness.toInt().toByte(),
            WHITE_BRIGHTNESS_COMMAND[0],
            WHITE_BRIGHTNESS_COMMAND[1],
            WHITE_BRIGHTNESS_COMMAND[2],
        )

        Timber
            .tag(LOG_TAG)
            .d("Setting white brightness to $brightness: ${command.contentToString()}")

        return bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            command,
            Pair(brightness, LedSetting.WHITE_LED_BRIGHTNESS),
        )
    }
}
