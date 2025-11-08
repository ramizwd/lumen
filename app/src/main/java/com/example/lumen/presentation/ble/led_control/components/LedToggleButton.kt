package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun LedToggleButton(
    isOn: Boolean,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    val animatedCornerRadius by animateDpAsState(
        targetValue = if (isOn) 16.dp else 28.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "LedButtonShapeAnimation"
    )

    val animatedShape = RoundedCornerShape(animatedCornerRadius)

    FilledIconToggleButton(
        modifier = modifier
            .size(65.dp),
        shape = animatedShape,
        checked = isOn,
        onCheckedChange = {
            if (it) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                onTurnLedOnClick()
            } else {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                onTurnLedOffClick()
            }
        },
    ) {
        Icon(
            painter = if (isOn) painterResource(R.drawable.power_settings_new_semibold_24px)
            else painterResource(R.drawable.power_settings_new_24px),
            contentDescription = if (isOn) "Turn LED off" else "Turn LED on"
        )
    }
}

@PreviewLightDark
@Composable
fun LedToggleButtonOnPreview() {
    LumenTheme {
        Surface {
            LedToggleButton(
                isOn = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
fun LedToggleButtonOffPreview() {
    LumenTheme {
        Surface {
            LedToggleButton(
                isOn = false,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {}
            )
        }
    }
}