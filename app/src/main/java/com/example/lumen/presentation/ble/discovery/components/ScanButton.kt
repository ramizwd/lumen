package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun ScanButton(
    onClick: () -> Unit,
    isScanning: Boolean,
) {
    Button(
        onClick = onClick
    ) {
        Text(text = if (isScanning) "Stop Scanning" else "Start Scan")
    }
}

@PreviewLightDark
@Composable
fun ScanButtonPreview() {
    LumenTheme {
        Surface {
            ScanButton(
                onClick = {},
                isScanning = false
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ScanButtonScanningPreview() {
    LumenTheme {
        Surface {
            ScanButton(
                onClick = {},
                isScanning = true
            )
        }
    }
}