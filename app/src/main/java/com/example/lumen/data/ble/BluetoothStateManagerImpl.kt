package com.example.lumen.data.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.example.lumen.domain.ble.BluetoothStateManager
import com.example.lumen.domain.ble.model.BluetoothState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implements [BluetoothStateManager] interface.
 * Class to monitor Bluetooth state using a broadcast receiver
 */
class BluetoothStateManagerImpl(
    private val context: Context,
): BluetoothStateManager {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    override fun observeBluetoothState(): Flow<BluetoothState> = callbackFlow {

        val bluetoothStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )

                    val newState = when (state) {
                        BluetoothAdapter.STATE_ON -> BluetoothState.ON
                        BluetoothAdapter.STATE_OFF -> BluetoothState.OFF
                        BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TURNING_ON
                        BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TURNING_OFF
                        else -> BluetoothState.UNKNOWN
                    }
                    trySend(newState)
                }
            }
        }

        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                bluetoothStateReceiver,
                intentFilter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(bluetoothStateReceiver, intentFilter)
        }

        val initialState = when (bluetoothAdapter?.state) {
            BluetoothAdapter.STATE_ON -> BluetoothState.ON
            BluetoothAdapter.STATE_OFF -> BluetoothState.OFF
            BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TURNING_ON
            BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TURNING_OFF
            else -> BluetoothState.UNKNOWN
        }
        trySend(initialState)

        awaitClose {
            context.unregisterReceiver(bluetoothStateReceiver)
        }
    }
}