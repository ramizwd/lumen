package com.example.lumen.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.lumen.data.mapper.toLedControllerState
import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.model.GattConstants.CCCD_UUID
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.GET_INFO_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.utils.hasPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

        private const val MAX_CONNECTION_TRIES = 5
        private const val RETRY_DELAY_MILLIS: Long = 500 // half a sec
    }

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private var connRetryCount = 0
    private var connRetryJob: Job? = null
    private val bleConnScope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState>
        get() = _connectionState.asStateFlow()

    private val _selectedDevice = MutableStateFlow<BleDevice?>(null)
    override val selectedDevice: StateFlow<BleDevice?>
        get() = _selectedDevice.asStateFlow()

    private val _ledControllerState = MutableStateFlow<LedControllerState?>(null)
    override val ledControllerState: StateFlow<LedControllerState?>
        get() = _ledControllerState.asStateFlow()

    override fun connect(selectedDevice: BleDevice?) {
        if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
            return
        }

        bluetoothAdapter?.let { adapter ->
            try {
                _connectionState.value = ConnectionState.CONNECTING
                _selectedDevice.value = selectedDevice

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

        connRetryJob?.cancel()
        connRetryJob = null

        if (_connectionState.value == ConnectionState.CONNECTED ||
            _connectionState.value == ConnectionState.CONNECTING ||
            _connectionState.value == ConnectionState.RETRYING) {
            bluetoothGatt?.disconnect()
            Log.d(LOG_TAG, "Device disconnected")
        }
    }

    override suspend fun writeCharacteristic(
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

            charaWriteOperation(chara, data)
         } ?: Log.d(LOG_TAG, "GATT not initialized for writeCharacteristic")
    }

    // Object that handles GATT connection state
    private val leGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
                return
            }

            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> {
                    Log.d(LOG_TAG, "GATT connecting...")
                    _connectionState.value = ConnectionState.CONNECTING
                }

                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(LOG_TAG, "GATT successfully connected")
                    connRetryCount = 0
                    _connectionState.value = ConnectionState.CONNECTED
                    bluetoothGatt?.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTING -> {
                    Log.d(LOG_TAG, "GATT disconnecting...")
                    _connectionState.value = ConnectionState.DISCONNECTING
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(LOG_TAG, "GATT disconnected")

                    if (status != BluetoothGatt.GATT_SUCCESS) {
                        Log.e(LOG_TAG, "GATT failure")

                        // Sometimes connection fails due to BLE or the device being
                        // connected to is finicky. If it fails, try again after some delay.
                        retryConnection()
                    } else {
                        close()
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
                return
            }

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

                    val supportsNotify = characteristic.properties and
                            BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0
                    val supportsIndicate = characteristic.properties and
                            BluetoothGattCharacteristic.PROPERTY_INDICATE != 0

                    // If characteristic supports either notify or indicate, enable them
                    if (supportsNotify) {
                        enableNotifications(
                            characteristic,
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        )
                    } else if (supportsIndicate) {
                        enableNotifications(
                            characteristic,
                            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        )
                    }
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                return
            }

            Log.d(LOG_TAG, "Characteristic ${characteristic?.uuid} written")
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)

            Log.d(LOG_TAG, "Notification received from ${characteristic.uuid}: " +
                    value.toHexString()
            )

            // Get the LED controller's info from its notification
            if (characteristic.uuid == CHARACTERISTIC_UUID) {
                getControllerState(value)
            }
        }

        @Deprecated("Deprecated in API 33")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            super.onCharacteristicChanged(gatt, characteristic)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                val value = characteristic.value

                Log.d(LOG_TAG, "Notification received from ${characteristic.uuid}: " +
                        value.toHexString()
                )

                // Get the LED controller's info from its notification
                if (characteristic.uuid == CHARACTERISTIC_UUID) {
                    getControllerState(value)
                }
            }
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
            value: ByteArray
        ) {
            super.onDescriptorRead(gatt, descriptor, status, value)

            Log.d(LOG_TAG, "onDescriptorRead: ${descriptor.uuid} " +
                    "value: ${value.toHexString()}")
        }

        @Deprecated("Deprecated in API 33")
        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
        ) {
            super.onDescriptorRead(gatt, descriptor, status)

            Log.d(LOG_TAG, "onDescriptorRead: ${descriptor.uuid} " +
                    "value: ${descriptor.value.toHexString()}")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                return
            }

            // If notifications are enabled for the characteristic then request controller's info
            descriptor?.let { descriptor ->
                if (descriptor.characteristic.uuid == CHARACTERISTIC_UUID &&
                    descriptor.uuid == CCCD_UUID) {
                    Log.d(LOG_TAG, "Notifications successfully enabled for" +
                            " characteristic ${descriptor.characteristic.uuid}")

                    requestControllerState()
                }
            } ?: Log.d(LOG_TAG, "Descriptor write failed")
        }
    }

    private fun enableNotifications(chara: BluetoothGattCharacteristic, descValue: ByteArray) {
        bluetoothGatt?.let { gatt ->
            gatt.setCharacteristicNotification(chara,true)

            val desc = chara.getDescriptor(CCCD_UUID)
            desc?.let { descriptor ->
                Log.d(LOG_TAG, "Writing descriptor for notifications on ${chara.uuid}")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(
                        descriptor,
                        descValue
                    )
                } else {
                    descriptor.value = descValue
                    gatt.writeDescriptor(descriptor)
                }
            } ?: Log.d(LOG_TAG, "CCCD not found for ${chara.uuid}")
        } ?: Log.d(LOG_TAG, "GATT not initialized for enableNotifications")
    }

    private fun requestControllerState() {
        if (!context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.d(LOG_TAG, "BLUETOOTH_CONNECT permission missing!")
            return
        }

        bluetoothGatt?.let { gatt ->
            val service = gatt.getService(SERVICE_UUID) ?: run {
                Log.d(LOG_TAG, "Service $SERVICE_UUID not found")
                return
            }

            val chara = service.getCharacteristic(CHARACTERISTIC_UUID) ?: run {
                Log.d(LOG_TAG, "Characteristic $CHARACTERISTIC_UUID not found")
                return
            }

            charaWriteOperation(chara, GET_INFO_COMMAND)
        } ?: Log.d(LOG_TAG, "GATT not initialized for requestControllerInfo")
    }


    private fun getControllerState(value: ByteArray) {
        if (value.size >= 12) {
            _ledControllerState.value = value.toLedControllerState()
            Log.i(LOG_TAG, "Controller state: ${_ledControllerState.value}")
        } else {
            Log.d(LOG_TAG, "Expected length 12 bytes. Received: ${value.size}")
        }
    }

    private fun charaWriteOperation(chara: BluetoothGattCharacteristic, data: ByteArray) {
        bluetoothGatt?.let { gatt ->
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
        } ?: Log.d(LOG_TAG, "GATT not initialized for charaWriteOperation")
    }

    private fun retryConnection() {
        connRetryJob?.cancel()

        if (connRetryCount < MAX_CONNECTION_TRIES) {
            connRetryCount++
            _connectionState.value = ConnectionState.RETRYING
            Log.d(LOG_TAG, "Connection failed (attempt $connRetryCount). Retrying...")

            connRetryJob = bleConnScope.launch {
                delay(RETRY_DELAY_MILLIS)
                bluetoothGatt?.close()
                bluetoothGatt = null

                _selectedDevice.value?.let { device ->
                    connect(device)
                } ?: run {
                    Log.e(LOG_TAG, "Cannot retry, device to connect to is null")
                    close()
                }
            }
        } else {
            Log.e(LOG_TAG, "Max connection tries reached. Connection failed")
            close()
        }
    }

    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        _selectedDevice.value = null
        _ledControllerState.value = null
        connRetryCount = 0
        connRetryJob?.cancel()
        connRetryJob = null
        _connectionState.value = ConnectionState.DISCONNECTED

        Log.d(LOG_TAG, "Connection closed")
    }
}