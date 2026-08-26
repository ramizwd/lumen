package com.example.lumen.presentation.ble.ledcontrol

import com.example.lumen.R
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.IcModel
import com.example.lumen.domain.ble.model.LedConstants.EFFECT_CYCLE_VALUE
import com.example.lumen.domain.ble.model.LedConstants.STATIC_COLOR_VALUE
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.domain.ble.model.RgbSequence
import com.example.lumen.domain.ble.usecase.config.ConfigUseCases
import com.example.lumen.domain.ble.usecase.config.SetDeviceNameUseCase
import com.example.lumen.domain.ble.usecase.config.SetIcModelUseCase
import com.example.lumen.domain.ble.usecase.config.SetLedNumUseCase
import com.example.lumen.domain.ble.usecase.config.SetRgbSequenceUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.connection.DisconnectUseCase
import com.example.lumen.domain.ble.usecase.connection.ObserveSelectedDeviceUseCase
import com.example.lumen.domain.ble.usecase.control.ChangeBrightnessUseCase
import com.example.lumen.domain.ble.usecase.control.ControlUseCases
import com.example.lumen.domain.ble.usecase.control.GetCustomColorsUseCase
import com.example.lumen.domain.ble.usecase.control.ObserveBrightnessUseCase
import com.example.lumen.domain.ble.usecase.control.ObserveControllerStateUseCase
import com.example.lumen.domain.ble.usecase.control.ObserveEffectSpeedUseCase
import com.example.lumen.domain.ble.usecase.control.SaveCustomColorUseCase
import com.example.lumen.domain.ble.usecase.control.SetEffectCycleUseCase
import com.example.lumen.domain.ble.usecase.control.SetEffectSpeedUseCase
import com.example.lumen.domain.ble.usecase.control.SetLedColorUseCase
import com.example.lumen.domain.ble.usecase.control.SetLedEffectUseCase
import com.example.lumen.domain.ble.usecase.control.TurnLedOnOffUseCase
import com.example.lumen.presentation.common.utils.UiText
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
    private val controllerState =
        LedControllerState(
            isOn = true,
            preset = 10,
            speed = 1f,
            brightness = 50f,
            icModel = IcModel.TM1804,
            rgbSeq = RgbSequence.RGB,
            totalActivePixels = 50,
            red = "ff",
            green = "00",
            blue = "00",
            whiteLedBrightness = 0,
        )

    private val deviceFlow = MutableStateFlow<BleDevice?>(null)
    private val controllerStateFlow = MutableStateFlow<LedControllerState?>(null)
    private val brightnessFlow = MutableSharedFlow<Float>()
    private val speedFlow = MutableSharedFlow<Float>()
    private val customColorsFlow = MutableStateFlow<List<CustomColorSlot>>(emptyList())

    private lateinit var setDeviceNameUseCase: SetDeviceNameUseCase
    private lateinit var setLedNumUseCase: SetLedNumUseCase
    private lateinit var observeSelectedDeviceUseCase: ObserveSelectedDeviceUseCase
    private lateinit var observeControllerStateUseCase: ObserveControllerStateUseCase
    private lateinit var observeBrightnessUseCase: ObserveBrightnessUseCase
    private lateinit var observeEffectSpeedUseCase: ObserveEffectSpeedUseCase
    private lateinit var getCustomColorsUseCase: GetCustomColorsUseCase
    private lateinit var turnLedOnOffUseCase: TurnLedOnOffUseCase
    private lateinit var changeBrightnessUseCase: ChangeBrightnessUseCase
    private lateinit var setEffectSpeedUseCase: SetEffectSpeedUseCase
    private lateinit var saveCustomColorUseCase: SaveCustomColorUseCase
    private lateinit var setLedColorUseCase: SetLedColorUseCase
    private lateinit var setLedEffectUseCase: SetLedEffectUseCase
    private lateinit var setEffectCycleUseCase: SetEffectCycleUseCase
    private lateinit var setIcModelUseCase: SetIcModelUseCase
    private lateinit var setRgbSequenceUseCase: SetRgbSequenceUseCase
    private lateinit var disconnectUseCase: DisconnectUseCase

    private lateinit var viewModel: LedControlViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        setDeviceNameUseCase = mockk()
        setLedNumUseCase = mockk()
        setLedEffectUseCase = mockk()
        setEffectCycleUseCase = mockk(relaxed = true)
        observeSelectedDeviceUseCase = mockk()
        observeControllerStateUseCase = mockk()
        observeBrightnessUseCase = mockk()
        observeEffectSpeedUseCase = mockk()
        getCustomColorsUseCase = mockk()
        turnLedOnOffUseCase = mockk(relaxed = true)
        changeBrightnessUseCase = mockk(relaxed = true)
        setEffectSpeedUseCase = mockk(relaxed = true)
        saveCustomColorUseCase = mockk(relaxed = true)
        setLedColorUseCase = mockk(relaxed = true)
        setIcModelUseCase = mockk(relaxed = true)
        setRgbSequenceUseCase = mockk(relaxed = true)
        disconnectUseCase = mockk(relaxed = true)

        coEvery { setDeviceNameUseCase(any()) } returns Result.success(Unit)
        coEvery { setLedNumUseCase(any()) } returns Result.success(Unit)
        coEvery { setLedEffectUseCase(any()) } returns Result.success(Unit)
        every { observeSelectedDeviceUseCase() } returns deviceFlow
        every { observeControllerStateUseCase() } returns controllerStateFlow
        every { observeBrightnessUseCase(any()) } returns brightnessFlow
        every { observeEffectSpeedUseCase(any()) } returns speedFlow
        every { getCustomColorsUseCase(any()) } returns customColorsFlow

        deviceFlow.value = device
        controllerStateFlow.value = controllerState
        customColorsFlow.value = emptyList()

        createViewModel()
    }

    private fun createViewModel() {
        val connectionUseCases =
            ConnectionUseCases(
                connectToDeviceUseCase = mockk(),
                observeConnectionStateUseCase = mockk(),
                observeConnectionEventsUseCase = mockk(),
                observeSelectedDeviceUseCase = observeSelectedDeviceUseCase,
                disconnectUseCase = disconnectUseCase,
            )

        val controlUseCases =
            ControlUseCases(
                turnLedOnOffUseCase,
                setLedColorUseCase,
                changeBrightnessUseCase,
                observeBrightnessUseCase,
                observeEffectSpeedUseCase,
                observeControllerStateUseCase,
                saveCustomColorUseCase,
                getCustomColorsUseCase,
                setLedEffectUseCase,
                setEffectSpeedUseCase,
                setEffectCycleUseCase = setEffectCycleUseCase,
            )

        val configUseCases = ConfigUseCases(
            setDeviceNameUseCase,
            setLedNumUseCase,
            setIcModelUseCase,
            setRgbSequenceUseCase,
        )

        viewModel = LedControlViewModel(
            connectionUseCases,
            controlUseCases,
            configUseCases,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads data into state`() =
        runTest {
            val state = viewModel.uiState.value

            assertEquals(device, state.selectedDevice)
            assertEquals(controllerState.isOn, state.isLedOn)
            assertEquals(
                controllerState.brightness,
                state.brightnessValue,
            )
            assertEquals(
                controllerState.totalActivePixels,
                state.totalActivePixels,
            )
            assertEquals(
                "${controllerState.red}${controllerState.green}${controllerState.blue}",
                state.ledHexColor,
            )
            assertEquals(
                UiText.DynamicString(controllerState.preset.toString()),
                state.effectPickerTxt,
            )
        }

    @Test
    fun `init with null controller state sets default values`() =
        runTest {
            // Given
            controllerStateFlow.value = null

            // When
            createViewModel()

            // Then
            assertFalse(viewModel.uiState.value.isLedOn)
            assertEquals("ffffff", viewModel.uiState.value.ledHexColor)
            assertEquals(0f, viewModel.uiState.value.brightnessValue)
            assertEquals(0, viewModel.uiState.value.totalActivePixels)
        }

    @Test
    fun `init collects custom colors for device`() =
        runTest {
            val expectedState = listOf(CustomColorSlot(id = 1, hexColor = "ffffff"))
            customColorsFlow.value = expectedState

            assertEquals(expectedState, viewModel.uiState.value.customColorSlots)
        }

    @Test
    fun `init collects from brightness flow and calls changeBrightnessUseCase`() =
        runTest {
            // Given
            val value = 50f

            // When
            brightnessFlow.emit(value)

            // Then
            coVerify(exactly = 1) { changeBrightnessUseCase(value) }
        }

    @Test
    fun `init collects from speed flow and calls setEffectSpeedUseCase`() =
        runTest {
            // Given
            val value = 50f

            // When
            speedFlow.emit(value)

            // Then
            coVerify(exactly = 1) { setEffectSpeedUseCase(value) }
        }

    @Test
    fun `turnLedOn updates state and calls use case`() =
        runTest {
            // When
            viewModel.turnLedOn()

            // Then
            assertTrue(viewModel.uiState.value.isLedOn)
            coVerify(exactly = 1) { turnLedOnOffUseCase(true) }
        }

    @Test
    fun `turnLedOff updates state and calls use case`() =
        runTest {
            viewModel.turnLedOff()

            assertTrue(!viewModel.uiState.value.isLedOn)
            coVerify(exactly = 1) { turnLedOnOffUseCase(false) }
        }

    @Test
    fun `setLedColor updates state and calls use case`() =
        runTest {
            val expectedHex = "ff00ff"
            val expectedEffectVal = STATIC_COLOR_VALUE
            viewModel.setLedColor(expectedHex)

            assertEquals(expectedHex, viewModel.uiState.value.ledHexColor)
            assertEquals(expectedEffectVal, viewModel.uiState.value.ledEffectValue)
            assertEquals(
                UiText.StringResource(R.string.static_color),
                viewModel.uiState.value.effectPickerTxt,
            )
            coVerify(exactly = 1) { setLedColorUseCase(expectedHex) }
        }

    @Test
    fun `setLedEffect on success updates state and calls use case`() =
        runTest {
            val expected = 2
            viewModel.setLedEffect(expected)

            assertEquals(expected, viewModel.uiState.value.ledEffectValue)
            assertEquals(
                UiText.DynamicString(expected.toString()),
                viewModel.uiState.value.effectPickerTxt,
            )
            coVerify(exactly = 1) { setLedEffectUseCase(expected) }
        }

    @Test
    fun `setEffectCycle updates state and calls use case`() =
        runTest {
            viewModel.setEffectCycle()

            assertEquals(
                EFFECT_CYCLE_VALUE,
                viewModel.uiState.value.ledEffectValue
            )
            assertEquals(
                UiText.StringResource(R.string.cycle),
                viewModel.uiState.value.effectPickerTxt,
            )
            coVerify(exactly = 1) { setEffectCycleUseCase() }
        }

    @Test
    fun `setLedEffect on failure updates infoMessage state with error text`() =
        runTest {
            // Given
            coEvery { setLedEffectUseCase(any()) } returns
                Result.failure(Exception("Error"))

            // When
            viewModel.setLedEffect(200) // out of range (1-120) value

            // Then
            assertEquals(
                UiText.StringResource(R.string.error_setting_effect),
                viewModel.uiState.value.infoMessage,
            )
        }

    @Test
    fun `setEffectSpeed on failure updates infoMessage state`() =
        runTest {
            // Given
            coEvery { setEffectSpeedUseCase(any()) } returns
                Result.failure(Exception("Error"))

            // When
            speedFlow.emit(200f)

            // Then
            assertEquals(
                UiText.StringResource(R.string.error_adjusting_effect_speed),
                viewModel.uiState.value.infoMessage,
            )
        }

    @Test
    fun `ToggleRenameDeviceDialog updates dialog visibility state`() =
        runTest {
            viewModel.onEvent(LedControlUiEvent.ToggleRenameDeviceDialog(true))
            assertTrue(viewModel.uiState.value.showRenameDeviceDialog)

            viewModel.onEvent(LedControlUiEvent.ToggleRenameDeviceDialog(false))
            assertFalse(viewModel.uiState.value.showRenameDeviceDialog)
        }

    @Test
    fun `ToggleHardwareConfigDialog updates dialog visibility state`() =
        runTest {
            viewModel.onEvent(LedControlUiEvent.ToggleHardwareConfigDialog(true))
            assertTrue(viewModel.uiState.value.showHardwareConfigDialog)

            viewModel.onEvent(LedControlUiEvent.ToggleHardwareConfigDialog(false))
            assertFalse(viewModel.uiState.value.showHardwareConfigDialog)
        }

    @Test
    fun `disconnectFromDevice calls disconnectUseCase`() =
        runTest {
            viewModel.disconnectFromDevice()

            verify(exactly = 1) { disconnectUseCase() }
        }

    @Test
    fun `setDeviceName on success updates infoMessage state with success text`() =
        runTest {
            viewModel.setDeviceName("New name")

            assertEquals(
                UiText.StringResource(R.string.device_renamed),
                viewModel.uiState.value.infoMessage,
            )
        }

    @Test
    fun `setDeviceName on failure updates infoMessage state with error text`() =
        runTest {
            // Given
            coEvery { setDeviceNameUseCase(any()) } returns
                Result.failure(Exception("Error"))

            // When
            viewModel.setDeviceName("New name")

            // Then
            assertEquals(
                UiText.StringResource(R.string.error_renaming_device),
                viewModel.uiState.value.infoMessage,
            )
        }

    @Test
    fun `setDeviceName returns an error message if device name is null`() =
        runTest {
            // Given
            coEvery { setDeviceNameUseCase(any()) } returns Result.failure(Exception("Error"))

            // When
            viewModel.setDeviceName("")

            // Then
            assertEquals(
                UiText.StringResource(R.string.error_renaming_device),
                viewModel.uiState.value.infoMessage,
            )
        }

    @Test
    fun `setLedNum on failure updates infoMessage state with error text`() =
        runTest {
            // Given
            coEvery { setLedNumUseCase(any()) } returns
                Result.failure(Exception("Error"))

            // When
            viewModel.setLedNum(1)

            // Then
            assertEquals(
                UiText.StringResource(R.string.error_setting_pixel_count),
                viewModel.uiState.value.infoMessage,
            )
        }

    @Test
    fun `setLedNum returns an error message if LED num is invalid`() =
        runTest {
            // Given
            coEvery { setLedNumUseCase(any()) } returns Result.failure(Exception("Error"))

            // When
            viewModel.setLedNum(0) // valid range: 1 - 1024

            // Then
            assertEquals(
                UiText.StringResource(R.string.error_setting_pixel_count),
                viewModel.uiState.value.infoMessage,
            )
        }

    @Test
    fun `clearInfoMessage resets infoMessage state`() =
        runTest {
            viewModel.setDeviceName("test")
            assertNotNull(viewModel.uiState.value.infoMessage)

            viewModel.clearInfoMessage()
            assertNull(viewModel.uiState.value.infoMessage)
        }

    @Test
    fun `saveCustomColor calls use case with device address`() =
        runTest {
            val slot = CustomColorSlot(1, "ffffff")
            viewModel.saveCustomColor(slot.id, slot.hexColor)

            coVerify(exactly = 1) {
                saveCustomColorUseCase(
                    device.address,
                    slot,
                )
            }
        }

    @Test
    fun `use case should NOT be called in saveCustomColor when selectedDevice is null`() =
        runTest {
            // Give
            val slot = CustomColorSlot(1, "ffffff")
            deviceFlow.value = null

            // When
            createViewModel()
            viewModel.saveCustomColor(slot.id, slot.hexColor)

            // Then
            coVerify(exactly = 0) { saveCustomColorUseCase(device.address, slot) }
        }

    @Test
    fun `setIcModel updates state and calls use case`() =
        runTest {
            val expected = IcModel.SK6812
            viewModel.setIcModel(expected)

            assertEquals(expected, viewModel.uiState.value.icModel)
            coVerify(exactly = 1) { setIcModelUseCase(expected) }
        }

    @Test
    fun `setRgbSequence updates state and calls use case`() =
        runTest {
            val expected = RgbSequence.BGR
            viewModel.setRgbSequence(expected)

            assertEquals(expected, viewModel.uiState.value.rgbSeq)
            coVerify(exactly = 1) { setRgbSequenceUseCase(expected) }
        }
}
