package com.example.lumen.presentation.common.model

import com.example.lumen.domain.ble.model.BleDevice

/**
 * Represent the BLE device UI, including its domain data and its favorite status
 */
data class DeviceContent(
    val device: BleDevice,
    val isFavorite: Boolean,
)