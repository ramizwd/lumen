package com.example.lumen.presentation.ble.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.ble.discovery.components.DeviceList
import com.example.lumen.presentation.ble.discovery.components.ScanButton
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun DiscoverDevicesScreen(
    innerPadding: PaddingValues,
    state: DiscoveryUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnectToDevice: (String) -> Unit,
) {
    val isScanning = state.isScanning

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            DeviceList(
                scanResults = state.scanResults,
                onDeviceClick = {
                    onConnectToDevice(it.address)
                },
                onStartScan = onStartScan,
            )
        }

        ScanButton(
            isEnabled = state.bluetoothState == BluetoothState.ON,
            onStartScan = onStartScan,
            onStopScan = onStopScan,
            isScanning = isScanning
        )
    }
}

@PreviewLightDark()
@Composable
fun DiscoverDevicesScreenWithDevicesPreview() {
    LumenTheme {
        Surface {
            val mockScanResults = listOf(
                BleDevice(name = "LED 1", address = "00:11:22:33:44:55"),
                BleDevice(name = "Test Device 2", address = "A:BB:CC:DD:EE:FF"),
                BleDevice(name = null, address = "FF:EE:DD:CC:BB:AA")
            )

            val state = DiscoveryUiState(
                scanResults = mockScanResults,
                isScanning = true,
                connectionState = ConnectionState.DISCONNECTED
            )

            DiscoverDevicesScreen(
                innerPadding = PaddingValues(),
                state = state,
                onStartScan = {},
                onStopScan = {},
                onConnectToDevice = {},
            )
        }
    }
}

@PreviewLightDark()
@Composable
fun DiscoverDevicesScreenWithoutDevicesPreview() {
    LumenTheme {
        Surface {
            val state = DiscoveryUiState(
                scanResults = emptyList(),
                isScanning = false,
                connectionState = ConnectionState.CONNECTED
            )

            DiscoverDevicesScreen(
                innerPadding = PaddingValues(),
                state = state,
                onStartScan = {},
                onStopScan = {},
                onConnectToDevice = {},
            )
        }
    }
}