package com.example.lumen.presentation.ble.ledcontrol.components

import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.R
import com.example.lumen.presentation.common.components.PlainTooltip
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun CycleEffectsButton(
    enabled: Boolean,
    isAutoCycleOn: Boolean,
    onSetCycle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    PlainTooltip(
        text = stringResource(R.string.effect_auto_cycle),
        content = {
            FilledTonalIconToggleButton(
                modifier = modifier,
                enabled = enabled,
                checked = isAutoCycleOn,
                onCheckedChange = {
                    if (it) {
                        onSetCycle()
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                },
            ) {
                Icon(
                    painter =
                        if (isAutoCycleOn) {
                            painterResource(R.drawable.autorenew_semibold_24px)
                        } else {
                            painterResource(R.drawable.autorenew_24px)
                        },
                    contentDescription = stringResource(
                        R.string.effect_auto_cycle,
                    ),
                )
            }
        },
    )
}

@PreviewLightDark
@Composable
fun CycleEffectsButtonPreview() {
    LumenTheme {
        Surface {
            CycleEffectsButton(
                enabled = true,
                isAutoCycleOn = true,
                onSetCycle = { },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CycleEffectsButtonDisabledPreview() {
    LumenTheme {
        Surface {
            CycleEffectsButton(
                enabled = false,
                isAutoCycleOn = true,
                onSetCycle = { },
            )
        }
    }
}
