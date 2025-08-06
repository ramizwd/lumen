package com.example.lumen.data.mapper

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.example.lumen.domain.ble.model.BleDevice

@SuppressLint("MissingPermission")
fun ScanResult.toBleDevice(): BleDevice {
    return BleDevice(
        name = device.name,
        address = device.address
    )
}