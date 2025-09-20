package com.example.lumen.presentation.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun PermissionAlertDialog(
    permissionTextProvider: PermissionTextProvider,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = permissionTextProvider.title)
        },
        text = {
            Text(text = permissionTextProvider.description)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(permissionTextProvider.confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

interface PermissionTextProvider {
    val title: String
    val description: String
    val confirmButtonText: String
}

class EnableBluetoothTextProvider() : PermissionTextProvider {
    override val title: String
        get() = "Bluetooth is off"

    override val description: String
        get() = "Bluetooth needs to be enabled to start scanning."

    override val confirmButtonText: String
        get() = "Enable"
}

class BluetoothPermissionTextProvider: PermissionTextProvider {
    override val title: String
        get() = "Permission request"

    override val description: String
        get() = "Nearby devices permission is required to scan for and connect to Bluetooth devices."

    override val confirmButtonText: String
        get() = "Allow"
}

class OpenAppSettingsTextProvider: PermissionTextProvider {
    override val title: String
        get() = "Permission request"

    override val description: String
        get() = "Please allow nearby devices permission in app settings to scan for and connect to Bluetooth devices."

    override val confirmButtonText: String
        get() = "Settings"
}

@PreviewLightDark
@Composable
fun PermissionAlertDialogPreview() {
    LumenTheme {
        Surface {
            PermissionAlertDialog(
                onConfirmation = {},
                onDismissRequest = {},
                permissionTextProvider = BluetoothPermissionTextProvider(),
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PermissionAlertDialogOpenSettingsPreview() {
    LumenTheme {
        Surface {
            PermissionAlertDialog(
                onConfirmation = {},
                onDismissRequest = {},
                permissionTextProvider = OpenAppSettingsTextProvider(),
            )
        }
    }
}