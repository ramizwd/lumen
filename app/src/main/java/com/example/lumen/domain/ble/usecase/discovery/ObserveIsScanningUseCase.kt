package com.example.lumen.domain.ble.usecase.discovery

import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.ScanState
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class ObserveIsScanningUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    companion object {
        private const val LOG_TAG = "ObserveIsScanningUseCase"
    }

    operator fun invoke(): Flow<ScanState> {
        val isScanning = bleScanController.scanState
        Timber.tag(LOG_TAG).d("Is scanning: ${isScanning.value}")
        return isScanning
    }
}