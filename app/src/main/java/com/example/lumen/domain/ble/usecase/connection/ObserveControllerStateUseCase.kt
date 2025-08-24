package com.example.lumen.domain.ble.usecase.connection

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.LedControllerState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveControllerStateUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ObserveControllerStateUseCase"
    }

    operator fun invoke(): Flow<LedControllerState?> {
        val controllerState = bleGattController.ledControllerState
        Log.d(LOG_TAG, "Controller state: $controllerState")

        return controllerState
    }
}