package com.example.lumen.domain.ble

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.LedControllerInfo
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Interface for controlling BLE GATT operations
 */
interface BleGattController {
    val connectionState: StateFlow<ConnectionState>
    val connectedDevice: StateFlow<BleDevice?>
    val ledControllerInfo: StateFlow<LedControllerInfo?>

    fun connect(selectedDevice: BleDevice?)
    fun disconnect()

    suspend fun writeCharacteristic(
        serviceUUID: UUID,
        charaUUID: UUID,
        data: ByteArray
    )
}
