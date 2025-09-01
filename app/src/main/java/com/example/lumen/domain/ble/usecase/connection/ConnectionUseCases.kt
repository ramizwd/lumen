package com.example.lumen.domain.ble.usecase.connection

import javax.inject.Inject

/**
 * Wrapper class for BLE connection use cases
 */
data class ConnectionUseCases @Inject constructor(
    val connectToDeviceUseCase: ConnectToDeviceUseCase,
    val observeConnectionUseCase: ObserveConnectionUseCase,
    val disconnectUseCase: DisconnectUseCase,
    val observeSelectedDeviceUseCase: ObserveSelectedDeviceUseCase,
)
