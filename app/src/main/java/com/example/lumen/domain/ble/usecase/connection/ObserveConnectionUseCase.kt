package com.example.lumen.domain.ble.usecase.connection

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.ConnectionState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectionUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ObserveConnectionUseCase"
    }

    operator fun invoke(): Flow<ConnectionState> {
        Log.d(LOG_TAG, "Connection state")
        return bleGattController.connectionState
    }
}
