package com.example.lumen.presentation.ble.ledcontrol.components

import android.graphics.Color
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import com.example.lumen.R
import com.example.lumen.presentation.common.components.PlainTooltip
import com.example.lumen.presentation.common.utils.toNoAlphaHexString
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun MatchDeviceThemeButton(
    enabled: Boolean,
    currentHexColor: String?,
    onMatchWithDeviceTheme: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary.toArgb()

    // convert input color to HSV and set the luminosity to max
    val hsv = FloatArray(3)
    Color.colorToHSV(onPrimaryColor, hsv)
    hsv[2] = 1f

    // convert to hex and drop the alpha value
    val maxBrightnessColorHex = Color.HSVToColor(hsv).toNoAlphaHexString()

    val isSelected = maxBrightnessColorHex == currentHexColor

    PlainTooltip(
        text = stringResource(R.string.match_with_device_theme),
        content = {
            FilledTonalIconToggleButton(
                modifier = modifier,
                enabled = enabled,
                checked = isSelected,
                onCheckedChange = {
                    if (!isSelected) {
                        onMatchWithDeviceTheme(maxBrightnessColorHex)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                },
            ) {
                Icon(
                    painter =
                        if (isSelected) {
                            painterResource(R.drawable.colorize_filled_24px)
                        } else {
                            painterResource(R.drawable.colorize_24px)
                        },
                    contentDescription = stringResource(R.string.match_with_device_theme),
                )
            }
        },
    )
}

@PreviewDynamicColors
@Composable
fun MatchDeviceThemeButtonPreview() {
    LumenTheme {
        Surface {
            MatchDeviceThemeButton(
                enabled = true,
                currentHexColor = "ffffff",
                onMatchWithDeviceTheme = { },
            )
        }
    }
}

@PreviewDynamicColors
@Composable
fun MatchDeviceThemeButtonDisabledPreview() {
    LumenTheme {
        Surface {
            MatchDeviceThemeButton(
                enabled = false,
                currentHexColor = "ffffff",
                onMatchWithDeviceTheme = { },
            )
        }
    }
}
