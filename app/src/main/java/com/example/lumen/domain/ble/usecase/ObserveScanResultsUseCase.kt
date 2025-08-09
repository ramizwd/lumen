package com.example.lumen.domain.ble.usecase

import android.util.Log
import com.example.lumen.domain.ble.BleController
import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveScanResultsUseCase @Inject constructor(
    private val bleController: BleController
) {
    companion object {
        private const val LOG_TAG = "ObserveScanResultsUseCase"
    }

    operator fun invoke(): Flow<List<BleDevice>> {
        val scanResults =  bleController.scanResults
        Log.d(LOG_TAG, "scan results: $scanResults")
        return scanResults
    }
}