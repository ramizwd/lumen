package com.example.lumen.presentation.ble.led_control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.ble.led_control.components.BrightnessSlider
import com.example.lumen.presentation.ble.led_control.components.LedSwitch
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun ControlScreen(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOn = uiState.controllerState?.isOn ?: false
    val pixelCount = uiState.controllerState?.pixelCount ?: 0
    val initialBrightness = uiState.controllerState?.brightness ?: 0f

    ControlContent(
        isOn = isOn,
        pixelCount = pixelCount,
        initialBrightness = initialBrightness,
        onTurnLedOnClick = onTurnLedOnClick,
        onTurnLedOffClick = onTurnLedOffClick,
        onChangeBrightness = onChangeBrightness,
        modifier = modifier,
    )

}

@Composable
fun ControlContent(
    isOn: Boolean,
    pixelCount: Int,
    initialBrightness: Float,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Controlling pixels: $pixelCount")

        BrightnessSlider(
            initialBrightness = initialBrightness,
            onChangeBrightness = onChangeBrightness
        )

        LedSwitch(
            isOn = isOn,
            onTurnLedOnClick = onTurnLedOnClick,
            onTurnLedOffClick = onTurnLedOffClick,
        )
    }
}

@PreviewLightDark
@Composable
fun ControlContentPreview() {
    LumenTheme {
        Surface {
            ControlContent(
                pixelCount = 26,
                initialBrightness = 180f,
                isOn = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
            )
        }
    }
}