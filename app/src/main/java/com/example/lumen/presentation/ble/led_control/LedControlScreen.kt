package com.example.lumen.presentation.ble.led_control

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
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.domain.ble.model.StaticLedColors
import com.example.lumen.presentation.ble.led_control.components.BrightnessSlider
import com.example.lumen.presentation.ble.led_control.components.LedSwitch
import com.example.lumen.presentation.ble.led_control.components.PresetColorRow
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun LedControlScreen(
    innerPadding: PaddingValues,
    state: LedControlUiState,
    onDisconnectClick: () -> Unit,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeStaticColorClick: (StaticLedColors) -> Unit,
    onChangeBrightness: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "CONNECTED to ${state.connectedDevice?.name}")

        LedSwitch(
            isOn = state.controllerState?.isOn ?: false,
            onTurnLedOnClick = onTurnLedOnClick,
            onTurnLedOffClick = onTurnLedOffClick,
        )

        PresetColorRow(onChangeStaticColorClick = onChangeStaticColorClick)

        BrightnessSlider(
            currentBrightness = state.controllerState?.brightness ?: 0f,
            onChangeBrightness = onChangeBrightness
        )

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
                brightness = 180f,
                icModel = 1.toByte(),
                channel = 0.toByte(),
                pixelCount = 80,
                red = "FF",
                green = "00",
                blue = "00",
                whiteLedBrightness = 0.toByte()
            )

            val state = LedControlUiState(
                connectedDevice = connDevice,
                controllerState = controllerState,
            )

            LedControlScreen(
                innerPadding = PaddingValues(),
                state = state,
                onDisconnectClick = {},
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeStaticColorClick = {},
                onChangeBrightness = {},
            )
        }
    }
}