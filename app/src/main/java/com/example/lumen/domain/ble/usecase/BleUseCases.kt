package com.example.lumen.domain.ble.usecase

import javax.inject.Inject

/**
 * Wrapper class for BLE use cases
 */
data class BleUseCases @Inject constructor(
    val observeIsScanningUseCase: ObserveIsScanningUseCase,
    val observeScanResultsUseCase: ObserveScanResultsUseCase,
    val startScanUseCase: StartScanUseCase,
    val stopScanUseCase: StopScanUseCase,
    val connectToDeviceUseCase: ConnectToDeviceUseCase,
    val observeConnectionUseCase: ObserveConnectionUseCase,
    val disconnectUseCase: DisconnectUseCase,
)
