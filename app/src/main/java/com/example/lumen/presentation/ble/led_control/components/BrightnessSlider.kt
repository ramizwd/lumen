package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN

@Composable
fun BrightnessSlider(
    enabled: Boolean,
    brightnessValue: Float,
    onChangeBrightness: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sliderPercentage = ((brightnessValue.toInt() / BRIGHTNESS_MAX) * 100).toInt()

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Brightness",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = "${sliderPercentage}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Slider(
            enabled = enabled,
            valueRange = BRIGHTNESS_MIN..BRIGHTNESS_MAX,
            value = brightnessValue,
            onValueChange = {
                onChangeBrightness(it)
            },
        )
    }
}

@PreviewLightDark
@Composable
fun BrightnessSliderPreview() {
    var sliderValue by remember { mutableFloatStateOf(180f) }

    LumenTheme {
        Surface {
            BrightnessSlider(
                enabled = true,
                brightnessValue = sliderValue,
                onChangeBrightness = {
                    sliderValue = it
                }
            )
        }
    }
}