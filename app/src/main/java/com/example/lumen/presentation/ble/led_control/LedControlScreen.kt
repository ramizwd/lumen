package com.example.lumen.presentation.ble.led_control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.CustomColorSlot
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.presentation.ble.led_control.components.BrightnessSlider
import com.example.lumen.presentation.ble.led_control.components.ColorPicker
import com.example.lumen.presentation.ble.led_control.components.ColorRows
import com.example.lumen.presentation.ble.led_control.components.LedSwitch
import com.example.lumen.presentation.ble.led_control.components.MatchDeviceThemeButton
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.hexToComposeColor
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun LedControlScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: LedControlViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorPickerController = rememberColorPickerController()

    val selectedDevice = uiState.selectedDevice
    val initialHexColor = uiState.controllerState?.let { "${it.red}${it.green}${it.blue}" }
    val initialBrightness = uiState.controllerState?.brightness ?: 0f
    val isOn = uiState.controllerState?.isOn ?: false
    val presetColors = PresetLedColors.entries.map { it.hex }
    val customColorSlots = uiState.customColorSlots

    LedControlContent(
        innerPadding = innerPadding,
        colorPickerController = colorPickerController,
        device = selectedDevice,
        initialHexColor = initialHexColor,
        initialBrightness = initialBrightness,
        isOn = isOn,
        presetColors = presetColors,
        customColorSlots= customColorSlots,
        onTurnLedOnClick = viewModel::turnLedOn,
        onTurnLedOffClick = viewModel::turnLedOff,
        setLedColor = viewModel::setLedColor,
        onSaveCustomColorSlot = viewModel::saveCustomColor,
        onChangeBrightness = viewModel::changeBrightness,
        onDisconnectClick = viewModel::disconnectFromDevice,
        modifier = modifier,
    )
}

@Composable
fun LedControlContent(
    innerPadding: PaddingValues,
    colorPickerController: ColorPickerController,
    device: BleDevice?,
    initialHexColor: String?,
    initialBrightness: Float,
    isOn: Boolean,
    presetColors: List<String>,
    customColorSlots: List<CustomColorSlot>,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedColor: (String) -> Unit,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentHexColor by rememberSaveable { mutableStateOf(initialHexColor ?: "ffffff") }
    var selectedSlot by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key1 = currentHexColor) {
        colorPickerController.selectByColor(
            color = currentHexColor.hexToComposeColor(),
            fromUser = false
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "CONNECTED to ${device?.name ?: "Unknown"}")

        LedSwitch(
            isOn = isOn,
            onTurnLedOnClick = onTurnLedOnClick,
            onTurnLedOffClick = onTurnLedOffClick,
        )

        ColorPicker(
            modifier = modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            controller = colorPickerController,
            onSetHsvColor = { hexColor ->
                currentHexColor = hexColor
                setLedColor(hexColor)
            }
        )

        MatchDeviceThemeButton(
            currentColor = currentHexColor,
            onMatchWithDeviceTheme = { hexColor ->
                selectedSlot = 0
                currentHexColor = hexColor
                setLedColor(hexColor)
            }
        )

        ColorRows(
            currentHexColor = currentHexColor,
            presetColors = presetColors,
            selectedSlot = selectedSlot,
            customColorSlots = customColorSlots,
            onSaveCustomColorSlot = onSaveCustomColorSlot,
            onColorSelected = { slotId, hexColor ->
                selectedSlot = slotId
                currentHexColor = hexColor
                setLedColor(hexColor)
            },
        )

        BrightnessSlider(
            initialBrightness = initialBrightness,
            onChangeBrightness = onChangeBrightness
        )

        Button(onClick = onDisconnectClick) {
            Text(text = "Disconnect")
        }
    }
}

@PreviewLightDark
@Composable
fun LedControlScreenPreview() {
    LumenTheme {
        Surface {
            val connDevice = BleDevice(
                name = "Test device",
                address = "00:11:22:33:44:55"
            )
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

            LedControlContent(
                innerPadding = PaddingValues(),
                colorPickerController = rememberColorPickerController(),
                device = connDevice,
                initialHexColor = "ffffff",
                initialBrightness = 180f,
                isOn = true,
                presetColors = presetColors,
                customColorSlots = customColorsList,
                onDisconnectClick = {},
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                setLedColor = {},
                onSaveCustomColorSlot = { slotId, color -> },
                onChangeBrightness = {},
            )
        }
    }
}