package com.example.lumen.presentation.common.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.lumen.R
import com.example.lumen.domain.ble.model.LedConstants.ACTIVE_PIXELS_RANGE

@Composable
fun DigitFieldDialog(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    title: String,
    allowedRange: IntRange = ACTIVE_PIXELS_RANGE,
    maxDigits: Int = 4,
    supportingText: String,
    onConfirmation: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val inputNumber by remember {
        derivedStateOf { state.text.toString().toIntOrNull() }
    }

    val isValid by remember {
        derivedStateOf {
            state.text.isNotEmpty() && inputNumber?.let { it in allowedRange } ?: false
        }
    }

    AlertDialog(
        modifier = modifier,
        title = { Text(text = title) },
        text = {
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                state = state,
                modifier = Modifier.focusRequester(focusRequester),
                lineLimits = TextFieldLineLimits.SingleLine,
                supportingText = { Text(text = supportingText) },
                isError = !isValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                inputTransformation = remember {
                    InputTransformation {
                        if (!asCharSequence().all { it.isDigit() }) {
                            revertAllChanges()
                        }
                    }.then(InputTransformation.maxLength(maxDigits))
                },
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    inputNumber?.let(onConfirmation)
                },
                enabled = isValid,
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.dismiss))
            }
        },
    )
}
