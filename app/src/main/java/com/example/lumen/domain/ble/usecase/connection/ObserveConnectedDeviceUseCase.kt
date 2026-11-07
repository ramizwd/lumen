package com.example.lumen.domain.ble.usecase.connection

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the device that is currently connected
 */
class ObserveConnectedDeviceUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    operator fun invoke(): Flow<BleDevice?> = bleGattController.connectedDevice
}
