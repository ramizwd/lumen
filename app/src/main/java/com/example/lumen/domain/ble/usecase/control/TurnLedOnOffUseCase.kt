package com.example.lumen.domain.ble.usecase.control

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.LED_OFF_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.LED_ON_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import timber.log.Timber
import javax.inject.Inject

class TurnLedOnOffUseCase @Inject constructor(
    private val bleGattController: BleGattController
){
    companion object {
        private const val LOG_TAG = "TurnLedOnOffUseCase"
    }

    suspend operator fun invoke(isLedOn: Boolean) {
        val command = if (isLedOn) {
            Timber.tag(LOG_TAG).d("Turn LED on")
            LED_ON_COMMAND
        } else {
            Timber.tag(LOG_TAG).d("Turn LED off")
            LED_OFF_COMMAND
        }

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            command
        )
    }
}