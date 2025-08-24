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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.domain.ble.model.StaticLedColors
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN

@Composable
fun LedControlScreen(
    innerPadding: PaddingValues,
    connectedDevice: BleDevice?,
    controllerState: LedControllerState,
    onDisconnectClick: () -> Unit,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeStaticColorClick: (StaticLedColors) -> Unit,
    onChangeBrightness: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val hapticFeedback = LocalHapticFeedback.current

        val isControllerOn = controllerState.isOn
        val currBrightness = controllerState.brightness.toFloat()

        var sliderPosition by remember { mutableFloatStateOf(currBrightness) }
        var checked by remember { mutableStateOf(isControllerOn) }

        Text(text = "CONNECTED to ${connectedDevice?.name}")

        Switch(
            checked = checked,
            onCheckedChange = {
                if (it) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    checked = true
                    onTurnLedOnClick()
                } else {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    checked = false
                    onTurnLedOffClick()
                }},
        )

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

        Slider(
            modifier = Modifier.padding(end = 34.dp, start = 34.dp),
            valueRange = BRIGHTNESS_MIN.toFloat()..BRIGHTNESS_MAX.toFloat(),
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onChangeBrightness(it.toInt())
            },
        )
        val sliderPercentage = ((sliderPosition / BRIGHTNESS_MAX) * 100).toInt()
        Text(text = "${sliderPercentage}%")

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

            val controllerState = LedControllerState(
                isOn = true,
                preset = 5.toByte(),
                speed = 50.toByte(),
                brightness = 180,
                icModel = 1.toByte(),
                channel = 0.toByte(),
                pixelCount = 80,
                red = "FF",
                green = "00",
                blue = "00",
                whiteLedBrightness = 0.toByte()
            )

            LedControlScreen(
                innerPadding = PaddingValues(),
                connectedDevice = connDevice,
                controllerState = controllerState,
                onDisconnectClick = {},
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeStaticColorClick = {},
                onChangeBrightness = {},
            )
        }
    }
}