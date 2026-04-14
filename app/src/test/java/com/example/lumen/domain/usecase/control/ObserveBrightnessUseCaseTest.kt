package com.example.lumen.domain.usecase.control

import app.cash.turbine.test
import com.example.lumen.domain.ble.usecase.control.ObserveBrightnessUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [ObserveBrightnessUseCase]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObserveBrightnessUseCaseTest {

    private lateinit var flow: MutableSharedFlow<Float>
    private lateinit var useCase: ObserveBrightnessUseCase

    @BeforeEach
    fun setup() {
        flow = MutableSharedFlow(extraBufferCapacity = 5)
        useCase = ObserveBrightnessUseCase()
    }

    @Test
    fun `should only emit once when multiple emissions happen within 250ms`() = runTest {
        useCase(flow).test {
            flow.tryEmit(10f)
            flow.tryEmit(50f)
            flow.tryEmit(90f)

            runCurrent()
            expectNoEvents()

            advanceTimeBy(100)
            expectNoEvents()

            advanceTimeBy(150)
            runCurrent()

            assertEquals(90f, expectMostRecentItem())
        }
    }

    @Test
    fun `should emit multiple values if they fall in different windows`() = runTest {
        useCase(flow).test {
            flow.tryEmit(10f)
            advanceTimeBy(250)
            runCurrent()
            assertEquals(10f, expectMostRecentItem())

            flow.tryEmit(50f)
            advanceTimeBy(250)
            runCurrent()
            assertEquals(50f, expectMostRecentItem())
        }
    }
}