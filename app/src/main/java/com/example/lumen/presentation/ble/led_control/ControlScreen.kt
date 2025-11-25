package com.example.lumen.presentation.ble.led_control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.ble.led_control.components.BrightnessSlider
import com.example.lumen.presentation.ble.led_control.components.LedToggleButton
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing

@Composable
fun ControlScreen(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOn = uiState.isLedOn
    val pixelCount = uiState.pixelCount
    val brightnessValue = uiState.brightnessValue

    ControlContent(
        isOn = isOn,
        pixelCount = pixelCount,
        brightnessValue = brightnessValue,
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
    brightnessValue: Float,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$pixelCount",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.width(MaterialTheme.spacing.smallIncreased))

            Column {
                Text(
                    text = "Pixels",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "in control",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        BrightnessSlider(
            modifier = Modifier
                .padding(MaterialTheme.spacing.large),
            enabled = isOn,
            brightnessValue = brightnessValue,
            onChangeBrightness = onChangeBrightness
        )

        LedToggleButton(
            isOn = isOn,
            onTurnLedOnClick = onTurnLedOnClick,
            onTurnLedOffClick = onTurnLedOffClick,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
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
                brightnessValue = 180f,
                isOn = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
            )
        }
    }
}