package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.StaticLedColors
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun PresetColorRow(
    onChangeStaticColorClick: (StaticLedColors) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StaticLedColors.entries.forEach { color ->
            val bgColor = when(color) {
                StaticLedColors.RED -> Color.Red
                StaticLedColors.GREEN -> Color.Green
                StaticLedColors.BLUE -> Color.Blue
                StaticLedColors.YELLOW -> Color.Yellow
                StaticLedColors.PURPLE -> Color.Magenta
                StaticLedColors.CYAN -> Color.Cyan
                StaticLedColors.WHITE -> Color.White
            }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .clip(shape = CircleShape)
                    .background(bgColor)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = CircleShape,
                    )
                    .clickable { onChangeStaticColorClick(color) }
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PresetColorPreview() {
    LumenTheme {
        Surface { 
            PresetColorRow(
                onChangeStaticColorClick = {}
            )
        }
    }
}