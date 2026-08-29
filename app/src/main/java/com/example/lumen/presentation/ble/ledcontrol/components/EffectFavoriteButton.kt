package com.example.lumen.presentation.ble.ledcontrol.components

import androidx.compose.material3.FilledIconToggleButton
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
fun EffectFavoriteButton(
    enabled: Boolean,
    isFavorite: Boolean,
    onFavor: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    PlainTooltip(
        text = stringResource(
            if (isFavorite) {
                R.string.remove_from_favorites
            } else {
                R.string.add_to_favorites
            },
        ),
        content = {
            FilledIconToggleButton(
                modifier = modifier,
                enabled = enabled,
                checked = isFavorite,
                onCheckedChange = {
                    if (it) {
                        onFavor()
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    } else {
                        onRemove()
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    }
                },
            ) {
                Icon(
                    painter =
                        if (isFavorite) {
                            painterResource(R.drawable.favorite_filled_24px)
                        } else {
                            painterResource(R.drawable.favorite_24px)
                        },
                    contentDescription = stringResource(
                        if (isFavorite) {
                            R.string.remove_from_favorites
                        } else {
                            R.string.add_to_favorites
                        },
                    ),
                )
            }
        },
    )
}

@PreviewLightDark
@Composable
fun EffectFavoriteButtonPreview() {
    LumenTheme {
        Surface {
            EffectFavoriteButton(
                enabled = true,
                isFavorite = false,
                onFavor = { },
                onRemove = { },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun EffectFavoriteButtonCheckedPreview() {
    LumenTheme {
        Surface {
            EffectFavoriteButton(
                enabled = true,
                isFavorite = true,
                onFavor = { },
                onRemove = { },
            )
        }
    }
}
