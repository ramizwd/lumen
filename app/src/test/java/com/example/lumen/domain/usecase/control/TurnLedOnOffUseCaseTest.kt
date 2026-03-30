package com.example.lumen.domain.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.model.GattConstants.LED_OFF_COMMAND
import com.example.lumen.domain.ble.model.GattConstants.LED_ON_COMMAND
import com.example.lumen.domain.ble.usecase.control.TurnLedOnOffUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [TurnLedOnOffUseCase]
 */
class TurnLedOnOffUseCaseTest {

    private lateinit var mockBleGattController: BleGattController
    private lateinit var turnLedOnOffUseCase: TurnLedOnOffUseCase

    @BeforeEach
    fun setup() {
        mockBleGattController = mockk(relaxed = true)
        turnLedOnOffUseCase = TurnLedOnOffUseCase(mockBleGattController)
    }

    @Test
    fun `invoke with true sends correct command`() = runTest {
        turnLedOnOffUseCase.invoke(true)

        coVerify(exactly = 1) {
            mockBleGattController.writeCharacteristic(
                any(),
                any(),
                LED_ON_COMMAND
            )
        }
    }

    @Test
    fun `invoke with false sends correct command`() = runTest {
        turnLedOnOffUseCase.invoke(false)

        coVerify {
            mockBleGattController.writeCharacteristic(
                any(),
                any(),
                LED_OFF_COMMAND
            )
        }
    }
}