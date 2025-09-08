package com.example.lumen.domain.ble.usecase.common

import com.example.lumen.domain.ble.BluetoothStateManager
import com.example.lumen.domain.ble.model.BluetoothState
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class ObserveBluetoothStateUseCase @Inject constructor(
    private val bluetoothStateDataSource: BluetoothStateManager
) {
    companion object {
        private const val LOG_TAG = "ObserveBluetoothStateUseCase"
    }

    operator fun invoke(): Flow<BluetoothState> {
        val bluetoothState = bluetoothStateDataSource.observeBluetoothState()
        Timber.Forest.tag(LOG_TAG).d("BT state: $bluetoothState")
        return bluetoothState
    }
}