package com.example.lumen.presentation.ble.discovery

import androidx.compose.material3.SnackbarDuration
import app.cash.turbine.test
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.BluetoothPermissionStatus
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionResult
import com.example.lumen.domain.ble.model.DeviceListType
import com.example.lumen.domain.ble.model.ScanState
import com.example.lumen.domain.ble.usecase.common.ObserveBluetoothStateUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectToDeviceUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.domain.ble.usecase.connection.ObserveConnectionEventsUseCase
import com.example.lumen.domain.ble.usecase.discovery.DiscoveryUseCases
import com.example.lumen.domain.ble.usecase.discovery.ObserveScanErrorsUseCase
import com.example.lumen.domain.ble.usecase.discovery.ObserveScanResultsUseCase
import com.example.lumen.domain.ble.usecase.discovery.ObserveScanStateUseCase
import com.example.lumen.domain.ble.usecase.discovery.StartScanUseCase
import com.example.lumen.domain.ble.usecase.discovery.StopScanUseCase
import com.example.lumen.domain.ble.usecase.prefs.AddFavDeviceAddressUseCase
import com.example.lumen.domain.ble.usecase.prefs.GetDeviceListPreferenceUseCase
import com.example.lumen.domain.ble.usecase.prefs.GetFavDeviceAddressesUseCase
import com.example.lumen.domain.ble.usecase.prefs.PrefsUseCases
import com.example.lumen.domain.ble.usecase.prefs.RemoveFavDeviceAddressUseCase
import com.example.lumen.domain.ble.usecase.prefs.SaveDeviceListPreferenceUseCase
import io.mockk.clearMocks
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
 * Unit tests for [DiscoveryViewModel]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DiscoveryViewModelTest {

    private val device = BleDevice("Test", "00:11:22:33:44:55")
    private val favAddress = "66:11:22:33:44:55"
    private val favDevice = BleDevice("Fav", favAddress)

    private val scanResultFlow = MutableStateFlow(listOf(device, favDevice))
    private val scanStateFlow = MutableStateFlow(ScanState.SCAN_PAUSED)
    private var errorMessageFlow = MutableStateFlow("")
    private var favDeviceAddressFlow = MutableStateFlow(setOf(favAddress))
    private val listTypeFlow = MutableStateFlow(DeviceListType.ALL_DEVICES)
    private val connectionResultFlow = MutableSharedFlow<ConnectionResult>()
    private val bluetoothStateFlow = MutableStateFlow(BluetoothState.UNKNOWN)

    private lateinit var observeScanResultsUseCase: ObserveScanResultsUseCase
    private lateinit var observeScanStateUseCase:  ObserveScanStateUseCase
    private lateinit var observeScanErrorsUseCase: ObserveScanErrorsUseCase
    private lateinit var startScanUseCase: StartScanUseCase
    private lateinit var stopScanUseCase: StopScanUseCase
    private lateinit var observeConnectionEventsUseCase: ObserveConnectionEventsUseCase
    private lateinit var connectToDeviceUseCase: ConnectToDeviceUseCase
    private lateinit var getFavoriteDeviceAddressesUseCase: GetFavDeviceAddressesUseCase
    private lateinit var getDeviceListPreferenceUseCase: GetDeviceListPreferenceUseCase
    private lateinit var addFavDeviceAddressUseCase: AddFavDeviceAddressUseCase
    private lateinit var removeFavDeviceAddressUseCase: RemoveFavDeviceAddressUseCase
    private lateinit var saveDeviceListPreferenceUseCase: SaveDeviceListPreferenceUseCase
    private lateinit var observeBluetoothStateUseCase: ObserveBluetoothStateUseCase

    private lateinit var viewModel: DiscoveryViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        observeScanResultsUseCase = mockk()
        observeScanStateUseCase = mockk()
        observeScanErrorsUseCase = mockk()
        getFavoriteDeviceAddressesUseCase = mockk()
        getDeviceListPreferenceUseCase = mockk()
        observeConnectionEventsUseCase = mockk()
        observeBluetoothStateUseCase = mockk()
        startScanUseCase = mockk(relaxed = true)
        stopScanUseCase = mockk(relaxed = true)
        connectToDeviceUseCase = mockk(relaxed = true)
        addFavDeviceAddressUseCase = mockk(relaxed = true)
        removeFavDeviceAddressUseCase = mockk(relaxed = true)
        saveDeviceListPreferenceUseCase = mockk(relaxed = true)

        every { observeScanResultsUseCase() } returns scanResultFlow
        every { observeScanStateUseCase() } returns scanStateFlow
        every { observeScanErrorsUseCase() } returns errorMessageFlow
        every { getFavoriteDeviceAddressesUseCase() } returns favDeviceAddressFlow
        every { getDeviceListPreferenceUseCase() } returns listTypeFlow
        every { observeConnectionEventsUseCase() } returns connectionResultFlow
        every { observeBluetoothStateUseCase() } returns bluetoothStateFlow

        val discoveryUseCases = DiscoveryUseCases(
            observeScanStateUseCase,
            observeScanResultsUseCase,
            observeScanErrorsUseCase,
            startScanUseCase,
            stopScanUseCase
        )

        val connectionUseCases = ConnectionUseCases(
            observeConnectionStateUseCase = mockk(),
            observeSelectedDeviceUseCase = mockk(),
            disconnectUseCase = mockk(),
            connectToDeviceUseCase = connectToDeviceUseCase,
            observeConnectionEventsUseCase = observeConnectionEventsUseCase
        )

        val prefsUseCases = PrefsUseCases(
            getFavoriteDeviceAddressesUseCase,
            addFavDeviceAddressUseCase,
            removeFavDeviceAddressUseCase,
            getDeviceListPreferenceUseCase,
            saveDeviceListPreferenceUseCase
        )

        viewModel = DiscoveryViewModel(
            discoveryUseCases,
            connectionUseCases,
            prefsUseCases,
            observeBluetoothStateUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState correctly maps devices and ids favorites`() = runTest {
        val state = viewModel.uiState.value

        assertEquals(2, state.scanResults.size)
        assertTrue(state.scanResults.first { it.device.address == favAddress }.isFavorite)
        assertFalse(state.scanResults.first { it.device.address != favAddress }.isFavorite)
    }

    @Test
    fun `uiState filters only favorites when FAVORITE_DEVICES type is selected`() = runTest {
        listTypeFlow.emit(DeviceListType.FAVORITE_DEVICES)

        val state = viewModel.uiState.value

        assertEquals(1,state.scanResults.size)
        assertEquals(favAddress,state.scanResults.first().device.address)
    }

    @Test
    fun `uiState provides correct empty text message for scanning favorites`() = runTest {
        scanResultFlow.emit(emptyList())
        listTypeFlow.emit(DeviceListType.FAVORITE_DEVICES)
        scanStateFlow.emit(ScanState.SCANNING)

        assertEquals(
            "Searching for favorites...",
            viewModel.uiState.value.emptyScanResultTxt
        )
    }

    @Test
    fun `uiState reacts to list type changes`() = runTest {
        viewModel.uiState.test {
            awaitItem()

            listTypeFlow.emit(DeviceListType.FAVORITE_DEVICES)

            assertEquals(
                DeviceListType.FAVORITE_DEVICES,
                awaitItem().selectedListType
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init collects scan error messages and updates state`() = runTest {
        val errMsg = "Error"

        viewModel.uiState.test {
            awaitItem()

            errorMessageFlow.emit(errMsg)

            assertEquals(errMsg, awaitItem().errorMessage)
        }
    }

    @Test
    fun `init should call startScanUseCase when scan preconditions are met`() = runTest {
        scanStateFlow.emit(ScanState.SCAN_PAUSED)
        viewModel.onBtPermissionResult(granted = true, showRationale = false)
        bluetoothStateFlow.emit(BluetoothState.ON)

        coVerify(exactly = 1) { startScanUseCase() }
    }

    @Test
    fun `init should call stopScanUseCase when BT is turning off and is scanning`() = runTest {
        scanStateFlow.emit(ScanState.SCANNING)
        viewModel.onBtPermissionResult(granted = true, showRationale = false)
        bluetoothStateFlow.emit(BluetoothState.TURNING_OFF)

        verify(exactly = 1) { stopScanUseCase() }
    }

    @Test
    fun `infoMessage and errorMessage states update correctly`() = runTest {
        val errMsg = "Error"

        viewModel.uiState.test {
            awaitItem()

            connectionResultFlow.emit(ConnectionResult.Disconnected)
            assertEquals("Disconnected", awaitItem().infoMessage)

            connectionResultFlow.emit(ConnectionResult.Error(errMsg))
            assertEquals(errMsg,awaitItem().errorMessage)

            connectionResultFlow.emit(ConnectionResult.ConnectionEstablished)

            val state = awaitItem()
            assertNull(state.errorMessage)
            assertNull(state.infoMessage)
        }
    }

    @Test
    fun `show snackbar with proper message and action label when connection fails`() = runTest {
        val errMsg = "Error"

        viewModel.snackbarEvent.test {
            connectionResultFlow.emit(ConnectionResult.ConnectionFailed(errMsg))
            val state = awaitItem()

            assertEquals(errMsg, state.message)
            assertEquals("RETRY", state.actionLabel)
            assertEquals(SnackbarDuration.Long, state.duration)
        }
    }

    @Test
    fun `snackbar should not show when connection is established`() = runTest {
        viewModel.snackbarEvent.test {
            connectionResultFlow.emit(ConnectionResult.ConnectionEstablished)
            expectNoEvents()
        }
    }

    @Test
    fun `onBtPermissionResult updates btPermissionStatus state correctly`() = runTest {
        viewModel.onBtPermissionResult(granted = true, showRationale = false)
        assertEquals(
            BluetoothPermissionStatus.GRANTED,
            viewModel.uiState.value.btPermissionStatus
        )

        viewModel.onBtPermissionResult(granted = false, showRationale = true)
        assertEquals(
            BluetoothPermissionStatus.DENIED_RATIONALE_REQUIRED,
            viewModel.uiState.value.btPermissionStatus
        )

        viewModel.onBtPermissionResult(granted = false, showRationale = false)
        assertEquals(
            BluetoothPermissionStatus.DENIED_PERMANENTLY,
            viewModel.uiState.value.btPermissionStatus
        )
    }

    @Test
    fun `show proper dialog`() = runTest {
        viewModel.onEvent(DiscoverDevicesUiEvent.ToggleEnableBtDialog(true))
        assertEquals(
            true,
            viewModel.uiState.value.showEnableBtDialog
        )

        viewModel.onEvent(DiscoverDevicesUiEvent.ToggleOpenSettingsDialog(true))
        assertEquals(
            true,
            viewModel.uiState.value.showOpenSettingsDialog
        )

        viewModel.onEvent(DiscoverDevicesUiEvent.TogglePermissionDialog(true))
        assertEquals(
            true,
            viewModel.uiState.value.showPermissionDialog
        )
    }

    @Test
    fun `startScan calls use case if BT is enabled and perms granted`() = runTest {
        viewModel.onBtPermissionResult(granted = true, showRationale = false)
        bluetoothStateFlow.emit(BluetoothState.ON)

        clearMocks(startScanUseCase)

        viewModel.startScan()

        coVerify(exactly = 1) { startScanUseCase() }
    }

    @Test
    fun `stopScan calls use case if BT is enabled and perms granted`() = runTest {
        viewModel.onBtPermissionResult(granted = true, showRationale = false)
        bluetoothStateFlow.emit(BluetoothState.ON)

        viewModel.stopScan()

        verify(exactly = 1) { stopScanUseCase() }
    }

    @Test
    fun `connectToDevice calls use case`() = runTest {
        viewModel.connectToDevice(device)

        coVerify(exactly = 1) { connectToDeviceUseCase(any()) }
    }

    @Test
    fun `retryConnection updates errorMessage when no device is selected`() = runTest {
        assertEquals("", viewModel.uiState.value.errorMessage)

        viewModel.retryConnection()

        assertEquals(
            "No device to retry connection for",
            viewModel.uiState.value.errorMessage
        )

        coVerify(exactly = 0) { connectToDeviceUseCase(any()) }
    }

    @Test
    fun `retryConnection calls use case when device is available`() = runTest {
        viewModel.connectToDevice(device)

        clearMocks(connectToDeviceUseCase)

        viewModel.retryConnection()

        coVerify(exactly = 1) { connectToDeviceUseCase(device) }

        assertEquals("", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `clearInfoMessage resets state`() = runTest {
        connectionResultFlow.emit(ConnectionResult.Disconnected)

        assertNotNull(viewModel.uiState.value.infoMessage)

        viewModel.clearInfoMessage()

        assertNull(viewModel.uiState.value.infoMessage)
    }

    @Test
    fun `clearErrorMessage resets state`() = runTest {
        viewModel.retryConnection()

        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.clearErrorMessage()

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `addFavDevice calls use case`() = runTest {
        viewModel.addFavDevice(device.address)

        coVerify(exactly = 1) { addFavDeviceAddressUseCase(device.address) }
    }

    @Test
    fun `removeFavDevice calls use case`() = runTest {
        viewModel.removeFavDevice(device.address)

        coVerify(exactly = 1) { removeFavDeviceAddressUseCase(device.address) }
    }

    @Test
    fun `selectDeviceListType calls save use case only when type is different`() = runTest {
        listTypeFlow.emit(DeviceListType.ALL_DEVICES)

        viewModel.selectDeviceListType(DeviceListType.ALL_DEVICES)

        coVerify(exactly = 0) { saveDeviceListPreferenceUseCase(any()) }

        listTypeFlow.emit(DeviceListType.FAVORITE_DEVICES)

        viewModel.selectDeviceListType(DeviceListType.FAVORITE_DEVICES)

        coVerify(exactly = 0) { saveDeviceListPreferenceUseCase(any()) }
    }

    @Test
    fun `selectDeviceListType saves preference when switching types`() = runTest {
        listTypeFlow.emit(DeviceListType.ALL_DEVICES)

        viewModel.selectDeviceListType(DeviceListType.FAVORITE_DEVICES)

        coVerify(exactly = 1) {
            saveDeviceListPreferenceUseCase(DeviceListType.FAVORITE_DEVICES)
        }

        listTypeFlow.emit(DeviceListType.FAVORITE_DEVICES)

        viewModel.selectDeviceListType(DeviceListType.ALL_DEVICES)

        coVerify(exactly = 1) {
            saveDeviceListPreferenceUseCase(DeviceListType.ALL_DEVICES)
        }
    }

    @Test
    fun `stopScanUseCase is called when ViewModel is cleared`() = runTest {
        val method = viewModel.javaClass.superclass.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        verify(exactly = 1) { stopScanUseCase() }
    }
}