package com.example.lumen.presentation.ble.ledcontrol

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EffectsScreen(
    uiState: LedControlUiState,
    setLedEffect: (Int) -> Unit,
) {
    Column {
        Text("Current effect value: ${uiState.ledEffectValue}")
        Button(onClick = { setLedEffect(2) }) {
            Text("2")
        }

        Button(onClick = { setLedEffect(121) }) {
            // TODO 121 indicates static color
            Text("121")
        }
    }
}
