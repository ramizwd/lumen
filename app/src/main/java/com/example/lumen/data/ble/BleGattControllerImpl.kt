package com.example.lumen.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.utils.hasPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Class that implements [BleGattController] interface.
 * Handles the low-level Android Bluetooth API GATT interactions
 */
@SuppressLint("MissingPermission")
class BleGattControllerImpl(
    private val context: Context
): BleGattController {

    companion object {
        private const val LOG_TAG = "BleGattControllerImpl"
    }

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState>
        get() = _connectionState.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BleDevice?>(null)
    override val connectedDevice: StateFlow<BleDevice?>
        get() = _connectedDevice.asStateFlow()

    override fun connect(selectedDevice: BleDevice?) {
        if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
            return
        }

        bluetoothAdapter?.let { adapter ->
            try {
                _connectionState.value = ConnectionState.CONNECTING
                _connectedDevice.value = selectedDevice

                val device = adapter.getRemoteDevice(selectedDevice?.address)
                bluetoothGatt = device?.connectGatt(context, false, leGattCallback)
            } catch (_: IllegalArgumentException) {
                Log.d(LOG_TAG, "Device not found")
                close()
            }
        } ?: Log.d(LOG_TAG, "BluetoothAdapter not initialized.")
    }

    override fun disconnect() {
        if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
            return
        }

        if (_connectionState.value == ConnectionState.CONNECTED ||
            _connectionState.value == ConnectionState.CONNECTING) {
            bluetoothGatt?.disconnect()
            Log.d(LOG_TAG, "Device disconnected")
        }
    }

    // Object that handles GATT connection state
    private val leGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                close()
                return
            }

            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> {
                    Log.d(LOG_TAG, "GATT connecting...")
                    _connectionState.value = ConnectionState.CONNECTING
                }

                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(LOG_TAG, "GATT successfully connected")
                    _connectionState.value = ConnectionState.CONNECTED
                }

                BluetoothProfile.STATE_DISCONNECTING -> {
                    Log.d(LOG_TAG, "GATT disconnecting...")
                    _connectionState.value = ConnectionState.DISCONNECTING
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    close()
                    Log.d(LOG_TAG, "GATT disconnected")
                }
            }
        }
    }

    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectedDevice.value = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}