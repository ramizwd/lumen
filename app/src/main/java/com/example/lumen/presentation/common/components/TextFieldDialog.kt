package com.example.lumen.presentation.common.components

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun TextFieldDialog(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    title: String,
    initialText: String,
    maxChar: Int,
    minChar: Int = 1,
    supportingText: String,
    onConfirmation: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                state = state,
                lineLimits = TextFieldLineLimits.SingleLine,
                supportingText = { Text(text = supportingText) },
                isError = state.text.length > maxChar,
                trailingIcon = {
                    if (state.text.isNotBlank()) {
                        IconButton(onClick = {
                            state.clearText()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.close_24px),
                                contentDescription = "Clear text"
                            )
                        }
                    }
                }
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation(state.text.toString()) },
                enabled = state.text.length in minChar..maxChar
                        && state.text.isNotBlank()
                        && state.text != initialText
            ) {
                Text(text = "Confirm")
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


@PreviewLightDark
@Composable
fun TextFieldDialogPreview() {
    LumenTheme {
        Surface {
            TextFieldDialog(
                state = rememberTextFieldState(),
                title = "Rename Device",
                initialText = "Test",
                maxChar = 10,
                supportingText = "10 characters max",
                onConfirmation = {},
                onDismissRequest = {},
            )
        }
    }
}