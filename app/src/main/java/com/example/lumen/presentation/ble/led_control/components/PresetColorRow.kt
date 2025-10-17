package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.hexToComposeColor

@Composable
fun ColorRows(
    currentHexColor: String,
    presetColors: List<String>,
    selectedSlot: Int,
    customColorSlots: List<CustomColorSlot>,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    onColorSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {

    LaunchedEffect(key1 = currentHexColor) {
        if (selectedSlot != 0) {
            val currSelectedSlot = customColorSlots.find { it.id == selectedSlot }

            if (currSelectedSlot != null &&
                currSelectedSlot.hexColor != currentHexColor) {
                onSaveCustomColorSlot(selectedSlot, currentHexColor)
            }
        }
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            presetColors.forEach { color ->
                val isSelected = currentHexColor == color && selectedSlot == 0

                ColorCircle(
                    color = color.hexToComposeColor(),
                    isSelected = isSelected,
                    onClick = {
                        onColorSelected(0, color)
                    }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            customColorSlots.forEach { slot ->
                val isSlotSelected = selectedSlot == slot.id

                ColorCircle(
                    color = slot.hexColor.hexToComposeColor(),
                    isSelected = isSlotSelected,
                    onClick = {
                        onColorSelected(slot.id, slot.hexColor)
                    }
                )
            }
        }
    }
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .size(40.dp)
            .clip(shape = CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 4.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            )
            .clickable(onClick = onClick)
    )
}

@PreviewLightDark
@Composable
fun PresetColorPreview() {
    LumenTheme {
        Surface {
            val customColorsList = listOf(
                CustomColorSlot(1, "ffffff"),
                CustomColorSlot(2, "ffffff"),
                CustomColorSlot(3, "32a852"),
                CustomColorSlot(4, "ffffff"),
                CustomColorSlot(5, "ffffff"),
                CustomColorSlot(6, "bc77d1"),
                CustomColorSlot(7, "ffffff"),
            )

            ColorRows(
                currentHexColor = "ffffff",
                presetColors = PresetLedColors.entries.map { it.hex },
                customColorSlots = customColorsList,
                onSaveCustomColorSlot = { slotId, color -> },
                onColorSelected = { slotId, color -> },
                selectedSlot = 0,
            )
        }
    }
}