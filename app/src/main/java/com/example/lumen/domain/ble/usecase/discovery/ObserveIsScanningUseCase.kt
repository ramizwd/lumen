package com.example.lumen.domain.ble.usecase.discovery

import android.util.Log
import com.example.lumen.domain.ble.BleScanController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsScanningUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    companion object {
        private const val LOG_TAG = "ObserveIsScanningUseCase"
    }

    operator fun invoke(): Flow<Boolean> {
        val isScanning = bleScanController.isScanning
        Log.d(LOG_TAG, "Is scanning: ${isScanning.value}")
        return isScanning
    }
}