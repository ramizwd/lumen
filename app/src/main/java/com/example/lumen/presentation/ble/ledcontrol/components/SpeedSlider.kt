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
import com.example.lumen.domain.ble.model.LedConstants.EFFECT_SPEED_RANGE
import com.example.lumen.presentation.common.components.CustomSlider
import com.example.lumen.presentation.common.components.SliderOrientation
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.calculatePercentage
import com.example.lumen.utils.formatAsPercentage

@Composable
fun SpeedSlider(
    enabled: Boolean,
    speedValue: Float,
    onSetEffectSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier,
    orientation: SliderOrientation = SliderOrientation.VERTICAL,
) {
    val sliderPercentage = speedValue.calculatePercentage(EFFECT_SPEED_RANGE.endInclusive)
    val percentageFormat = sliderPercentage.formatAsPercentage()

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
                value = speedValue,
                valueRange = EFFECT_SPEED_RANGE,
                onValueChange = {
                    onSetEffectSpeed(it)
                },
                icon = R.drawable.bolt_24px,
                iconDescription = stringResource(R.string.speed),
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
                value = speedValue,
                valueRange = EFFECT_SPEED_RANGE,
                onValueChange = {
                    onSetEffectSpeed(it)
                },
                icon = R.drawable.bolt_24px,
                iconDescription = stringResource(R.string.speed),
                orientation = orientation,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun SpeedSliderPreview() {
    var sliderValue by remember { mutableFloatStateOf(180f) }

    LumenTheme {
        Surface {
            SpeedSlider(
                enabled = true,
                speedValue = sliderValue,
                onSetEffectSpeed = {
                    sliderValue = it
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun SpeedSliderHorizontalPreview() {
    var sliderValue by remember { mutableFloatStateOf(180f) }

    LumenTheme {
        Surface {
            SpeedSlider(
                enabled = true,
                speedValue = sliderValue,
                orientation = SliderOrientation.HORIZONTAL,
                onSetEffectSpeed = {
                    sliderValue = it
                },
            )
        }
    }
}
