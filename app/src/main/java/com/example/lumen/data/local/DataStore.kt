package com.example.lumen.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.colorDataStore: DataStore<Preferences> by preferencesDataStore("color_prefs")
