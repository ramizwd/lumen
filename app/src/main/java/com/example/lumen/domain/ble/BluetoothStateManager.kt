package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BluetoothState
import kotlinx.coroutines.flow.Flow

/**
 * Interface for observing Bluetooth state.
 */
interface BluetoothStateManager {
    fun observeBluetoothState(): Flow<BluetoothState>
}
