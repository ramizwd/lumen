package com.example.lumen.domain.ble.usecase.discovery

import com.example.lumen.domain.ble.BleScanController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveScanErrorsUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    operator fun invoke(): Flow<String> {
        return bleScanController.errors
    }
}