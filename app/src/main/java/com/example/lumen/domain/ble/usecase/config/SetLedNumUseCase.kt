package com.example.lumen.domain.ble.usecase.config

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.CHARACTERISTIC_UUID
import com.example.lumen.domain.ble.model.GattConstants.SERVICE_UUID
import com.example.lumen.domain.ble.model.GattConstants.SET_LED_NUM_COMMAND
import com.example.lumen.utils.AppConstants.MAX_LED_NUM
import com.example.lumen.utils.AppConstants.MIN_LED_NUM
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
            require(pxlCount in MIN_LED_NUM..MAX_LED_NUM) {
                "LED count must be between $MIN_LED_NUM and $MAX_LED_NUM"
            }

            val hiByte = (pxlCount shr 8).toByte()
            val loByte = (pxlCount and 0xFF).toByte()

            val commandBytes = byteArrayOf(hiByte, loByte) + SET_LED_NUM_COMMAND

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
