package com.example.lumen.domain.ble.usecase.connection

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.ConnectionState
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class ObserveConnectionStateUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    companion object {
        private const val LOG_TAG = "ObserveConnectionUseCase"
    }

    operator fun invoke(): Flow<ConnectionState> {
        Timber.tag(LOG_TAG).d("Connection state")
        return bleGattController.connectionState
    }
}
