package com.example.lumen.domain.ble.usecase.connection

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.ConnectionResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectionEventsUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ObserveConnectionEventsUseCase"
    }

    operator fun invoke(): Flow<ConnectionResult> {
        val connectionEvent = bleGattController.connectionEvents
        Log.i(LOG_TAG, "Connection event: $connectionEvent")
        return connectionEvent
    }
}