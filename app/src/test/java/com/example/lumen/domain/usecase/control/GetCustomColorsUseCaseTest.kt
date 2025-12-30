package com.example.lumen.domain.usecase.control

import app.cash.turbine.test
import com.example.lumen.domain.ble.ColorPreferenceManager
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.usecase.control.GetCustomColorsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetCustomColorsUseCaseTest {

    private val colorPreferenceManager: ColorPreferenceManager = mockk()
    private val useCase = GetCustomColorsUseCase(colorPreferenceManager)

    @Test
    fun `invoke should return custom colors from preference manager`() = runTest {
        // Given
        val deviceAddress = "00:11:22:33:44:55"
        val expectedColors = listOf(
            CustomColorSlot(id = 1, hexColor = "FF0000"),
            CustomColorSlot(id = 2, hexColor = "00FF00"),
        )

        every {
            colorPreferenceManager.getCustomColors(deviceAddress)
        } returns flowOf(expectedColors)

        // When
        val result = useCase(deviceAddress)

        // Then
        result.test {
            assertEquals(expectedColors, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) {
            colorPreferenceManager.getCustomColors(deviceAddress)
        }
    }
}