package com.example.lumen.domain.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.usecase.control.SetEffectSpeedUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SetEffectSpeedUseCaseTest {
    @Test
    fun `invoke coerces values within range before writing to characteristic`() =
        runTest {
            // Given
            val mockController = mockk<BleGattController>(relaxed = true)
            val useCase = SetEffectSpeedUseCase(mockController)

            // When
            useCase(300f)

            // Then
            val expectedByte = 255.toByte()
            coVerify {
                mockController.writeCharacteristic(
                    any(),
                    any(),
                    match {
                        it[0] == expectedByte
                    },
                )
            }
        }
}
