package com.example.lumen.presentation.ble.ledcontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lumen.R
import com.example.lumen.domain.ble.model.LedConstants.EFFECT_CYCLE_VALUE
import com.example.lumen.domain.ble.model.LedConstants.LED_EFFECT_RANGE
import com.example.lumen.domain.ble.model.LedConstants.STATIC_COLOR_VALUE
import com.example.lumen.presentation.ble.ledcontrol.components.CycleEffectsButton
import com.example.lumen.presentation.ble.ledcontrol.components.EffectFavoriteButton
import com.example.lumen.presentation.ble.ledcontrol.components.EffectPicker
import com.example.lumen.presentation.ble.ledcontrol.components.LedToggleButton
import com.example.lumen.presentation.common.utils.DeviceConfiguration
import com.example.lumen.presentation.common.utils.UiText
import com.example.lumen.presentation.common.utils.hexToComposeColor
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing

@Composable
fun EffectsScreen(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedEffect: (Int) -> Unit,
    setEffectCycle: () -> Unit,
    addFavEffect: (Int) -> Unit,
    removeFavEffect: (Int) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val deviceConfig = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val isOn = uiState.isLedOn
    val ledHexColor = uiState.ledHexColor
    val currentLedEffect = uiState.ledEffectValue
    val effectPickerTxt = uiState.effectPickerTxt
    val favoriteEffects = uiState.favoriteEffects

    EffectsContent(
        isOn = isOn,
        ledHexColor = ledHexColor,
        currentLedEffect = currentLedEffect,
        effectPickerTxt = effectPickerTxt,
        favoriteEffects = favoriteEffects,
        onTurnLedOnClick = onTurnLedOnClick,
        onTurnLedOffClick = onTurnLedOffClick,
        setLedEffect = setLedEffect,
        setEffectCycle = setEffectCycle,
        addFavEffect = addFavEffect,
        removeFavEffect = removeFavEffect,
        deviceConfig = deviceConfig,
        modifier = Modifier,
    )
}

