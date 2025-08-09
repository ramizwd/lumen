package com.example.lumen.domain.ble.usecase

import android.util.Log
import com.example.lumen.domain.ble.BleController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsScanningUseCase @Inject constructor(
    private val bleController: BleController
) {
    companion object {
        private const val LOG_TAG = "ObserveIsScanningUseCase"
    }

    operator fun invoke(): Flow<Boolean> {
        val isScanning = bleController.isScanning
        Log.d(LOG_TAG, "Is scanning: $isScanning")
        return isScanning
    }
}