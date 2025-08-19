package com.example.lumen.domain.ble.usecase.discovery

import android.util.Log
import com.example.lumen.domain.ble.BleScanController
import javax.inject.Inject

class StartScanUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    companion object {
        private const val LOG_TAG = "StartScanUseCase"
    }

    suspend operator fun invoke() {
        Log.d(LOG_TAG, "Start scan")
        bleScanController.startScan()
    }
}