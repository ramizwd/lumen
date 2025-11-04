package com.example.lumen.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lumen.presentation.navigation.LumenNavHost
import com.example.lumen.presentation.theme.LumenTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object MainActivity {
        private const val LOG_TAG = "MainActivityLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bluetoothLeAvailable = packageManager.hasSystemFeature(
            PackageManager.FEATURE_BLUETOOTH_LE
        )

        if (!bluetoothLeAvailable){
            Timber.tag(LOG_TAG).d("BLE not available.")
            return
        }

        setContent {
            LumenTheme {
                LumenNavHost()
            }
        }
    }
}
