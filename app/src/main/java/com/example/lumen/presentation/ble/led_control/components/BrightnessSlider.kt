package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN

@Composable
fun BrightnessSlider(
    initialBrightness: Float,
    onChangeBrightness: (Float) -> Unit,
) {
    var sliderPosition by rememberSaveable { mutableFloatStateOf(initialBrightness) }
    val sliderPercentage = ((sliderPosition.toInt() / BRIGHTNESS_MAX) * 100).toInt()

    Slider(
        modifier = Modifier.padding(end = 34.dp, start = 34.dp),
        valueRange = BRIGHTNESS_MIN..BRIGHTNESS_MAX,
        value = sliderPosition,
        onValueChange = {
            sliderPosition = it
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
                initialBrightness = 180f,
                onChangeBrightness = {}
            )
        }
    }
}