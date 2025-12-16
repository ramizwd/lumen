package com.example.lumen.domain.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants
import com.example.lumen.domain.ble.usecase.control.ChangeBrightnessUseCase
import com.example.lumen.utils.toBrightnessCommandBytes
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [com.example.lumen.domain.ble.usecase.control.ChangeBrightnessUseCase]
 */
class ChangeBrightnessUseCaseTest {

    private lateinit var mockBleGattController: BleGattController
    private lateinit var changeBrightnessUseCase: ChangeBrightnessUseCase

    @BeforeEach
    fun setup() {
        mockBleGattController = mockk(relaxed = true)
        changeBrightnessUseCase = ChangeBrightnessUseCase(mockBleGattController)
    }

    @Test
    fun `invoke calls writeCharacteristic with correct UUIDs and converted value`() = runTest {
        val testBrightnessValue = 100f
        val expectedBytes = testBrightnessValue.toBrightnessCommandBytes()

        changeBrightnessUseCase.invoke(testBrightnessValue)

        coVerify(exactly = 1) {
            mockBleGattController.writeCharacteristic(
                GattConstants.SERVICE_UUID,
                GattConstants.CHARACTERISTIC_UUID,
                expectedBytes
            )
        }
    }

    @Test
    fun `invoke calls writeCharacteristic with zero for 0 percent brightness`() = runTest {
        val testBrightnessValue = 0f
        val expectedBytes = testBrightnessValue.toBrightnessCommandBytes()

        changeBrightnessUseCase.invoke(testBrightnessValue)

        coVerify(exactly = 1) {
            mockBleGattController.writeCharacteristic(
                GattConstants.SERVICE_UUID,
                GattConstants.CHARACTERISTIC_UUID,
                expectedBytes
            )
        }
    }

}