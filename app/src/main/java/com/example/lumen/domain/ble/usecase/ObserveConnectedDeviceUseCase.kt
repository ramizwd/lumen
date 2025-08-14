package com.example.lumen.domain.ble.usecase

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectedDeviceUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    operator fun invoke(): Flow<BleDevice?> {
        return bleGattController.connectedDevice
    }
}