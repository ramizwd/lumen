package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun ScanButton(
    isEnabled: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    isScanning: Boolean,
) {
    Button(
        enabled = isEnabled,
        onClick = if (isScanning) onStopScan else onStartScan
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
                isEnabled = true,
                onStopScan = {},
                onStartScan = {},
                isScanning = false
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ScanButtonDisabledPreview() {
    LumenTheme {
        Surface {
            ScanButton(
                isEnabled = false,
                onStopScan = {},
                onStartScan = {},
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
                isEnabled = true,
                onStopScan = {},
                onStartScan = {},
                isScanning = true
            )
        }
    }
}