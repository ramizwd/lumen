package com.example.lumen.presentation.ble.led_control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.presentation.ble.led_control.components.ColorPicker
import com.example.lumen.presentation.ble.led_control.components.ColorRows
import com.example.lumen.presentation.ble.led_control.components.LedSwitch
import com.example.lumen.presentation.ble.led_control.components.MatchDeviceThemeButton
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.hexToComposeColor
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

    LaunchedEffect(key1 = ledHexColor) {
        if (!isUsingColorPicker) {
            colorPickerController.selectByColor(
                color = ledHexColor.hexToComposeColor(),
                fromUser = false
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ColorPicker(
            modifier = modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
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
            }
        )

        MatchDeviceThemeButton(
            currentHexColor = ledHexColor,
            onMatchWithDeviceTheme = { hexColor ->
                selectedSlot = 0
                isUsingColorPicker = false
                setLedColor(hexColor)
            }
        )

        ColorRows(
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

        LedSwitch(
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
                onSaveCustomColorSlot = { slotId, color -> },
            )
        }
    }
}