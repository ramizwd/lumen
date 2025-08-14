package com.example.lumen.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

val permissionArray =
    arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
    )


fun Context.hasPermission(permission: String): Boolean {
    return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}
