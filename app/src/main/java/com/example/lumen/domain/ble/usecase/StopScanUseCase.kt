package com.example.lumen.domain.ble.usecase

import android.util.Log
import com.example.lumen.domain.ble.BleController
import javax.inject.Inject

class StopScanUseCase @Inject constructor(
    private val bleController: BleController
) {
    companion object {
        private const val LOG_TAG = "StopScanUseCase"
    }

    operator fun invoke() {
        Log.d(LOG_TAG, "Stop scan")
        bleController.stopScan()
    }
}