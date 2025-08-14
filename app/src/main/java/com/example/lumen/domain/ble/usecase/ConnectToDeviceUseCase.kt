package com.example.lumen.domain.ble.usecase

import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.BleScanController
import javax.inject.Inject

class ConnectToDeviceUseCase @Inject constructor(
    private val bleGattController: BleGattController,
    private val bleScanController: BleScanController
) {
    companion object ConnectToDeviceUseCase {
        private const val LOG_TAG = "ConnectToDeviceUseCase"
    }

    operator fun invoke(address: String) {
        Log.d(LOG_TAG, "Connect to device ($address)")
        bleScanController.stopScan()
        bleGattController.connect(address)
    }
}