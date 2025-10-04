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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lumen.domain.ble.model.BleDevice
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.domain.ble.model.PresetLedColors
import com.example.lumen.presentation.ble.led_control.components.BrightnessSlider
import com.example.lumen.presentation.ble.led_control.components.ColorPicker
import com.example.lumen.presentation.ble.led_control.components.LedSwitch
import com.example.lumen.presentation.ble.led_control.components.MatchDeviceThemeButton
import com.example.lumen.presentation.ble.led_control.components.PresetColorRow
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.utils.hexToComposeColor
import timber.log.Timber

private const val LOG_TAG = "LedControlScreen"

@Composable
fun LedControlScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: LedControlViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedControlContent(
        innerPadding = innerPadding,
        uiState = uiState,
        onTurnLedOnClick = viewModel::turnLedOn,
        onTurnLedOffClick = viewModel::turnLedOff,
        onChangePresetColorClick = viewModel::changePresetColor,
        onSetHsvColor = viewModel::setHsvColor,
        onChangeBrightness = viewModel::changeBrightness,
        onDisconnectClick = viewModel::disconnectFromDevice,
        modifier = modifier,
    )
}

@Composable
fun LedControlContent(
    innerPadding: PaddingValues,
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangePresetColorClick: (PresetLedColors) -> Unit,
    onSetHsvColor: (String) -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentColor = remember {
        uiState.controllerState?.let {
            val red = it.red
            val green = it.green
            val blue = it.blue
            val hexColor = "$red$green$blue"
            try {
                hexColor.hexToComposeColor()
            } catch (e: IllegalArgumentException) {
                Timber.tag(LOG_TAG)
                    .e(e, "Error converting to Compose color")
                Color.White
            }
        }
    }

    val currentBrightness = uiState.controllerState?.brightness ?: 0f
    val isOn = uiState.controllerState?.isOn ?: false

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "CONNECTED to ${uiState.selectedDevice?.name}")

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
            currentColor = currentColor,
            onSetHsvColor = onSetHsvColor,
        )

        MatchDeviceThemeButton(onSetHsvColor = onSetHsvColor)

        PresetColorRow(onChangePresetColorClick = onChangePresetColorClick)

        BrightnessSlider(
            currentBrightness = currentBrightness,
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

            val controllerState = LedControllerState(
                isOn = true,
                preset = 5.toByte(),
                speed = 50.toByte(),
                brightness = 180f,
                icModel = 1.toByte(),
                channel = 0.toByte(),
                pixelCount = 80,
                red = "FF",
                green = "00",
                blue = "00",
                whiteLedBrightness = 0.toByte()
            )

            val state = LedControlUiState(
                selectedDevice = connDevice,
                controllerState = controllerState,
            )

            LedControlContent(
                innerPadding = PaddingValues(),
                uiState = state,
                onDisconnectClick = {},
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangePresetColorClick = {},
                onSetHsvColor = {},
                onChangeBrightness = {},
            )
        }
    }
}