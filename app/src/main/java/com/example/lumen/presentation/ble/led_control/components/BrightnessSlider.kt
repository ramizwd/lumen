package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN

@Composable
fun BrightnessSlider(
    brightnessValue: Float,
    onChangeBrightness: (Float) -> Unit,
) {
    val sliderPercentage = ((brightnessValue.toInt() / BRIGHTNESS_MAX) * 100).toInt()

    Slider(
        modifier = Modifier.padding(end = 34.dp, start = 34.dp),
        valueRange = BRIGHTNESS_MIN..BRIGHTNESS_MAX,
        value = brightnessValue,
        onValueChange = {
            onChangeBrightness(it)
        },
    )
    Text(text = "${sliderPercentage}%")
}

@PreviewLightDark
@Composable
fun BrightnessSliderPreview() {
    LumenTheme {
        Surface {
            BrightnessSlider(
                brightnessValue = 180f,
                onChangeBrightness = {}
            )
        }
    }
}