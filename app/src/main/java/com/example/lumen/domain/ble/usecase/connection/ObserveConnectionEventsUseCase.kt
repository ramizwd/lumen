package com.example.lumen.domain.ble.usecase.connection

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.ConnectionResult
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class ObserveConnectionEventsUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ObserveConnectionEventsUseCase"
    }

    operator fun invoke(): Flow<ConnectionResult> {
        val connectionEvent = bleGattController.connectionEvents
        Timber.tag(LOG_TAG).d("Connection event: $connectionEvent")
        return connectionEvent
    }
}