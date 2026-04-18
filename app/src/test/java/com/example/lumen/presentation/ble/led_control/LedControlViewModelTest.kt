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
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

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

    private val deviceFlow = MutableStateFlow<BleDevice?>(null)
    private val controllerStateFlow = MutableStateFlow<LedControllerState?>(null)
    private val brightnessFlow = MutableSharedFlow<Float>()
    private val customColorsFlow = MutableStateFlow<List<CustomColorSlot>>(emptyList())

    private lateinit var setDeviceNameUseCase: SetDeviceNameUseCase
    private lateinit var observeSelectedDeviceUseCase: ObserveSelectedDeviceUseCase
    private lateinit var observeControllerStateUseCase: ObserveControllerStateUseCase
    private lateinit var observeBrightnessUseCase: ObserveBrightnessUseCase
    private lateinit var getCustomColorsUseCase: GetCustomColorsUseCase
    private lateinit var turnLedOnOffUseCase: TurnLedOnOffUseCase
    private lateinit var changeBrightnessUseCase: ChangeBrightnessUseCase
    private lateinit var saveCustomColorUseCase: SaveCustomColorUseCase
    private lateinit var setLedColorUseCase: SetLedColorUseCase
    private lateinit var disconnectUseCase: DisconnectUseCase

    private lateinit var viewModel: LedControlViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        setDeviceNameUseCase = mockk()
        observeSelectedDeviceUseCase = mockk()
        observeControllerStateUseCase = mockk()
        observeBrightnessUseCase = mockk()
        getCustomColorsUseCase = mockk()
        turnLedOnOffUseCase = mockk(relaxed = true)
        changeBrightnessUseCase = mockk(relaxed = true)
        saveCustomColorUseCase = mockk(relaxed = true)
        setLedColorUseCase = mockk(relaxed = true)
        disconnectUseCase = mockk(relaxed = true)

        coEvery { setDeviceNameUseCase(any()) } returns Result.success(Unit)
        every { observeSelectedDeviceUseCase() } returns deviceFlow
        every { observeControllerStateUseCase() } returns controllerStateFlow
        every { observeBrightnessUseCase(any()) } returns brightnessFlow
        every { getCustomColorsUseCase(any()) } returns customColorsFlow

        deviceFlow.value = device
        controllerStateFlow.value = controllerState
        customColorsFlow.value = emptyList()

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
            turnLedOnOffUseCase,
            setLedColorUseCase,
            changeBrightnessUseCase,
            observeBrightnessUseCase,
            observeControllerStateUseCase,
            saveCustomColorUseCase,
            getCustomColorsUseCase,
        )

        viewModel = LedControlViewModel(connectionUseCases, controlUseCases, setDeviceNameUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads data into state`() = runTest {
        val state = viewModel.uiState.value

        assertEquals(device, state.selectedDevice)
        assertEquals(controllerState.isOn, state.isLedOn)
        assertEquals(
            controllerState.brightness, state.brightnessValue
        )
        assertEquals(
            controllerState.pixelCount, state.pixelCount
        )
        assertEquals(
            "${controllerState.red}${controllerState.green}${controllerState.blue}",
            state.ledHexColor
        )
    }

    @Test
    fun `init with null controller state sets default values`() = runTest {
        // Given
        controllerStateFlow.value = null

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
        val expectedState = listOf(CustomColorSlot(id = 1, hexColor = "ffffff"))
        customColorsFlow.value = expectedState

        assertEquals(expectedState, viewModel.uiState.value.customColorSlots)
    }

    @Test
    fun `init collects from brightness flow and calls changeBrightnessUseCase`() = runTest {
        // Given
        val value = 50f

        // When
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

        verify(exactly = 1) { disconnectUseCase() }
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
        assertNotNull(viewModel.uiState.value.infoMessage)

        viewModel.clearInfoMessage()
        assertNull(viewModel.uiState.value.infoMessage)
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
        deviceFlow.value = null

        // When
        createViewModel()
        viewModel.saveCustomColor(slot.id, slot.hexColor)

        // Then
        coVerify(exactly = 0) { saveCustomColorUseCase(device.address, slot) }
    }
}