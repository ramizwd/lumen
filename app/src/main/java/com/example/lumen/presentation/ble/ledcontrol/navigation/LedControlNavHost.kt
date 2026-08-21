package com.example.lumen.presentation.ble.ledcontrol.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lumen.domain.ble.model.IcModel
import com.example.lumen.domain.ble.model.RgbSequence
import com.example.lumen.presentation.ble.ledcontrol.ColorPickerScreen
import com.example.lumen.presentation.ble.ledcontrol.ControlScreen
import com.example.lumen.presentation.ble.ledcontrol.LedControlUiEvent
import com.example.lumen.presentation.ble.ledcontrol.LedControlUiState

@Composable
fun LedControlNavHost(
    uiState: LedControlUiState,
    onTurnLedOnClick: () -> Unit,
    onTurnLedOffClick: () -> Unit,
    setLedColor: (String) -> Unit,
    setLedNum: (Int) -> Unit,
    setIcModel: (IcModel) -> Unit,
    setRgbSequence: (RgbSequence) -> Unit,
    onSaveCustomColorSlot: (Int, String) -> Unit,
    onChangeBrightness: (Float) -> Unit,
    navController: NavHostController,
    startDestination: Screen,
    onEvent: (LedControlUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(140, easing = LinearEasing)) },
        exitTransition = { fadeOut(animationSpec = tween(140, easing = LinearEasing)) },
        modifier = modifier.fillMaxSize(),
    ) {
        composable<Screen.ColorPickerScreen> {
            ColorPickerScreen(
                uiState = uiState,
                onTurnLedOnClick = onTurnLedOnClick,
                onTurnLedOffClick = onTurnLedOffClick,
                setLedColor = setLedColor,
                onSaveCustomColorSlot = onSaveCustomColorSlot,
            )
        }

        composable<Screen.ControlScreen> {
            ControlScreen(
                uiState = uiState,
                onTurnLedOnClick = onTurnLedOnClick,
                onTurnLedOffClick = onTurnLedOffClick,
                onChangeBrightness = onChangeBrightness,
                setLedNum = setLedNum,
                setIcModel = setIcModel,
                setRgbSequence = setRgbSequence,
                onEvent = onEvent,
            )
        }
    }
}
