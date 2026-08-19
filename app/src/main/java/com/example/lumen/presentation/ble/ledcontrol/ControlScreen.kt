package com.example.lumen.presentation.ble.ledcontrol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.R
import com.example.lumen.presentation.ble.ledcontrol.components.BrightnessSlider
import com.example.lumen.presentation.ble.ledcontrol.components.LedToggleButton
import com.example.lumen.presentation.common.components.DigitFieldDialog
import com.example.lumen.presentation.common.components.SliderOrientation
import com.example.lumen.presentation.common.utils.DeviceConfiguration
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing
import com.example.lumen.utils.AppConstants.MAX_LED_NUM
import com.example.lumen.utils.AppConstants.MIN_LED_NUM

@Composable
fun ControlScreen(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    setLedNum: (Int) -> Unit,
    onEvent: (LedControlUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOn = uiState.isLedOn
    val totalActivePixels = uiState.totalActivePixels
    val brightnessValue = uiState.brightnessValue

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val deviceConfig = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    if (uiState.showPixelControlDialog) {
        val textFieldState = rememberTextFieldState(initialText = totalActivePixels.toString())

        DigitFieldDialog(
            state = textFieldState,
            title = stringResource(R.string.set_active_pixel_range),
            supportingText = stringResource(
                R.string.pixel_range,
                MIN_LED_NUM,
                MAX_LED_NUM,
            ),
            onConfirmation = {
                setLedNum(it)
                onEvent(LedControlUiEvent.TogglePixelControlDialog(false))
            },
            onDismissRequest = {
                onEvent(LedControlUiEvent.TogglePixelControlDialog(false))
            },
        )
    }

    ControlContent(
        deviceConfig = deviceConfig,
        isOn = isOn,
        pixelCount = totalActivePixels,
        brightnessValue = brightnessValue,
        onPixelCountClick = { onEvent(LedControlUiEvent.TogglePixelControlDialog(true)) },
        onTurnLedOnClick = onTurnLedOnClick,
        onTurnLedOffClick = onTurnLedOffClick,
        onChangeBrightness = onChangeBrightness,
        modifier = modifier,
    )
}

@Composable
fun ControlContent(
    deviceConfig: DeviceConfiguration,
    isOn: Boolean,
    pixelCount: Int,
    brightnessValue: Float,
    onPixelCountClick: () -> Unit,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (deviceConfig) {
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.MOBILE_PORTRAIT,
        -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                PixelCountText(
                    enabled = isOn,
                    pixelCount,
                    onPixelCountClick,
                )

                BrightnessSlider(
                    enabled = isOn,
                    brightnessValue = brightnessValue,
                    onChangeBrightness = onChangeBrightness,
                )

                LedToggleButton(
                    isOn = isOn,
                    onTurnLedOnClick = onTurnLedOnClick,
                    onTurnLedOffClick = onTurnLedOffClick,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium),
                )
            }
        }

        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                PixelCountText(
                    enabled = isOn,
                    pixelCount,
                    onPixelCountClick,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    LedToggleButton(
                        isOn = isOn,
                        onTurnLedOnClick = onTurnLedOnClick,
                        onTurnLedOffClick = onTurnLedOffClick,
                        modifier = Modifier.padding(start = MaterialTheme.spacing.medium),
                    )

                    BrightnessSlider(
                        enabled = isOn,
                        brightnessValue = brightnessValue,
                        onChangeBrightness = onChangeBrightness,
                        orientation = SliderOrientation.HORIZONTAL,
                    )
                }
            }
        }
    }
}

@Composable
private fun PixelCountText(
    enabled: Boolean,
    pixelCount: Int,
    onPixelCountClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(
                onClick = onPixelCountClick,
                enabled = enabled,
            ).padding(MaterialTheme.spacing.smallIncreased)
            .alpha(alpha = if (enabled) 1f else 0.5f),
    ) {
        Text(
            text = "$pixelCount",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.width(MaterialTheme.spacing.smallIncreased))

        Column {
            Text(
                text = stringResource(R.string.active),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = pluralStringResource(R.plurals.pixel, pixelCount),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ControlContentPreview() {
    LumenTheme {
        Surface {
            ControlContent(
                deviceConfig = DeviceConfiguration.MOBILE_PORTRAIT,
                pixelCount = 26,
                brightnessValue = 180f,
                isOn = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
                onPixelCountClick = {},
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun ControlContentLandscapePreview() {
    LumenTheme {
        Surface {
            ControlContent(
                deviceConfig = DeviceConfiguration.MOBILE_LANDSCAPE,
                pixelCount = 26,
                brightnessValue = 180f,
                isOn = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
                onPixelCountClick = {},
            )
        }
    }
}
