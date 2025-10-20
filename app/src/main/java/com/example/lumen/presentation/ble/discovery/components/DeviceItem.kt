package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.common.model.DeviceContent
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun DeviceItem(
    deviceContent: DeviceContent,
    onSaveDevice: (String) -> Unit,
    onRemoveDevice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val device = deviceContent.device
    val isDeviceFav = deviceContent.isFavorite

    Row (modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Name: ${device.name ?: "Unknown"}")
            Text("Address: ${device.address}")
        }

        TextButton(
            onClick = {
                if (isDeviceFav) {
                    onRemoveDevice(device.address)
                } else {
                    onSaveDevice(device.address)
                }
            },
        ) {
            Text(if (isDeviceFav) "Forget" else "Favor")
        }
    }
}

@PreviewLightDark
@Composable
fun DeviceItemPreview() {
    LumenTheme {
        Surface {
            val mockDeviceContent = DeviceContent(BleDevice(
                name = "LED Test",
                address = "00:11:22:33:44:55"),
                isFavorite = false
            )

            DeviceItem(
                deviceContent = mockDeviceContent,
                onSaveDevice = { },
                onRemoveDevice = { },
            )
        }
    }
}
