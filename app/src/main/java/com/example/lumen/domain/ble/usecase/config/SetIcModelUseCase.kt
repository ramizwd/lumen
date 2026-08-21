package com.example.lumen.domain.ble.usecase.config

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_IC_MODEL_COMMAND
import com.example.lumen.domain.ble.model.IcModel
import timber.log.Timber
import javax.inject.Inject

class SetIcModelUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    companion object {
        private const val LOG_TAG = "SetIcModelUseCase"
    }

    suspend operator fun invoke(icModel: IcModel) {
        val commandBytes = byteArrayOf(icModel.value) + SET_IC_MODEL_COMMAND

        Timber
            .tag(LOG_TAG)
            .d("Setting IC model to $icModel: ${commandBytes.contentToString()}")

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            commandBytes,
        )
    }
}
