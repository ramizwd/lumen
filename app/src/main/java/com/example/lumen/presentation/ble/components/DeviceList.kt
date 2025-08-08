package com.example.lumen.presentation.ble.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun DeviceList(
    scanResults: List<BleDevice>,
    onDeviceClick: (BleDevice) -> Unit
) {
    if (scanResults.isEmpty()) {
        Text("No devices found")
    } else {
        LazyColumn {
            items(scanResults) { device ->
                DeviceItem(
                    device = device,
                    onDeviceClick = onDeviceClick
                )
            }
        }
    }
}

@PreviewLightDark()
@Composable
fun DeviceListPreview() {
    LumenTheme {
        Surface {
            val mockScanResults = listOf(
                BleDevice(name = "LED 1", address = "00:11:22:33:44:55"),
                BleDevice(name = "Test Device 2", address = "A:BB:CC:DD:EE:FF"),
                BleDevice(name = null, address = "FF:EE:DD:CC:BB:AA")
            )

            DeviceList(
                scanResults = mockScanResults,
                onDeviceClick = {}
            )
        }
    }
}

@PreviewLightDark()
@Composable
fun DeviceListWithoutDevicesPreview() {
    LumenTheme {
        Surface {
            DeviceList(
                scanResults = emptyList(),
                onDeviceClick = {}
            )
        }
    }
}
