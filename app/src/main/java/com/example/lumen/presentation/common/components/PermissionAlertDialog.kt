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
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = { },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Ok")
            }
        }
    )
}

@PreviewLightDark
@Composable
fun PermissionAlertDialogPreview() {
    LumenTheme {
        Surface {
            PermissionAlertDialog(
                onConfirmation = { },
                dialogTitle = "Allow permission",
                dialogText = "Example text to allow permissions."
            )
        }
    }
}