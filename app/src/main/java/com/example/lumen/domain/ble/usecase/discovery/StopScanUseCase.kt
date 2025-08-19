package com.example.lumen.domain.ble.usecase.discovery

import android.util.Log
import com.example.lumen.domain.ble.BleScanController
import javax.inject.Inject

class StopScanUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    companion object {
        private const val LOG_TAG = "StopScanUseCase"
    }

    operator fun invoke() {
        Log.d(LOG_TAG, "Stop scan")
        bleScanController.stopScan()
    }
}