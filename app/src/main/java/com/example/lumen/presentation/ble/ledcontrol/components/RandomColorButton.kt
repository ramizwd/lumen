package com.example.lumen.presentation.ble.ledcontrol.components

import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import com.example.lumen.R
import com.example.lumen.presentation.common.components.PlainTooltip
import com.example.lumen.presentation.common.utils.nextColorHexString
import com.example.lumen.presentation.theme.LumenTheme
import java.util.Random

@Composable
fun RandomColorButton(
    enabled: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    PlainTooltip(
        text = stringResource(R.string.random_color),
        content = {
            FilledTonalIconButton(
                modifier = modifier,
                enabled = enabled,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onClick(Random().nextColorHexString())
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.dice_filled_24px),
                    contentDescription = stringResource(R.string.random_color),
                )
            }
        },
    )
}

@PreviewDynamicColors
@Composable
fun RandomColorButtonPreview() {
    LumenTheme {
        Surface {
            RandomColorButton(
                enabled = true,
                onClick = { },
            )
        }
    }
}

@PreviewDynamicColors
@Composable
fun RandomColorButtonDisabledPreview() {
    LumenTheme {
        Surface {
            RandomColorButton(
                enabled = false,
                onClick = { },
            )
        }
    }
}
