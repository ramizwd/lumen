package com.example.lumen.presentation.common.components

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun DeviceFavoriteButton(
    isFavorite: Boolean,
    onFavor: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        modifier = modifier,
        onClick = {
            if (isFavorite) onRemove() else onFavor()
        },
    ) {
        Text(if (isFavorite) "Forget" else "Favor")
    }
}

@PreviewLightDark
@Composable
fun DeviceFavoriteButtonFavorPreview() {
    LumenTheme {
        Surface {
            DeviceFavoriteButton(
                isFavorite = false,
                onFavor = { },
                onRemove = { },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DeviceFavoriteButtonForgetPreview() {
    LumenTheme {
        Surface {
            DeviceFavoriteButton(
                isFavorite = true,
                onFavor = { },
                onRemove = { },
            )
        }
    }
}