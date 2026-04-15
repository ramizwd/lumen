package com.example.lumen.domain.usecase.control

import com.example.lumen.domain.ble.BleGattController
import com.example.lumen.domain.ble.BleScanController
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.usecase.connection.ConnectToDeviceUseCase
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [ConnectToDeviceUseCase]
 */
class ConnectToDeviceUseCaseTest {

    private lateinit var mockBleGattController: BleGattController
    private lateinit var mockBleScanController: BleScanController
    private lateinit var connectToDeviceUseCase: ConnectToDeviceUseCase

    @BeforeEach
    fun setup() {
        mockBleGattController = mockk(relaxed = true)
        mockBleScanController = mockk(relaxed = true)
        connectToDeviceUseCase = ConnectToDeviceUseCase(
            mockBleGattController,
            mockBleScanController
        )
    }

    @Test
    fun `invoke stops scan and connects`() = runTest {
        // Given
        val device = BleDevice("test", "00:11:22:33:44:55")

        // When
        connectToDeviceUseCase(device)

        // Then
        coVerify(exactly = 1) { mockBleScanController.stopScan() }
        coVerify(exactly = 1) { mockBleGattController.connect(device) }
    }

    @Test
    fun `invoke ensures scan is stopped before connection starts`() = runTest {
        //Given
        val device = BleDevice("test", "00:11:22:33:44:55")

        // When
        connectToDeviceUseCase(device)

        // Then
        coVerifyOrder {
            mockBleScanController.stopScan()
            mockBleGattController.connect(device)
        }
    }
}