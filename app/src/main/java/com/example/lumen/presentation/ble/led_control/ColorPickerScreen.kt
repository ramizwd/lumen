package com.example.lumen.presentation.ble.led_control

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.presentation.ble.led_control.components.ColorPicker
import com.example.lumen.presentation.ble.led_control.components.ColorRows
import com.example.lumen.presentation.ble.led_control.components.LedToggleButton
import com.example.lumen.presentation.ble.led_control.components.MatchDeviceThemeButton
import com.example.lumen.presentation.ble.led_control.components.RandomColorButton
import com.example.lumen.presentation.common.utils.hexToComposeColor
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPickerScreen(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedColor: (String) -> Unit,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorPickerController = rememberColorPickerController()

    val isOn = uiState.isLedOn
    val ledHexColor = uiState.ledHexColor
    val presetColors = PresetLedColors.entries.map { it.hex }
    val customColorSlots = uiState.customColorSlots

    ColorPickerContent(
        isOn = isOn,
        colorPickerController = colorPickerController,
        ledHexColor = ledHexColor,
        presetColors = presetColors,
        customColorSlots = customColorSlots,
        onTurnLedOnClick = onTurnLedOnClick,
        onTurnLedOffClick = onTurnLedOffClick,
        setLedColor = setLedColor,
        onSaveCustomColorSlot = onSaveCustomColorSlot,
        modifier = modifier,
    )
}

@Composable
fun ColorPickerContent(
    isOn: Boolean,
    colorPickerController: ColorPickerController,
    ledHexColor: String,
    presetColors: List<String>,
    customColorSlots: List<CustomColorSlot>,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedColor: (String) -> Unit,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isUsingColorPicker by remember { mutableStateOf(false) }
    var selectedSlot by rememberSaveable { mutableIntStateOf(0) }

    val transition = updateTransition(targetState = isOn)
    val glowRadius by transition.animateFloat { state ->
        when (state) {
            true -> 120f
            false -> 0f
        }
    }

    LaunchedEffect(key1 = ledHexColor) {
        if (!isUsingColorPicker) {
            colorPickerController.selectByColor(
                color = ledHexColor.hexToComposeColor(),
                fromUser = false
            )
        }
    }

    LaunchedEffect(isOn) {
        colorPickerController.enabled = isOn
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "#$ledHexColor".uppercase(),
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary
        )

        Column {
            ColorPicker(
                modifier = modifier
                    .fillMaxWidth()
                    .height(340.dp),
                controller = colorPickerController,
                onSetHsvColor = { hexColor ->
                    isUsingColorPicker = true
                    setLedColor(hexColor)
                },
                onStartInteraction = {
                    isUsingColorPicker = true
                },
                onEndInteraction = {
                    isUsingColorPicker = false
                },
                glowRadius = glowRadius,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.largeIncreased,
                        end = MaterialTheme.spacing.largeIncreased,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RandomColorButton(
                    enabled = isOn,
                    onClick = { hexColor ->
                        selectedSlot = 0
                        isUsingColorPicker = false
                        setLedColor(hexColor)
                    },
                )

                MatchDeviceThemeButton(
                    enabled = isOn,
                    currentHexColor = ledHexColor,
                    onMatchWithDeviceTheme = { hexColor ->
                        selectedSlot = 0
                        isUsingColorPicker = false
                        setLedColor(hexColor)
                    }
                )
            }
        }

        ColorRows(
            enabled = isOn,
            currentHexColor = ledHexColor,
            presetColors = presetColors,
            selectedSlot = selectedSlot,
            customColorSlots = customColorSlots,
            onSaveCustomColorSlot = onSaveCustomColorSlot,
            onColorSelected = { slotId, hexColor ->
                selectedSlot = slotId
                isUsingColorPicker = false
                setLedColor(hexColor)
            },
        )

        LedToggleButton(
            isOn = isOn,
            onTurnLedOnClick = onTurnLedOnClick,
            onTurnLedOffClick = onTurnLedOffClick,
        )
    }
}

@PreviewLightDark
@Composable
fun ColorPickerContentPreview() {
    LumenTheme {
        Surface {
            val presetColors = PresetLedColors.entries.map { it.hex }
            val customColorsList = listOf(
                CustomColorSlot(1, "ffffff"),
                CustomColorSlot(2, "ffffff"),
                CustomColorSlot(3, "32a852"),
                CustomColorSlot(4, "ffffff"),
                CustomColorSlot(5, "ffffff"),
                CustomColorSlot(6, "bc77d1"),
                CustomColorSlot(7, "ffffff"),
            )

            ColorPickerContent(
                isOn = true,
                colorPickerController = rememberColorPickerController(),
                ledHexColor = "ffffff",
                presetColors = presetColors,
                customColorSlots = customColorsList,
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                setLedColor = {},
                onSaveCustomColorSlot = { _, _ -> },
            )
        }
    }
}