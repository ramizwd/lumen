package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
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
import com.example.lumen.R
import com.example.lumen.presentation.common.components.VerticalSlider
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
    val sliderPercentage = ((brightnessValue.toInt() / BRIGHTNESS_MAX) * 100)

    val percentageFormat = when {
        sliderPercentage == 0f || sliderPercentage == 100f -> "%.0f".format(sliderPercentage)
        else -> "%.1f".format(sliderPercentage)
    }

    val brightnessIcon = when(sliderPercentage) {
        in 80.1f..100f -> R.drawable.brightness_max_24px
        in 20.1f..80f -> R.drawable.brightness_medium_24px
        in 0.1f..20f -> R.drawable.brightness_low_24px
        else -> R.drawable.brightness_zero_24px
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${percentageFormat}%",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        VerticalSlider(
            enabled = enabled,
            value = brightnessValue,
            valueRange = BRIGHTNESS_MIN..BRIGHTNESS_MAX,
            onValueChange = {
                onChangeBrightness(it)
            },
            icon = brightnessIcon,
            iconDescription = "Brightness",
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