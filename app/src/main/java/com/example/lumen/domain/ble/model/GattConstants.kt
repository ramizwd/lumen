package com.example.lumen.domain.ble.model

import java.util.UUID

/**
 * Holds GATT services & characteristics UUIDs, and command bytes related to the LED controller
 */
object GattConstants {

    // Service and characteristic UUIDs

    val SERVICE_UUID: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")

    // Command Bytes

    val LED_ON_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0xAA.toByte())
    val LED_OFF_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0xAB.toByte())

    // Padding values (0s) and command byte (2A) of brightness hex
    const val BRIGHTNESS_SUFFIX_HEX = "00002A"

}