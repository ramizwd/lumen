package com.example.lumen.presentation.ble.led_control

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.StaticLedColors
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun LedControlScreen(
    innerPadding: PaddingValues,
    connectedDevice: BleDevice?,
    onDisconnectClick: () -> Unit,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeStaticColorClick: (StaticLedColors) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "CONNECTED to ${connectedDevice?.name}")


        Button(onClick = onTurnLedOnClick) {
            Text(text = "Turn On")
        }
        Button(onClick = onTurnLedOffClick) {
            Text(text = "Turn Off")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            StaticLedColors.entries.forEach { color ->
                val bgColor = when(color) {
                    StaticLedColors.RED -> Color.Red
                    StaticLedColors.GREEN -> Color.Green
                    StaticLedColors.BLUE -> Color.Blue
                    StaticLedColors.YELLOW -> Color.Yellow
                    StaticLedColors.PURPLE -> Color.Magenta
                    StaticLedColors.CYAN -> Color.Cyan
                    StaticLedColors.WHITE -> Color.White
                }

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(40.dp)
                        .clip(shape = CircleShape)
                        .background(bgColor)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = CircleShape,
                        )
                        .clickable { onChangeStaticColorClick(color) }
                )
            }
        }

        Button(onClick = onDisconnectClick) {
            Text(text = "Disconnect")
        }

    }
}

@PreviewLightDark()
@Composable
fun LedControlScreenPreview() {
    LumenTheme {
        Surface {
            val connDevice = BleDevice(
                name = "Test device",
                address = "00:11:22:33:44:55"
            )

            LedControlScreen(
                innerPadding = PaddingValues(),
                connectedDevice = connDevice,
                onDisconnectClick = {},
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeStaticColorClick = {},
            )
        }
    }
}