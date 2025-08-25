package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.common.components.PullToRefresh
import com.example.lumen.presentation.theme.LumenTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeviceList(
    scanResults: List<BleDevice>,
    onDeviceClick: (BleDevice) -> Unit,
    onStartScan: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    PullToRefresh(
        items = scanResults,
        content = { device ->
            DeviceItem(
                device = device,
                onDeviceClick = onDeviceClick
            )
        },
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                onStartScan()
                delay(1000) // show indicator for 1 sec.
                isRefreshing = false
            }
        },
    )
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
                onDeviceClick = {},
                onStartScan = {},
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
                onDeviceClick = {},
                onStartScan = {},
            )
        }
    }
}
