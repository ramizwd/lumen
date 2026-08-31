package com.example.lumen.presentation.ble.ledcontrol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.R
import com.example.lumen.domain.ble.model.IcModel
import com.example.lumen.domain.ble.model.LedConstants.ACTIVE_PIXELS_RANGE
import com.example.lumen.domain.ble.model.LedConstants.STATIC_COLOR_VALUE
import com.example.lumen.domain.ble.model.RgbSequence
import com.example.lumen.presentation.ble.ledcontrol.components.BrightnessSlider
import com.example.lumen.presentation.ble.ledcontrol.components.HardwareConfigDialog
import com.example.lumen.presentation.ble.ledcontrol.components.LedToggleButton
import com.example.lumen.presentation.ble.ledcontrol.components.SpeedSlider
import com.example.lumen.presentation.ble.ledcontrol.components.WhiteLedBrightnessSlider
import com.example.lumen.presentation.common.components.DigitFieldDialog
import com.example.lumen.presentation.common.components.PlainTooltip
import com.example.lumen.presentation.common.components.SliderOrientation
import com.example.lumen.presentation.common.utils.DeviceConfiguration
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing

@Composable
fun ControlScreen(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onChangeWhiteBrightness: (Float) -> Unit,
    onSetEffectSpeed: (Float) -> Unit,
    setLedNum: (Int) -> Unit,
    setIcModel: (IcModel) -> Unit,
    setRgbSequence: (RgbSequence) -> Unit,
    onEvent: (LedControlUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOn = uiState.isLedOn
    val hasWhiteLed = uiState.hasWhiteLed
    val currentLedEffect = uiState.ledEffectValue
    val totalActivePixels = uiState.totalActivePixels
    val brightnessValue = uiState.brightnessValue
    val whiteLedBrightnessValue = uiState.whiteLedBrightnessValue
    val speedValue = uiState.speedValue

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val deviceConfig = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    if (uiState.showPixelControlDialog) {
        val textFieldState = rememberTextFieldState(initialText = totalActivePixels.toString())

        DigitFieldDialog(
            state = textFieldState,
            title = stringResource(R.string.set_active_pixel_range),
            supportingText = stringResource(
                R.string.pixel_range,
                ACTIVE_PIXELS_RANGE.first,
                ACTIVE_PIXELS_RANGE.last,
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

    if (uiState.showHardwareConfigDialog) {
        HardwareConfigDialog(
            deviceConfig = deviceConfig,
            currentIcModel = uiState.icModel,
            currentRgbSeq = uiState.rgbSeq,
            onIcModelChange = setIcModel,
            onRgbSeqChange = setRgbSequence,
            onDismissRequest = {
                onEvent(LedControlUiEvent.ToggleHardwareConfigDialog(false))
            },
        )
    }

    ControlContent(
        deviceConfig = deviceConfig,
        isOn = isOn,
        hasWhiteLed = hasWhiteLed,
        currentLedEffect = currentLedEffect,
        pixelCount = totalActivePixels,
        brightnessValue = brightnessValue,
        whiteLedBrightnessValue = whiteLedBrightnessValue,
        speedValue = speedValue,
        onPixelCountClick = { onEvent(LedControlUiEvent.TogglePixelControlDialog(true)) },
        onHardwareConfigClick = { onEvent(LedControlUiEvent.ToggleHardwareConfigDialog(true)) },
        onTurnLedOnClick = onTurnLedOnClick,
        onTurnLedOffClick = onTurnLedOffClick,
        onChangeBrightness = onChangeBrightness,
        onChangeWhiteBrightness = onChangeWhiteBrightness,
        onSetEffectSpeed = onSetEffectSpeed,
        modifier = modifier,
    )
}

@Composable
fun ControlContent(
    deviceConfig: DeviceConfiguration,
    isOn: Boolean,
    hasWhiteLed: Boolean,
    currentLedEffect: Int,
    pixelCount: Int,
    brightnessValue: Float,
    whiteLedBrightnessValue: Float,
    speedValue: Float,
    onPixelCountClick: () -> Unit,
    onHardwareConfigClick: () -> Unit,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    onChangeBrightness: (Float) -> Unit,
    onChangeWhiteBrightness: (Float) -> Unit,
    onSetEffectSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val landscapeSliderWidth = 70.dp
    val sliderSpacing = if (hasWhiteLed) {
        MaterialTheme.spacing.large
    } else {
        MaterialTheme.spacing.largeIncreased
    }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PixelCountText(
                        enabled = isOn,
                        pixelCount = pixelCount,
                        onPixelCountClick = onPixelCountClick,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrightnessSlider(
                        enabled = isOn,
                        brightnessValue = brightnessValue,
                        onChangeBrightness = onChangeBrightness,
                    )

                    if (hasWhiteLed) {
                        Spacer(modifier = Modifier.padding(sliderSpacing))
                        WhiteLedBrightnessSlider(
                            enabled = isOn,
                            brightnessValue = whiteLedBrightnessValue,
                            onChangeBrightness = onChangeWhiteBrightness,
                        )
                    }

                    Spacer(modifier = Modifier.padding(sliderSpacing))

                    SpeedSlider(
                        enabled = isOn && currentLedEffect != STATIC_COLOR_VALUE,
                        speedValue = speedValue,
                        onSetEffectSpeed = onSetEffectSpeed,
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    LedToggleButton(
                        isOn = isOn,
                        onTurnLedOnClick = onTurnLedOnClick,
                        onTurnLedOffClick = onTurnLedOffClick,
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium),
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = MaterialTheme.spacing.medium),
                    ) {
                        PlainTooltip(
                            text = stringResource(R.string.hardware_configuration),
                            content = {
                                FilledIconButton(
                                    onClick = onHardwareConfigClick,
                                    enabled = isOn,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.settings_24px),
                                        contentDescription =
                                            stringResource(R.string.hardware_configuration),
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }

        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PixelCountText(
                        enabled = isOn,
                        pixelCount = pixelCount,
                        onPixelCountClick = onPixelCountClick,
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = MaterialTheme.spacing.medium),
                    ) {
                        PlainTooltip(
                            text = stringResource(R.string.hardware_configuration),
                            content = {
                                FilledIconButton(
                                    onClick = onHardwareConfigClick,
                                    enabled = isOn,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.settings_24px),
                                        contentDescription =
                                            stringResource(R.string.hardware_configuration),
                                    )
                                }
                            },
                        )
                    }
                }

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

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        SpeedSlider(
                            enabled = isOn && currentLedEffect != STATIC_COLOR_VALUE,
                            speedValue = speedValue,
                            onSetEffectSpeed = onSetEffectSpeed,
                            orientation = SliderOrientation.HORIZONTAL,
                            modifier = Modifier.height(landscapeSliderWidth),
                        )

                        Spacer(modifier = Modifier.padding(MaterialTheme.spacing.small))

                        if (hasWhiteLed) {
                            WhiteLedBrightnessSlider(
                                enabled = isOn,
                                brightnessValue = whiteLedBrightnessValue,
                                onChangeBrightness = onChangeWhiteBrightness,
                                orientation = SliderOrientation.HORIZONTAL,
                                modifier = Modifier.height(landscapeSliderWidth),
                            )
                            Spacer(modifier = Modifier.padding(MaterialTheme.spacing.small))
                        }

                        BrightnessSlider(
                            enabled = isOn,
                            brightnessValue = brightnessValue,
                            onChangeBrightness = onChangeBrightness,
                            orientation = SliderOrientation.HORIZONTAL,
                            modifier = Modifier.height(landscapeSliderWidth),
                        )
                    }
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
                currentLedEffect = STATIC_COLOR_VALUE,
                pixelCount = 26,
                brightnessValue = 180f,
                speedValue = 100f,
                whiteLedBrightnessValue = 100f,
                isOn = true,
                hasWhiteLed = false,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
                onChangeWhiteBrightness = {},
                onSetEffectSpeed = {},
                onPixelCountClick = {},
                onHardwareConfigClick = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ControlContentAllSlidersPreview() {
    LumenTheme {
        Surface {
            ControlContent(
                deviceConfig = DeviceConfiguration.MOBILE_PORTRAIT,
                currentLedEffect = STATIC_COLOR_VALUE,
                pixelCount = 26,
                brightnessValue = 180f,
                speedValue = 100f,
                whiteLedBrightnessValue = 100f,
                isOn = true,
                hasWhiteLed = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
                onChangeWhiteBrightness = {},
                onSetEffectSpeed = {},
                onPixelCountClick = {},
                onHardwareConfigClick = {},
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
                currentLedEffect = STATIC_COLOR_VALUE,
                pixelCount = 26,
                brightnessValue = 180f,
                speedValue = 100f,
                whiteLedBrightnessValue = 100f,
                isOn = true,
                hasWhiteLed = false,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
                onChangeWhiteBrightness = {},
                onSetEffectSpeed = {},
                onPixelCountClick = {},
                onHardwareConfigClick = {},
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun ControlContentLandscapeAllSlidersPreview() {
    LumenTheme {
        Surface {
            ControlContent(
                deviceConfig = DeviceConfiguration.MOBILE_LANDSCAPE,
                currentLedEffect = STATIC_COLOR_VALUE,
                pixelCount = 26,
                brightnessValue = 180f,
                speedValue = 100f,
                whiteLedBrightnessValue = 100f,
                isOn = true,
                hasWhiteLed = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {},
                onChangeBrightness = {},
                onChangeWhiteBrightness = {},
                onSetEffectSpeed = {},
                onPixelCountClick = {},
                onHardwareConfigClick = {},
            )
        }
    }
}
