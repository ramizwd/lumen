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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.presentation.ble.led_control.components.BrightnessSlider
import com.example.lumen.presentation.ble.led_control.components.ColorPicker
import com.example.lumen.presentation.ble.led_control.components.LedSwitch
import com.example.lumen.presentation.ble.led_control.components.MatchDeviceThemeButton
import com.example.lumen.presentation.ble.led_control.components.PresetColorRow
import com.example.lumen.presentation.common.utils.ColorSaver
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.hexToComposeColor
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun LedControlScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: LedControlViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val selectedDevice = uiState.selectedDevice
    val initialColor = uiState.initialLedColor
    val initialBrightness = uiState.controllerState?.brightness ?: 0f
    val isOn = uiState.controllerState?.isOn ?: false

    LedControlContent(
        innerPadding = innerPadding,
        device = selectedDevice,
        initialColor = initialColor,
        initialBrightness = initialBrightness,
        isOn = isOn,
        onTurnLedOnClick = viewModel::turnLedOn,
        onTurnLedOffClick = viewModel::turnLedOff,
        setLedColor = viewModel::setLedColor,
        onChangeBrightness = viewModel::changeBrightness,
        onDisconnectClick = viewModel::disconnectFromDevice,
        modifier = modifier,
    )
}

@Composable
fun LedControlContent(
    innerPadding: PaddingValues,
    device: BleDevice?,
    initialColor: Color?,
    initialBrightness: Float,
    isOn: Boolean,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedColor: (String) -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorPickerController = rememberColorPickerController()

    var currentColor by rememberSaveable(stateSaver = ColorSaver) {
        mutableStateOf(initialColor)
    }

    LaunchedEffect(key1 = currentColor) {
        currentColor?.let { color ->
            colorPickerController.selectByColor(
                color = color,
                fromUser = false
            )
        }
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
                .height(450.dp)
                .padding(16.dp),
            controller = colorPickerController,
            onSetHsvColor = { hexColor ->
                currentColor = hexColor.hexToComposeColor()
                setLedColor(hexColor)
            }
        )

        MatchDeviceThemeButton(
            currentColor = currentColor,
            onMatchWithDeviceTheme = { hexColor ->
                currentColor = hexColor.hexToComposeColor()
                setLedColor(hexColor)
            }
        )

        PresetColorRow(
            currentColor = currentColor,
            onSetPresetColor = { hexColor ->
                currentColor = hexColor.hexToComposeColor()
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

            LedControlContent(
                innerPadding = PaddingValues(),
                device = connDevice,
                initialColor = Color.White,
                initialBrightness = 180f,
                isOn = true,
                onDisconnectClick = {},
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                setLedColor = {},
                onChangeBrightness = {},
            )
        }
    }
}