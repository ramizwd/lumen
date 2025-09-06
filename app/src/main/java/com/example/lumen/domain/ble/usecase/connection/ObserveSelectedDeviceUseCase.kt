package com.example.lumen.domain.ble.usecase.connection

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the device that is selected and successfully connected
 */
class ObserveSelectedDeviceUseCase @Inject constructor(
    private val bleGattController: BleGattController
) {
    operator fun invoke(): Flow<BleDevice?> {
        return bleGattController.selectedDevice
    }
}