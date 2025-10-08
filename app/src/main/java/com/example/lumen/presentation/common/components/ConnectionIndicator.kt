package com.example.lumen.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun ConnectionIndicator(
    connectionState: ConnectionState,
    onCancelConnectionClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val connectionStateText = when (connectionState) {
            ConnectionState.CONNECTING -> "Connecting..."
            ConnectionState.LOADING_DEVICE_STATE -> "Loading state..."
            ConnectionState.RETRYING -> "Connection failed, retrying..."
            ConnectionState.INVALID_DEVICE -> "Invalid device, disconnecting..."
            else -> ""
        }

        if (connectionState != ConnectionState.STATE_LOADED_AND_CONNECTED) {
            CircularProgressIndicator()
            Text(text = connectionStateText)

            Button(onClick =onCancelConnectionClick) {
                Text(text = "Cancel")
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ConnectionIndicatorPreview() {
    LumenTheme {
        Surface {
            ConnectionIndicator(
                connectionState = ConnectionState.CONNECTING,
                onCancelConnectionClick = {},
            )
        }
    }
}