package com.example.lumen.domain.ble.model

/**
 * enum for BLE GATT client connection state
 */
enum class ConnectionState {
    CONNECTING,
    LOADING_STATE,
    CONNECTED,
    DISCONNECTING,
    DISCONNECTED,
    RETRYING,
    INVALID_DEVICE,
}