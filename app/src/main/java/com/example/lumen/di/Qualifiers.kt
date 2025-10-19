package com.example.lumen.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ColorPreferences

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BleDevicePreferences