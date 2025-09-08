package com.example.lumen.di

import android.content.Context
import com.example.lumen.data.ble.BleGattControllerImpl
import com.example.lumen.data.ble.BleScanControllerImpl
import com.example.lumen.data.ble.BluetoothStateManagerImpl
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.BluetoothStateManager
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
    @Singleton
    fun provideBleScanController(@ApplicationContext context: Context): BleScanController {
        return BleScanControllerImpl(context)
    }

    @Provides
    @Singleton
    fun provideBleGattController(@ApplicationContext context: Context): BleGattController {
        return BleGattControllerImpl(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothStateDataSource(
        @ApplicationContext context: Context
    ): BluetoothStateManager {
        return BluetoothStateManagerImpl(context)
    }
}