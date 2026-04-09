package com.example.lumen.presentation

import app.cash.turbine.test
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.usecase.common.ObserveBluetoothStateUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.connection.DisconnectUseCase
import com.example.lumen.domain.ble.usecase.connection.ObserveConnectionStateUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [MainViewModel]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var observeConnectionStateUseCase: ObserveConnectionStateUseCase
    private lateinit var disconnectUseCase: DisconnectUseCase
    private lateinit var observeBluetoothStateUseCase: ObserveBluetoothStateUseCase

    private val connectionFlow = MutableSharedFlow<ConnectionState>()
    private val bluetoothFlow = MutableSharedFlow<BluetoothState>()

    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        observeConnectionStateUseCase = mockk()
        disconnectUseCase = mockk(relaxed = true)
        observeBluetoothStateUseCase = mockk()

        every { observeConnectionStateUseCase() } returns connectionFlow
        every { observeBluetoothStateUseCase() } returns bluetoothFlow
        
        val connectionUseCases = ConnectionUseCases(
            observeConnectionStateUseCase = observeConnectionStateUseCase,
            disconnectUseCase = disconnectUseCase,
            connectToDeviceUseCase = mockk(relaxed = true),
            observeConnectionEventsUseCase = mockk(relaxed = true),
            observeSelectedDeviceUseCase = mockk(relaxed = true)
        )
        
        viewModel = MainViewModel(connectionUseCases, observeBluetoothStateUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when connection state is CONNECTING, loading state is true`() = runTest {
        viewModel.showLoading.test {
            connectionFlow.emit(ConnectionState.CONNECTING)
            assertTrue(expectMostRecentItem())
        }
    }

    @Test
    fun `when connection state is CONNECTING, loading text is correct`() = runTest {
        viewModel.loadingText.test {
            connectionFlow.emit(ConnectionState.CONNECTING)
            assertEquals("Connecting...", expectMostRecentItem())
        }
    }

    @Test
    fun `when connection state is DISCONNECTED, loading text is empty`() = runTest {
        viewModel.loadingText.test {
            connectionFlow.emit(ConnectionState.DISCONNECTED)
            assertEquals("", expectMostRecentItem())
        }
    }

    @Test
    fun `when bluetooth turns off while connected, disconnect is called`() = runTest {
        connectionFlow.emit(ConnectionState.STATE_LOADED_AND_CONNECTED)

        bluetoothFlow.emit(BluetoothState.TURNING_OFF)

        coVerify(exactly = 1) { disconnectUseCase() }
    }

    @Test
    fun `manual disconnect calls disconnectUseCase`() = runTest {
        viewModel.disconnect()
        
        coVerify(exactly = 1) { disconnectUseCase() }
    }









}