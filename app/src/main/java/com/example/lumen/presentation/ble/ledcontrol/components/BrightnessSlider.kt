package com.example.lumen.presentation.ble.ledcontrol.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.R
import com.example.lumen.domain.ble.model.LedConstants.BRIGHTNESS_RANGE
import com.example.lumen.presentation.common.components.CustomSlider
import com.example.lumen.presentation.common.components.SliderOrientation
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.calculatePercentage
import com.example.lumen.utils.formatAsPercentage

@Composable
fun BrightnessSlider(
    enabled: Boolean,
    brightnessValue: Float,
    onChangeBrightness: (Float) -> Unit,
    modifier: Modifier = Modifier,
    orientation: SliderOrientation = SliderOrientation.VERTICAL,
) {
    val sliderPercentage = brightnessValue.calculatePercentage(BRIGHTNESS_RANGE.endInclusive)
    val percentageFormat = sliderPercentage.formatAsPercentage()

    val brightnessIcon =
        when (sliderPercentage) {
            in 80.1f..100f -> R.drawable.brightness_max_24px
            in 20.1f..80f -> R.drawable.brightness_medium_24px
            in 0.1f..20f -> R.drawable.brightness_low_24px
            else -> R.drawable.brightness_zero_24px
        }

    val textAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        label = "text_alpha",
    )

    val textColor by animateColorAsState(
        targetValue = if (enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            LocalContentColor.current
        },
        label = "text_color",
    )

    if (orientation == SliderOrientation.HORIZONTAL) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CustomSlider(
                enabled = enabled,
                value = brightnessValue,
                valueRange = BRIGHTNESS_RANGE,
                onValueChange = {
                    onChangeBrightness(it)
                },
                icon = brightnessIcon,
                iconDescription = stringResource(R.string.brightness),
                orientation = orientation,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = "$percentageFormat%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.alpha(textAlpha).weight(0.2f),
            )
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$percentageFormat%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.alpha(textAlpha),
            )

            CustomSlider(
                enabled = enabled,
                value = brightnessValue,
                valueRange = BRIGHTNESS_RANGE,
                onValueChange = {
                    onChangeBrightness(it)
                },
                icon = brightnessIcon,
                iconDescription = stringResource(R.string.brightness),
                orientation = orientation,
            )
        }
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
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun BrightnessSliderHorizontalPreview() {
    var sliderValue by remember { mutableFloatStateOf(180f) }

    LumenTheme {
        Surface {
            BrightnessSlider(
                enabled = true,
                brightnessValue = sliderValue,
                orientation = SliderOrientation.HORIZONTAL,
                onChangeBrightness = {
                    sliderValue = it
                },
            )
        }
    }
}
