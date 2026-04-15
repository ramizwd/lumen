package com.example.lumen.domain.usecase.config

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.GattConstants
import com.example.lumen.domain.ble.model.GattConstants.RENAME_DEVICE_COMMAND
import com.example.lumen.domain.ble.usecase.config.SetDeviceNameUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [SetDeviceNameUseCase]
 */
class SetDeviceNameUseCaseTest {
    private lateinit var mockBleGattController: BleGattController
    private lateinit var mockBleScanController: BleScanController
    private lateinit var setDeviceNameUseCase: SetDeviceNameUseCase

    @BeforeEach
    fun setup() {
        mockBleGattController = mockk(relaxed = true)
        mockBleScanController = mockk(relaxed = true)
        setDeviceNameUseCase = SetDeviceNameUseCase(
            mockBleGattController,
            mockBleScanController
        )
    }

    @Test
    fun `successful rename follows correct function invocations`() = runTest {
        // Given
        val name = "Test"
        val nameBytes = name.toByteArray()
        val expectedBytes = byteArrayOf(nameBytes.size.toByte()) + RENAME_DEVICE_COMMAND + nameBytes

        // When
        val result = setDeviceNameUseCase(name)

        // Then
        assertTrue(result.isSuccess)
        coVerifyOrder {
            mockBleGattController.writeCharacteristic(
                GattConstants.SERVICE_UUID,
                GattConstants.CHARACTERISTIC_UUID,
                expectedBytes
            )
            mockBleGattController.disconnect()
            mockBleScanController.startScan()
        }
    }

    @Test
    fun `when name is too long, returns failure with IllegalArgumentException`() = runTest {
        // When
        val result = setDeviceNameUseCase("longDeviceName")

        // Then
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue(ex is IllegalArgumentException)
        assertEquals("Device name must be between 1 and 10 characters", ex?.message)

        coVerify(exactly = 0) {
            mockBleGattController.writeCharacteristic(
                any(), any(), any()
            )
        }
    }

    @Test
    fun `when name is blank, returns failure`() = runTest {
        // When
        val result = setDeviceNameUseCase(" ")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `when writeCharacteristic fails, returns failure`() = runTest {
        // Given
        val errMsg = "BLE error"
        coEvery {
            mockBleGattController.writeCharacteristic(
                any(), any(), any()
            )
        } throws Exception(errMsg)

        // When
        val result = setDeviceNameUseCase("Test")

        // Then
        assertTrue(result.isFailure)
        assertEquals(errMsg, result.exceptionOrNull()?.message)

        coVerify(exactly = 0) { mockBleGattController.disconnect() }
        coVerify(exactly = 0) { mockBleScanController.startScan() }
    }
}