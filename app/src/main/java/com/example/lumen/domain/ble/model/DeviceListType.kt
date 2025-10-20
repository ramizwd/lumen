package com.example.lumen.domain.ble.model

/**
 * Represents the types of BLE device lists
 */
enum class DeviceListType(val displayName: String) {
    ALL_DEVICES("All"),
    FAVORITE_DEVICES("Favorites")
}