@Composable
fun EffectsContent(
    isOn: Boolean,
    ledHexColor: String,
    currentLedEffect: Int,
    effectPickerTxt: UiText,
    favoriteEffects: Set<Int>,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedEffect: (Int) -> Unit,
    setEffectCycle: () -> Unit,
    addFavEffect: (Int) -> Unit,
    removeFavEffect: (Int) -> Unit,
    deviceConfig: DeviceConfiguration,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    var index by remember(currentLedEffect) {
        mutableIntStateOf(currentLedEffect)
    }

    val isAutoCycleOn = EFFECT_CYCLE_VALUE == currentLedEffect
    val isFavEffect = favoriteEffects.contains(currentLedEffect)

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
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.largeIncreased))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    EffectPicker(
                        enabled = isOn,
                        effectNumber = index,
                        onValueChange = {
                            index = it
                            setLedEffect(it)
                        },
                        currentLedEffect = currentLedEffect,
                        favoriteEffects = favoriteEffects,
                        effectPickerTxt = effectPickerTxt,
                        currentLedColor = ledHexColor.hexToComposeColor(),
                        modifier = Modifier.size(360.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = MaterialTheme.spacing.largeIncreased,
                                end = MaterialTheme.spacing.largeIncreased,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CycleEffectsButton(
                            enabled = isOn,
                            isAutoCycleOn = isAutoCycleOn,
                            onSetCycle = setEffectCycle,
                        )

                        Spacer(Modifier.width(240.dp))

                        EffectFavoriteButton(
                            enabled = isOn,
                            isFavorite = isFavEffect,
                            onFavor = { addFavEffect(currentLedEffect) },
                            onRemove = { removeFavEffect(currentLedEffect) },
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        enabled = isOn,
                        onClick = {
                            val prevValue = when (index) {
                                STATIC_COLOR_VALUE, LED_EFFECT_RANGE.first, EFFECT_CYCLE_VALUE,
                                -> LED_EFFECT_RANGE.last
                                else -> index - 1
                            }
                            index = prevValue
                            setLedEffect(prevValue)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        },
                        modifier = Modifier.size(MaterialTheme.spacing.extraExtraLarge),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = "Previous effect",
                            modifier = Modifier.size(MaterialTheme.spacing.extraLargeIncreased),
                        )
                    }

                    IconButton(
                        enabled = isOn,
                        onClick = {
                            val nextValue = when (index) {
                                STATIC_COLOR_VALUE, LED_EFFECT_RANGE.last -> LED_EFFECT_RANGE.first
                                else -> index + 1
                            }
                            index = nextValue
                            setLedEffect(nextValue)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        },
                        modifier = Modifier.size(MaterialTheme.spacing.extraExtraLarge),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_24px),
                            contentDescription = "Next effect",
                            modifier = Modifier.size(MaterialTheme.spacing.extraLargeIncreased),
                        )
                    }
                }

                LedToggleButton(
                    isOn = isOn,
                    onTurnLedOnClick = onTurnLedOnClick,
                    onTurnLedOffClick = onTurnLedOffClick,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium),
                )
            }
        }

        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Row(
                modifier = modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EffectPicker(
                        enabled = isOn,
                        effectNumber = index,
                        onValueChange = {
                            index = it
                            setLedEffect(it)
                        },
                        currentLedEffect = currentLedEffect,
                        effectPickerTxt = effectPickerTxt,
                        favoriteEffects = favoriteEffects,
                        currentLedColor = ledHexColor.hexToComposeColor(),
                        modifier = Modifier.size(300.dp),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1.2f),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.largeIncreased))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            enabled = isOn,
                            onClick = {
                                val prevValue = when (index) {
                                    STATIC_COLOR_VALUE -> LED_EFFECT_RANGE.last
                                    LED_EFFECT_RANGE.first -> LED_EFFECT_RANGE.last
                                    else -> index - 1
                                }
                                index = prevValue
                                setLedEffect(prevValue)
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            },
                            modifier = Modifier.size(MaterialTheme.spacing.extraExtraLarge),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back_24px),
                                contentDescription = "Previous effect",
                                modifier = Modifier.size(MaterialTheme.spacing.extraLargeIncreased),
                            )
                        }

                        IconButton(
                            enabled = isOn,
                            onClick = {
                                val nextValue = when (index) {
                                    STATIC_COLOR_VALUE -> LED_EFFECT_RANGE.first
                                    LED_EFFECT_RANGE.last -> LED_EFFECT_RANGE.first
                                    else -> index + 1
                                }
                                index = nextValue
                                setLedEffect(nextValue)
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            },
                            modifier = Modifier.size(MaterialTheme.spacing.extraExtraLarge),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward_24px),
                                contentDescription = "Next effect",
                                modifier = Modifier.size(MaterialTheme.spacing.extraLargeIncreased),
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = MaterialTheme.spacing.largeIncreased,
                                end = MaterialTheme.spacing.largeIncreased,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        CycleEffectsButton(
                            enabled = isOn,
                            isAutoCycleOn = isAutoCycleOn,
                            onSetCycle = setEffectCycle,
                        )

                        EffectFavoriteButton(
                            enabled = isOn,
                            isFavorite = isFavEffect,
                            onFavor = { addFavEffect(currentLedEffect) },
                            onRemove = { removeFavEffect(currentLedEffect) },
                        )
                    }

                    LedToggleButton(
                        isOn = isOn,
                        onTurnLedOnClick = onTurnLedOnClick,
                        onTurnLedOffClick = onTurnLedOffClick,
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun EffectContentPreview() {
    LumenTheme {
        Surface {
            EffectsContent(
                isOn = true,
                ledHexColor = "00ffff",
                currentLedEffect = STATIC_COLOR_VALUE,
                effectPickerTxt = UiText.StringResource(R.string.static_color),
                favoriteEffects = setOf(10, 15, 24, 50, 77),
                setLedEffect = { },
                setEffectCycle = { },
                addFavEffect = { },
                removeFavEffect = { },
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                deviceConfig = DeviceConfiguration.MOBILE_PORTRAIT,
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun EffectContentLandscapePreview() {
    LumenTheme {
        Surface {
            EffectsContent(
                isOn = true,
                ledHexColor = "00ffff",
                currentLedEffect = STATIC_COLOR_VALUE,
                effectPickerTxt = UiText.StringResource(R.string.static_color),
                favoriteEffects = emptySet(),
                setLedEffect = { },
                setEffectCycle = { },
                addFavEffect = { },
                removeFavEffect = { },
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                deviceConfig = DeviceConfiguration.MOBILE_LANDSCAPE,
            )
        }
    }
}

@Preview(widthDp = 1200, heightDp = 800)
@Composable
fun EffectContentTabletLandscapePreview() {
    LumenTheme {
        Surface {
            EffectsContent(
                isOn = true,
                ledHexColor = "00ffff",
                currentLedEffect = STATIC_COLOR_VALUE,
                effectPickerTxt = UiText.StringResource(R.string.static_color),
                favoriteEffects = emptySet(),
                setLedEffect = { },
                setEffectCycle = { },
                addFavEffect = { },
                removeFavEffect = { },
                onTurnLedOnClick = { },
                onTurnLedOffClick = { },
                deviceConfig = DeviceConfiguration.TABLET_LANDSCAPE,
            )
        }
    }
}
