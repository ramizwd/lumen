package com.example.lumen.presentation.common.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing

@Composable
fun ListHeader(
    @StringRes header: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(MaterialTheme.spacing.small),
    ) {
        Text(
            text = stringResource(header),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@PreviewLightDark
@Composable
fun ListHeaderPreview() {
    LumenTheme {
        Surface {
            ListHeader(R.string.scanned)
        }
    }
}
