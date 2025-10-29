package com.example.lumen.presentation.common.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.R

@Composable
fun DeviceFavoriteButton(
    isFavorite: Boolean,
    onFavor: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = {
            if (isFavorite) onRemove() else onFavor()
        },
    ) {
        Icon(
            painter = if (!isFavorite) painterResource(R.drawable.star_24px)
            else painterResource(R.drawable.star_filled_24px),
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
        )
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