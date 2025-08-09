package com.example.lumen.presentation.ble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.ble.components.DeviceList
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun DiscoverDevicesScreen(
    innerPadding: PaddingValues,
    state: BleUiState,
    onStartScanClick: () -> Unit,
    onStopScanClick: () -> Unit
) {
    val isScanning = state.isScanning

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = if (isScanning) onStopScanClick else onStartScanClick) {
            Text(text = if (isScanning) "Stop Scan" else "Start Scan")
        }

        Text(
            text = "Devices:",
            modifier = Modifier.padding(top = 16.dp)
        )

        DeviceList(
            scanResults = state.scanResults,
            onDeviceClick = {}
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

            val state = BleUiState(
                scanResults = mockScanResults,
                isScanning = true
            )

            DiscoverDevicesScreen(
                innerPadding = PaddingValues(),
                state = state,
                onStartScanClick = {},
                onStopScanClick = {},
            )
        }
    }
}

@PreviewLightDark()
@Composable
fun DiscoverDevicesScreenWithoutDevicesPreview() {
    LumenTheme {
        Surface {
            val state = BleUiState(
                scanResults = emptyList(),
                isScanning = false
            )

            DiscoverDevicesScreen(
                innerPadding = PaddingValues(),
                state = state,
                onStartScanClick = {},
                onStopScanClick = {},
            )
        }
    }
}