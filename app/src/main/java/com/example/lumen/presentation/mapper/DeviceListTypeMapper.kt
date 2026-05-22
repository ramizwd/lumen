package com.example.lumen.presentation.mapper

import androidx.annotation.StringRes
import com.example.lumen.R
import com.example.lumen.domain.ble.model.DeviceListType

@StringRes
fun DeviceListType.toResourceId(): Int =
    when (this) {
        DeviceListType.ALL_DEVICES -> R.string.all
        DeviceListType.FAVORITE_DEVICES -> R.string.favorites
    }
