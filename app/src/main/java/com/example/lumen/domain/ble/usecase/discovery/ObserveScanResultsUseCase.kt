package com.example.lumen.domain.ble.usecase.discovery

import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveScanResultsUseCase @Inject constructor(
    private val bleScanController: BleScanController
) {
    operator fun invoke(): Flow<List<BleDevice>> {
        val scanResultsMap =  bleScanController.scanResults
        return scanResultsMap.map { it.values.toList() }
    }
}