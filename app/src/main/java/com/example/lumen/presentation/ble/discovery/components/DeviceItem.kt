package com.example.lumen.presentation.ble.discovery.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun DeviceItem(
    device: BleDevice,
    onDeviceClick: (BleDevice) -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDeviceClick(device) }
        ) {
            Text("Name: ${device.name ?: "Unknown"}")
            Text("Address: ${device.address}")
        }
    }
}

@Preview(heightDp = 100, uiMode = UI_MODE_NIGHT_YES)
@Preview(heightDp = 100, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun DeviceItemPreview() {
    LumenTheme {
        Surface {
            DeviceItem(
                device = BleDevice(
                    name = "LED Test",
                    address = "00:11:22:33:44:55",
                ),
                onDeviceClick = {}
            )
        }
    }
}
