package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Interface for controlling BLE GATT operations
 */
interface BleGattController {
    val connectionState: StateFlow<ConnectionState>
    val connectedDevice: StateFlow<BleDevice?>

    fun connect(selectedDevice: BleDevice?)
    fun disconnect()

    fun readCharacteristic(
        serviceUUID: UUID,
        charaUUID: UUID
    ): ByteArray?

    fun writeCharacteristic(
        serviceUUID: UUID,
        charaUUID: UUID,
        data: ByteArray
    )
}
