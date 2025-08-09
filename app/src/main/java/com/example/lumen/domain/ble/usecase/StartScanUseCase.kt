package com.example.lumen.domain.ble.usecase

import android.util.Log
import com.example.lumen.domain.ble.BleController
import javax.inject.Inject

class StartScanUseCase @Inject constructor(
    private val bleController: BleController
) {
    companion object {
        private const val LOG_TAG = "StartScanUseCase"
    }

    operator fun invoke() {
        Log.d(LOG_TAG, "Start scan")
        bleController.startScan()
    }
}