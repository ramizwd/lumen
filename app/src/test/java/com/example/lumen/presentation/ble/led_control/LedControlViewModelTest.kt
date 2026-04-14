package com.example.lumen.presentation.ble.led_control

import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.domain.ble.usecase.config.SetDeviceNameUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.connection.DisconnectUseCase
import com.example.lumen.domain.ble.usecase.connection.ObserveSelectedDeviceUseCase
import com.example.lumen.domain.ble.usecase.control.ChangeBrightnessUseCase
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import com.example.lumen.domain.ble.usecase.control.GetCustomColorsUseCase
import com.example.lumen.domain.ble.usecase.control.ObserveBrightnessUseCase
import com.example.lumen.domain.ble.usecase.control.ObserveControllerStateUseCase
import com.example.lumen.domain.ble.usecase.control.SaveCustomColorUseCase
import com.example.lumen.domain.ble.usecase.control.SetLedColorUseCase
import com.example.lumen.domain.ble.usecase.control.TurnLedOnOffUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [LedControlViewModel]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LedControlViewModelTest {

    private val device = BleDevice("Test", "00:11:22:33:44:55")
    private val controllerState = LedControllerState(
        isOn = true,
        preset = 1,
        speed = 1,
        brightness = 50f,
        icModel = 1,
        channel = 1,
        pixelCount = 50,
        red = "ff",
        green = "00",
        blue = "00",
        whiteLedBrightness = 0
    )

    private val setDeviceNameUseCase: SetDeviceNameUseCase = mockk()
    private val observeSelectedDeviceUseCase: ObserveSelectedDeviceUseCase = mockk()
    private val observeControllerStateUseCase: ObserveControllerStateUseCase = mockk()
    private val observeBrightnessUseCase: ObserveBrightnessUseCase = mockk()
    private val getCustomColorsUseCase: GetCustomColorsUseCase = mockk()
    private val turnLedOnOffUseCase: TurnLedOnOffUseCase = mockk(relaxed = true)
    private val changeBrightnessUseCase: ChangeBrightnessUseCase = mockk(relaxed = true)
    private val saveCustomColorUseCase: SaveCustomColorUseCase = mockk(relaxed = true)
    private val setLedColorUseCase: SetLedColorUseCase = mockk(relaxed = true)
    private val disconnectUseCase: DisconnectUseCase = mockk(relaxed = true)

    private lateinit var viewModel: LedControlViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        coEvery { setDeviceNameUseCase(any()) } returns Result.success(Unit)
        every { observeSelectedDeviceUseCase() } returns flowOf(device)
        every { observeControllerStateUseCase() } returns flowOf(controllerState)
        every { observeBrightnessUseCase(any()) } returns emptyFlow()
        every { getCustomColorsUseCase(any()) } returns flowOf(emptyList())

        createViewModel()
    }

    private fun createViewModel() {
        val connectionUseCases = ConnectionUseCases(
            connectToDeviceUseCase = mockk(),
            observeConnectionStateUseCase = mockk(),
            observeConnectionEventsUseCase = mockk(),
            observeSelectedDeviceUseCase = observeSelectedDeviceUseCase,
            disconnectUseCase = disconnectUseCase,
        )

        val controlUseCases = ControlUseCases(
            turnLedOnOffUseCase = turnLedOnOffUseCase,
            setLedColorUseCase = setLedColorUseCase,
            changeBrightnessUseCase = changeBrightnessUseCase,
            observeBrightnessUseCase = observeBrightnessUseCase,
            observeControllerStateUseCase = observeControllerStateUseCase,
            saveCustomColorUseCase = saveCustomColorUseCase,
            getCustomColorsUseCase = getCustomColorsUseCase,
        )

        viewModel = LedControlViewModel(connectionUseCases, controlUseCases, setDeviceNameUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads data into state`() = runTest {
        assertEquals(device, viewModel.uiState.value.selectedDevice)
        assertEquals(controllerState.isOn, viewModel.uiState.value.isLedOn)
        assertEquals(
            controllerState.brightness, viewModel.uiState.value.brightnessValue
        )
        assertEquals(
            controllerState.pixelCount, viewModel.uiState.value.pixelCount
        )
        assertEquals(
            "${controllerState.red}${controllerState.green}${controllerState.blue}",
            viewModel.uiState.value.ledHexColor
        )
    }

    @Test
    fun `init with null controller state sets default values`() = runTest {
        // Given
        every { observeControllerStateUseCase() } returns flowOf(null)

        // When
        createViewModel()

        // Then
        assertFalse(viewModel.uiState.value.isLedOn)
        assertEquals("ffffff",viewModel.uiState.value.ledHexColor)
        assertEquals(0f,viewModel.uiState.value.brightnessValue)
        assertEquals(0, viewModel.uiState.value.pixelCount)
    }

    @Test
    fun `init collects custom colors for device`() = runTest {
        // Given
        val expectedState = listOf(CustomColorSlot(id = 1, hexColor = "ffffff"))
        every { getCustomColorsUseCase(any()) } returns
                flowOf(expectedState)

        // When
        createViewModel()

        // Then
        assertEquals(expectedState, viewModel.uiState.value.customColorSlots)
    }

    @Test
    fun `init collects from brightness flow and calls changeBrightnessUseCase`() = runTest {
        // Given
        val brightnessFlow = MutableSharedFlow<Float>()
        val value = 50f
        every { observeBrightnessUseCase(any()) } returns brightnessFlow

        // When
        createViewModel()
        brightnessFlow.emit(value)

        // Then
        coVerify(exactly = 1) { changeBrightnessUseCase(value) }
    }

    @Test
    fun `turnLedOn updates state and calls use case`() = runTest {
        // When
        viewModel.turnLedOn()

        // Then
        assertTrue(viewModel.uiState.value.isLedOn)
        coVerify(exactly = 1) { turnLedOnOffUseCase(true) }
    }

    @Test
    fun `turnLedOff updates state and calls use case`() = runTest {
        viewModel.turnLedOff()

        assertTrue(!viewModel.uiState.value.isLedOn)
        coVerify(exactly = 1) { turnLedOnOffUseCase(false) }
    }

    @Test
    fun `setLedColor updates state and calls use case`() = runTest {
        val expectedHex = "ff00ff"
        viewModel.setLedColor(expectedHex)

        assertEquals(expectedHex, viewModel.uiState.value.ledHexColor)
        coVerify(exactly = 1) { setLedColorUseCase(expectedHex) }
    }

    @Test
    fun `ToggleRenameDeviceDialog updates dialog visibility state`() = runTest {
        viewModel.onEvent(LedControlUiEvent.ToggleRenameDeviceDialog(true))
        assertTrue(viewModel.uiState.value.showRenameDeviceDialog)

        viewModel.onEvent(LedControlUiEvent.ToggleRenameDeviceDialog(false))
        assertFalse(viewModel.uiState.value.showRenameDeviceDialog)
    }

    @Test
    fun `disconnectFromDevice calls disconnectUseCase`() = runTest {
        viewModel.disconnectFromDevice()

        coVerify(exactly = 1) { disconnectUseCase() }
    }

    @Test
    fun `setDeviceName on success updates infoMessage state with success text`() = runTest {
        viewModel.setDeviceName("New name")

        assertEquals("Device renamed", viewModel.uiState.value.infoMessage)
    }

    @Test
    fun `setDeviceName on failure updates infoMessage state with error text`() = runTest {
        // Given
        coEvery { setDeviceNameUseCase(any()) } returns
                Result.failure(Exception("Error"))

        // When
        viewModel.setDeviceName("New name")

        // Then
        assertEquals("Error renaming device", viewModel.uiState.value.infoMessage)
    }

    @Test
    fun `setDeviceName returns an error message if device name is null`() = runTest {
        // Given
        coEvery { setDeviceNameUseCase(any()) } returns Result.failure(Exception("Error"))

        // When
        viewModel.setDeviceName("")

        // Then
        assertEquals("Error renaming device", viewModel.uiState.value.infoMessage)
    }

    @Test
    fun `clearInfoMessage resets infoMessage state`() = runTest {
        viewModel.setDeviceName("test")
        assertTrue(viewModel.uiState.value.infoMessage != null)

        viewModel.clearInfoMessage()
        assertTrue(viewModel.uiState.value.infoMessage == null)
    }

    @Test
    fun `saveCustomColor calls use case with device address`() = runTest {
        val slot = CustomColorSlot(1, "ffffff")
        viewModel.saveCustomColor(slot.id, slot.hexColor)

        coVerify(exactly = 1) {
            saveCustomColorUseCase(
                device.address,
                slot
            )
        }
    }

    @Test
    fun `use case should NOT be called in saveCustomColor when selectedDevice is null`() = runTest {
        // Give
        val slot = CustomColorSlot(1, "ffffff")
        every { observeSelectedDeviceUseCase() } returns flowOf(null)

        // When
        createViewModel()
        viewModel.saveCustomColor(slot.id, slot.hexColor)

        // Then
        coVerify(exactly = 0) { saveCustomColorUseCase(device.address, slot) }
    }
}