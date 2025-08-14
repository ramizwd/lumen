package com.example.lumen.domain.ble.usecase

import android.util.Log
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveScanResultsUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    companion object {
        private const val LOG_TAG = "ObserveScanResultsUseCase"
    }

    operator fun invoke(): Flow<List<BleDevice>> {
        val scanResults =  bleScanController.scanResults
        Log.d(LOG_TAG, "scan results: ${scanResults.value}")
        return scanResults
    }
}