package com.example.lumen.domain.ble.usecase

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.LED_OFF_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import javax.inject.Inject

class TurnLedOffUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "TurnLedOffUseCase"
    }

    operator fun invoke() {
        Log.d(LOG_TAG, "Turn LED off")

        bleGattController.writeCharacteristic(
            SERVICE_UUID,
            CHARACTERISTIC_UUID,
            LED_OFF_COMMAND
        )
    }
}