package com.example.lumen.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.utils.hasPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

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

    override fun readCharacteristic(
        serviceUUID: UUID,
        charaUUID: UUID,
    ): ByteArray? {

        TODO("Not yet implemented")
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        charaUUID: UUID,
        data: ByteArray
    ) {
        if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
            return
        }

        bluetoothGatt?.let { gatt ->
            val service = gatt.getService(serviceUUID) ?: run {
                Log.d(LOG_TAG, "Service $serviceUUID not found")
                return
            }

            val chara = service.getCharacteristic(charaUUID) ?: run {
                Log.d(LOG_TAG, "Characteristic $charaUUID not found")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeCharacteristic(
                    chara,
                    data,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                chara.value = data
                gatt.writeCharacteristic(chara)
            }
         } ?: Log.d(LOG_TAG, "GATT not initialized")
    }

    // Object that handles GATT connection state
    private val leGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
                return
            }

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
                    bluetoothGatt?.discoverServices()
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

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                close()
                return
            }

            println("Services discovered:")
            gatt?.services?.forEach { service ->
                println(" Service UUID: ${service.uuid}")

                service.characteristics.forEach { characteristic ->
                    println("  Characteristic UUID: ${characteristic.uuid} " +
                            "(${characteristic.properties})")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                return
            }

            // TODO("Not yet implemented")
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
                return
            }

            if (status != BluetoothGatt.GATT_SUCCESS) {
                return
            }

            Log.d(LOG_TAG, "Characteristic ${characteristic?.uuid} written")
        }
    }

    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectedDevice.value = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}