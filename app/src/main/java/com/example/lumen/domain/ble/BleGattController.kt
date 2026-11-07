package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.domain.ble.model.LedSetting
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Interface for controlling BLE GATT operations
 */
interface BleGattController {
    val connectionState: StateFlow<ConnectionState>
    val connectionEvents: SharedFlow<ConnectionResult>
    val selectedDevice: StateFlow<BleDevice?>
    val connectedDevice: StateFlow<BleDevice?>
    val ledControllerState: StateFlow<LedControllerState?>

    suspend fun connect(selectedDevice: BleDevice)

    fun disconnect()

    suspend fun writeCharacteristic(
        serviceUUID: UUID,
        charaUUID: UUID,
        data: ByteArray,
        value: Pair<Any, LedSetting>?,
    ): Result<Unit>
}
