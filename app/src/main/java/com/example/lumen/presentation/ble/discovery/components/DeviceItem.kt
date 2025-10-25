package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.common.components.DeviceFavoriteButton
import com.example.lumen.presentation.common.model.DeviceContent
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing

@Composable
fun DeviceItem(
    deviceContent: DeviceContent,
    onDeviceClick: (String) -> Unit,
    onFavDevice: (String) -> Unit,
    onRemoveDevice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val device = deviceContent.device
    val isFavorite = deviceContent.isFavorite
    val deviceName = device.name ?: "Unknown"
    val scrollState = rememberScrollState()

    Row (
        modifier = modifier
            .height(126.dp)
            .background(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainer
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable { onDeviceClick(device.address) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(MaterialTheme.spacing.largeIncreased),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
            Text(
                modifier = Modifier
                    .horizontalScroll(scrollState),
                text = deviceName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
            )
            Text(
                text = device.address,
                fontWeight = FontWeight.Light
            )
        }

        DeviceFavoriteButton(
            isFavorite = isFavorite,
            onFavor = { onFavDevice(device.address) },
            onRemove = { onRemoveDevice(device.address) } ,
            modifier = Modifier
                .padding(MaterialTheme.spacing.largeIncreased),
        )
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
                onDeviceClick = { },
                onFavDevice = { },
                onRemoveDevice = { },
            )
        }
    }
}
