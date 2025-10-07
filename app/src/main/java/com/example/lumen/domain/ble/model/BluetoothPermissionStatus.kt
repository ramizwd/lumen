package com.example.lumen.domain.ble.model

/**
 * Represents Bluetooth permission status
 */
enum class BluetoothPermissionStatus {
    GRANTED,
    DENIED_RATIONALE_REQUIRED,
    DENIED_PERMANENTLY,
    UNKNOWN,
}