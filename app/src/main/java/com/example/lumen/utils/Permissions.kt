package com.example.lumen.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

val btPermissionArray =
    arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
    )


fun Context.hasPermission(permission: String): Boolean {
    return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasBluetoothPermissions(): Boolean {
    return btPermissionArray.all { perms ->
        return this.checkSelfPermission(perms) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.shouldShowBluetoothRationale(): Boolean {
    return btPermissionArray.any { perm ->
        shouldShowRequestPermissionRationale(perm)
    }
}