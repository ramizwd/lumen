package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BluetoothState
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface for observing Bluetooth state.
 */
interface BluetoothStateManager {
    val bluetoothState: SharedFlow<BluetoothState>
}
