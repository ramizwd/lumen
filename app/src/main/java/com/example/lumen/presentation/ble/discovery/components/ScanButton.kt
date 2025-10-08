package com.example.lumen.presentation.ble.discovery.components

import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun ScanButton(
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
) {
    Button(onClick = { if (isScanning) onStopScan() else onStartScan() }) {
        Text(text = if (isScanning) "Stop Scanning" else "Start Scan")
    }
}

@PreviewLightDark
@Composable
fun ScanButtonPreview() {
    LumenTheme {
        Surface {
            ScanButton(
                isScanning = false,
                onStartScan = {},
                onStopScan = {}
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
                isScanning = true,
                onStartScan = {},
                onStopScan = {}
            )
        }
    }
}