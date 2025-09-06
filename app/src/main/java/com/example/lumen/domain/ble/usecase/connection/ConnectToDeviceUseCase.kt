package com.example.lumen.domain.ble.usecase.connection

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.BleDevice
import timber.log.Timber
import javax.inject.Inject

class ConnectToDeviceUseCase @Inject constructor(
    private val bleGattController: BleGattController,
    private val bleScanController: BleScanController
) {
    companion object ConnectToDeviceUseCase {
        private const val LOG_TAG = "ConnectToDeviceUseCase"
    }

    suspend operator fun invoke(selectedDevice: BleDevice?) {
        Timber.tag(LOG_TAG).d("Connect to device ($selectedDevice)")
        bleScanController.stopScan()
        bleGattController.connect(selectedDevice)
    }
}