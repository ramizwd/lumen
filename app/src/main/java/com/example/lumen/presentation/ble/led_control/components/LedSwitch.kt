package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun LedSwitch(
    isOn: Boolean,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    var checked by rememberSaveable { mutableStateOf(isOn) }

    Switch(
        checked = checked,
        onCheckedChange = {
            if (it) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                checked = true
                onTurnLedOnClick()
            } else {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                checked = false
                onTurnLedOffClick()
            }},
    )
}

@PreviewLightDark
@Composable
fun LedSwitchPreview() {
    LumenTheme {
        Surface {
            LedSwitch(
                isOn = true,
                onTurnLedOnClick = {},
                onTurnLedOffClick = {}
            )
        }
    }
}