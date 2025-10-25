package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.common.components.PullToRefresh
import com.example.lumen.presentation.common.model.DeviceContent
import com.example.lumen.presentation.theme.LumenTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeviceList(
    scanResults: List<DeviceContent>,
    onStartScan: () -> Unit,
    onSaveDevice: (String) -> Unit,
    onRemoveDevice: (String) -> Unit,
    onDeviceClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    PullToRefresh(
        items = scanResults,
        content = { deviceContent ->
            DeviceItem(
                modifier = modifier
                    .fillMaxWidth(),
                onDeviceClick = onDeviceClick,
                deviceContent = deviceContent,
                onSaveDevice = onSaveDevice,
                onRemoveDevice = onRemoveDevice,
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

@PreviewLightDark
@Composable
fun DeviceListPreview() {
    LumenTheme {
        Surface {
            val mockScanResults = listOf(
                DeviceContent(BleDevice(
                    name = "LED 1",
                    address = "00:11:22:33:44:55"),
                    isFavorite = true
                ),
                DeviceContent(BleDevice(
                    name = "Test Device 2",
                    address = "A:BB:CC:DD:EE:FF"),
                    isFavorite = false),
                DeviceContent(BleDevice(
                    name = null,
                    address = "FF:EE:DD:CC:BB:AA"),
                    isFavorite = true),
            )

            DeviceList(
                scanResults = mockScanResults,
                onStartScan = {},
                onSaveDevice = {},
                onRemoveDevice = {},
                onDeviceClick = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DeviceListWithoutDevicesPreview() {
    LumenTheme {
        Surface {
            DeviceList(
                scanResults = emptyList(),
                onStartScan = {},
                onSaveDevice = {},
                onRemoveDevice = {},
                onDeviceClick = {},
            )
        }
    }
}
