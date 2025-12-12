package com.example.lumen.domain.ble.usecase.config

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.RENAME_DEVICE_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.utils.AppConstants.MAX_DEVICE_CHAR
import timber.log.Timber
import javax.inject.Inject

class SetDeviceNameUseCase @Inject constructor(
    private val bleGattController: BleGattController,
    private val bleScanController: BleScanController
) {
    companion object {
        private const val LOG_TAG = "SetDeviceNameUseCase"
    }

    suspend operator fun invoke(name: String): Result<Unit> {
        return try {
            require (name.length <= MAX_DEVICE_CHAR && name.isNotBlank()) {
                "Device name must be between 1 and 10 characters"
            }
            
            val nameBytes = name.toByteArray()
            val lengthByte = nameBytes.size.toByte()
            val commandBytes = byteArrayOf(lengthByte) + RENAME_DEVICE_COMMAND + nameBytes

            Timber.tag(LOG_TAG).d("commandByteList: ${commandBytes.toHexString()}")

            bleGattController.writeCharacteristic(
                SERVICE_UUID,
                CHARACTERISTIC_UUID,
                commandBytes
            )

            bleGattController.disconnect()
            bleScanController.startScan()

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Failed to set device name")
            Result.failure(e)
        }
    }
}