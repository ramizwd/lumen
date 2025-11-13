package com.example.lumen.presentation.common.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltip(
    content: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    val positionProvider = rememberTooltipPositionProvider(positioning = TooltipAnchorPosition.Above)

    TooltipBox(
        modifier = modifier,
        positionProvider = positionProvider,
        tooltip = {
            PlainTooltip { Text(text = text) }
        },
        state = rememberTooltipState()
    ) {
        content()
    }
}