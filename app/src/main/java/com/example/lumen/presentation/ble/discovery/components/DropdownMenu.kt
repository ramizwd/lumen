package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun DropdownMenu(
    isExpanded: Boolean,
    onMenuClick: (Boolean) -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        IconButton(onClick = { onMenuClick(true) }) {
            Icon(
                painter = painterResource(R.drawable.more_vert_24px),
                contentDescription = stringResource(R.string.more_options),
                tint = MaterialTheme.colorScheme.outline,
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onMenuClick(false) },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.about)) },
                onClick = {
                    onMenuClick(false)
                    onAboutClick()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.info_24px),
                        contentDescription = stringResource(R.string.more_options),
                    )
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun DropdownMenuPreview() {
    LumenTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.padding(6.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                DropdownMenu(
                    isExpanded = true,
                    onMenuClick = { },
                    onAboutClick = { },
                )
            }
        }
    }
}
