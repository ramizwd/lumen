package com.example.lumen.domain.ble.model

/**
 * enum for BLE GATT client connection state
 */
enum class ConnectionState {
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    DISCONNECTED,
}