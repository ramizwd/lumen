package com.example.lumen.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import timber.log.Timber

val Context.colorDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "color_prefs",
    corruptionHandler = ReplaceFileCorruptionHandler { exception ->
        Timber.tag("DataStore").e(exception, "colorDataStore corrupted")
        emptyPreferences()
    }
)

val Context.bleDeviceDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "device_prefs",
    corruptionHandler = ReplaceFileCorruptionHandler { exception ->
        Timber.tag("DataStore").e(exception, "bleDeviceDataStore corrupted")
        emptyPreferences()
    }
)
