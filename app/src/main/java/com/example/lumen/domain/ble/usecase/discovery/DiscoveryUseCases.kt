package com.example.lumen.domain.ble.usecase.discovery

import javax.inject.Inject

/**
 * Wrapper class for BLE scanning and scan results use cases
 */
data class DiscoveryUseCases @Inject constructor(
    val observeIsScanningUseCase: ObserveIsScanningUseCase,
    val observeScanResultsUseCase: ObserveScanResultsUseCase,
    val observeScanErrorsUseCase: ObserveScanErrorsUseCase,
    val startScanUseCase: StartScanUseCase,
    val stopScanUseCase: StopScanUseCase,
)
