package com.example.lumen.domain.ble.usecase.discovery

import com.example.lumen.domain.ble.BleScanController
import timber.log.Timber
import javax.inject.Inject

class StartScanUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    companion object {
        private const val LOG_TAG = "StartScanUseCase"
    }

    suspend operator fun invoke() {
        Timber.tag(LOG_TAG).d("Start scan")
        bleScanController.startScan()
    }
}