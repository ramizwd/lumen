package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.common.components.PullToRefresh
import com.example.lumen.presentation.common.model.DeviceContent
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeviceList(
    scanResults: List<DeviceContent>,
    emptyScanResultTxt: String?,
    onStartScan: () -> Unit,
    onFavDevice: (String) -> Unit,
    onRemoveDevice: (String) -> Unit,
    onDeviceClick: (BleDevice) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    PullToRefresh(
        modifier = modifier,
        items = scanResults,
        keySelector = { it.device.address },
        emptyContent = {
          if (emptyScanResultTxt != null) {
              EmptyScreenText(
                  modifier = Modifier
                      .padding(
                          start = MaterialTheme.spacing.large,
                          end = MaterialTheme.spacing.large
                      ),
                  text = emptyScanResultTxt
              )
          }
        },
        content = { deviceContent ->
            DeviceItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.large,
                        end = MaterialTheme.spacing.large
                    ),
                onDeviceClick = onDeviceClick,
                deviceContent = deviceContent,
                onFavDevice = onFavDevice,
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

@Composable
private fun EmptyScreenText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.secondary
        )
    }
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
                emptyScanResultTxt = null,
                onStartScan = {},
                onFavDevice = {},
                onRemoveDevice = {},
                onDeviceClick = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DeviceListInitialState() {
    LumenTheme {
        Surface {
            DeviceList(
                scanResults = emptyList(),
                emptyScanResultTxt = "Start scanning to find nearby devices.",
                onStartScan = {},
                onFavDevice = {},
                onRemoveDevice = {},
                onDeviceClick = {},
            )
        }
    }
}

@Preview(widthDp = 1200, heightDp = 800)
@Composable
fun DeviceListTabletLandscapePreview() {
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
                emptyScanResultTxt = null,
                onStartScan = {},
                onFavDevice = {},
                onRemoveDevice = {},
                onDeviceClick = {},
            )
        }
    }
}

@Preview(widthDp = 800, heightDp = 1200)
@Composable
fun DeviceListTabletPortraitPreview() {
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
                emptyScanResultTxt = null,
                onStartScan = {},
                onFavDevice = {},
                onRemoveDevice = {},
                onDeviceClick = {},
            )
        }
    }
}