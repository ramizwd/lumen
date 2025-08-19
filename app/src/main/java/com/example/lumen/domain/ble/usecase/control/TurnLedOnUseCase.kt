package com.example.lumen.domain.ble.usecase.control

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.LED_ON_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import javax.inject.Inject

class TurnLedOnUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "TurnLedOnUseCase"
    }

    operator fun invoke() {
        Log.d(LOG_TAG, "Turn LED on")

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            LED_ON_COMMAND
        )
    }
}