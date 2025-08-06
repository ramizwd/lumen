package com.example.lumen.di

import android.content.Context
import com.example.lumen.data.ble.BleControllerImpl
import com.example.lumen.domain.ble.BleController
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
    fun provideBleController(@ApplicationContext context: Context): BleController {
        return BleControllerImpl(context)
    }
}