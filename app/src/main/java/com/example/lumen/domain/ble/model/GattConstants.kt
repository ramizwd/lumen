package com.example.lumen.domain.ble.model

import java.util.UUID

/**
 * Holds GATT services & characteristics UUIDs, and command bytes related to the LED controller
 */
object GattConstants {
    // Service and characteristic UUIDs

    // Service and its characteristic (with notify and write) and CCCD
    val SERVICE_UUID: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // Command bytes and their padding bytes

    val LED_ON_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0xAA.toByte())
    val LED_OFF_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0xAB.toByte())

    val GET_INFO_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x10)

    val RENAME_DEVICE_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0xBB.toByte())

    val SET_LED_NUM_COMMAND: ByteArray = byteArrayOf(0x00, 0x2D.toByte())

    val SET_IC_MODEL_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x1C.toByte())

    val SET_RGB_SEQ_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x3C.toByte())

    val SET_EFFECT_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x2C.toByte())

    val SET_SPEED_COMMAND: ByteArray = byteArrayOf(0x00, 0x00, 0x03.toByte())

    val BRIGHTNESS_COMMAND = byteArrayOf(0x00, 0x00, 0x2A)

    // Color change command byte
    const val COLOR_SUFFIX_HEX = "1E"
}
