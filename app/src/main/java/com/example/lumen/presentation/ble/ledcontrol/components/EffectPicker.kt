package com.example.lumen.presentation.ble.ledcontrol.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lumen.R
import com.example.lumen.domain.ble.model.LedConstants.LED_EFFECT_RANGE
import com.example.lumen.domain.ble.model.LedConstants.STATIC_COLOR_VALUE
import com.example.lumen.presentation.common.components.CircularSlider
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun EffectPicker(
    enabled: Boolean,
    effectNumber: Int,
    onValueChange: (Int) -> Unit,
    currentLedEffect: Int,
    currentLedColor: Color,
    modifier: Modifier = Modifier,
) {
    val thumbColor by animateColorAsState(
        targetValue =
            when {
                currentLedEffect == STATIC_COLOR_VALUE && enabled -> currentLedColor
                enabled -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            },
        label = "active_track_color",
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        label = "content_alpha",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        CircularSlider(
            enabled = enabled,
            value = effectNumber,
            onValueChange = onValueChange,
            valueRange = LED_EFFECT_RANGE,
            indicatorCount = LED_EFFECT_RANGE.last,
            defaultValue = STATIC_COLOR_VALUE,
            thumbColor = thumbColor,
            modifier = Modifier.fillMaxSize(),
        )

        if (currentLedEffect == STATIC_COLOR_VALUE) {
            Text(
                text = stringResource(R.string.static_color),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.alpha(contentAlpha),
            )
        } else {
            Text(
                text = effectNumber.toString(),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.alpha(contentAlpha),
            )
        }
    }
}

@Preview(heightDp = 360)
@Composable
fun EffectPickerPreview() {
    LumenTheme {
        Surface {
            EffectPicker(
                enabled = true,
                effectNumber = STATIC_COLOR_VALUE,
                onValueChange = { },
                currentLedEffect = STATIC_COLOR_VALUE,
                currentLedColor = Color.Cyan,
            )
        }
    }
}

@Preview(heightDp = 360)
@Composable
fun EffectPickerActivePreview() {
    LumenTheme {
        Surface {
            EffectPicker(
                enabled = true,
                effectNumber = 10,
                onValueChange = { },
                currentLedEffect = 10,
                currentLedColor = Color.Cyan,
            )
        }
    }
}
