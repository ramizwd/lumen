package com.example.lumen.domain.ble.usecase.config

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_LED_NUM_COMMAND
import com.example.lumen.domain.ble.model.LedConstants.ACTIVE_PIXELS_RANGE
import timber.log.Timber
import javax.inject.Inject

class SetLedNumUseCase @Inject constructor(
    private val bleGattController: BleGattController,
) {
    companion object {
        private const val LOG_TAG = "SetLedNumUseCase"
    }

    suspend operator fun invoke(pxlCount: Int): Result<Unit> =
        try {
            require(pxlCount in ACTIVE_PIXELS_RANGE) {
                "LED count must be within the range"
            }

            val hiByte = (pxlCount shr 8).toByte()
            val loByte = (pxlCount and 0xFF).toByte()

            val commandBytes = byteArrayOf(
                hiByte,
                loByte,
                SET_LED_NUM_COMMAND[0],
                SET_LED_NUM_COMMAND[1],
            )

            Timber.tag(LOG_TAG).d("Command to send: ${commandBytes.contentToString()}")

            bleGattController.writeCharacteristic(
                SERVICE_UUID,
                CHARACTERISTIC_UUID,
                commandBytes,
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "Failed to set LED count")
            Result.failure(e)
        }
}
