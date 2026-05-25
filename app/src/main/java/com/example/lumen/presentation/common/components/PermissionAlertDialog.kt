package com.example.lumen.presentation.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.R
import com.example.lumen.presentation.common.utils.UiText
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun PermissionAlertDialog(
    permissionTextProvider: PermissionTextProvider,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = permissionTextProvider.title.asString())
        },
        text = {
            Text(text = permissionTextProvider.description.asString())
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
            ) {
                Text(permissionTextProvider.confirmButtonText.asString())
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(stringResource(R.string.dismiss))
            }
        },
    )
}

interface PermissionTextProvider {
    val title: UiText
    val description: UiText
    val confirmButtonText: UiText
}

class EnableBluetoothTextProvider : PermissionTextProvider {
    override val title: UiText
        get() = UiText.StringResource(R.string.bt_is_off)

    override val description: UiText
        get() = UiText.StringResource(R.string.bt_needs_to_be_enabled_to_scan)

    override val confirmButtonText: UiText
        get() = UiText.StringResource(R.string.enable)
}

class BluetoothPermissionTextProvider : PermissionTextProvider {
    override val title: UiText
        get() = UiText.StringResource(R.string.perms_request)

    override val description: UiText
        get() = UiText.StringResource(R.string.perms_required_to_scan)

    override val confirmButtonText: UiText
        get() = UiText.StringResource(R.string.allow)
}

class OpenAppSettingsTextProvider : PermissionTextProvider {
    override val title: UiText
        get() = UiText.StringResource(R.string.perms_request)

    override val description: UiText
        get() = UiText.StringResource(R.string.allow_perms_in_settings)

    override val confirmButtonText: UiText
        get() = UiText.StringResource(R.string.settings)
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
