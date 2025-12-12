package com.example.lumen.presentation.ble.led_control.navigation

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    onNavIconClick: () -> Unit,
    onActionClick: () -> Unit,
    onClickTitle: () -> Unit,
    onLongClickTitle: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .combinedClickable(
                        onClick = onClickTitle,
                        onLongClick = onLongClickTitle
                    )
                    .padding(MaterialTheme.spacing.smallIncreased)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavIconClick) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back_24px),
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            TextButton(onClick = onActionClick) {
                Text(text = "Disconnect")
            }
        }
    )
}

@Composable
@PreviewLightDark
fun TopAppBarPreview() {
    LumenTheme {
        Surface {
            TopAppBar(
                title = "Test",
                onNavIconClick = {},
                onActionClick = {},
                onClickTitle = {},
                onLongClickTitle = {},
            )
        }
    }
}