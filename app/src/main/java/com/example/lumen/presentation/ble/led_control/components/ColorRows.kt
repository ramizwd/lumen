package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.presentation.common.utils.hexToComposeColor
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun ColorRows(
    enabled: Boolean,
    currentHexColor: String,
    presetColors: List<String>,
    selectedSlot: Int,
    customColorSlots: List<CustomColorSlot>,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    onColorSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    var isCustomColorActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = currentHexColor) {
        if (selectedSlot != 0 && isCustomColorActive && enabled) {
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
                    enabled = enabled,
                    onClick = {
                        isCustomColorActive = false
                        onColorSelected(0, color)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            customColorSlots.forEach { slot ->
                val isSlotSelected = selectedSlot == slot.id && isCustomColorActive

                ColorCircle(
                    color = slot.hexColor.hexToComposeColor(),
                    isSelected = isSlotSelected,
                    enabled = enabled,
                    onClick = {
                        if (isSlotSelected) {
                            isCustomColorActive = false
                            hapticFeedback
                                .performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                        } else {
                            isCustomColorActive = true
                            onColorSelected(slot.id, slot.hexColor)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        }
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
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (isSelected && enabled) {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }

    val color = if (enabled) color else color.copy(alpha = 0.4f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(54.dp)
            .clip(shape = CircleShape)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape,
            )
    ) {
        Box(
            modifier = modifier
                .size(24.dp)
                .clip(shape = CircleShape)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape,
                )
                .background(color)
                .clickable(enabled = enabled, onClick = onClick)
        )
    }
}

@PreviewLightDark
@Composable
fun ColorRowsPreview() {
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
                enabled = true,
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

@PreviewLightDark
@Composable
fun ColorRowsDisabledPreview() {
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
                enabled = false,
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