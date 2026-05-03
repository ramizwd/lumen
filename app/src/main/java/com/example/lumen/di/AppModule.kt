package com.example.lumen.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.lumen.data.ble.BleDevicePreferenceManagerImpl
import com.example.lumen.data.ble.BleGattControllerImpl
import com.example.lumen.data.ble.BleScanControllerImpl
import com.example.lumen.data.ble.BluetoothStateManagerImpl
import com.example.lumen.data.ble.ColorPreferenceManagerImpl
import com.example.lumen.data.local.bleDeviceDataStore
import com.example.lumen.data.local.colorDataStore
import com.example.lumen.domain.ble.BleDevicePreferenceManager
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.BluetoothStateManager
import com.example.lumen.domain.ble.ColorPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter? {
        val manager = context.getSystemService(BluetoothManager::class.java)
        return manager?.adapter
    }

    @Provides
    @Singleton
    fun provideBleScanController(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter?,
    ): BleScanController {
        return BleScanControllerImpl(
            context,
            bluetoothAdapter,
        )
    }

    @Provides
    @Singleton
    fun provideBleGattController(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter?,
    ): BleGattController {
        return BleGattControllerImpl(
            context,
            bluetoothAdapter,
        )
    }

    @Provides
    @Singleton
    fun provideBluetoothStateDataSource(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter?,
    ): BluetoothStateManager {
        return BluetoothStateManagerImpl(
            context,
            bluetoothAdapter
        )
    }

    @Provides
    @Singleton
    @ColorPreferences
    fun provideColorDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.colorDataStore
    }

    @Provides
    @Singleton
    @BleDevicePreferences
    fun provideBleDeviceDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.bleDeviceDataStore
    }

    @Provides
    @Singleton
    fun provideColorPreferenceManager(
        @ColorPreferences dataStore: DataStore<Preferences>
    ): ColorPreferenceManager {
        return ColorPreferenceManagerImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideBleDevicePreferenceManager(
        @BleDevicePreferences dataStore: DataStore<Preferences>
    ): BleDevicePreferenceManager {
        return BleDevicePreferenceManagerImpl(dataStore)
    }
}