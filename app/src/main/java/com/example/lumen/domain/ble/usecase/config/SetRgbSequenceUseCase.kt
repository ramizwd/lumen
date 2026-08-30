package com.example.lumen.domain.ble.usecase.config

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_RGB_SEQ_COMMAND
import com.example.lumen.domain.ble.model.RgbSequence
import timber.log.Timber
import javax.inject.Inject

class SetRgbSequenceUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    companion object {
        private const val LOG_TAG = "SetRgbSequenceUseCase"
    }

    suspend operator fun invoke(rgbSeq: RgbSequence): Result<Unit> {
        val commandBytes = byteArrayOf(
            rgbSeq.value,
            SET_RGB_SEQ_COMMAND[0],
            SET_RGB_SEQ_COMMAND[1],
            SET_RGB_SEQ_COMMAND[2],
        )

        Timber
            .tag(LOG_TAG)
            .d("Setting RGB sequence to $rgbSeq: ${commandBytes.contentToString()}")

        return bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            commandBytes,
        )
    }
}
