package com.example.lumen.presentation.common.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState

fun showToast(
    context: Context,
    message: String,
    duration: Int = Toast.LENGTH_LONG,
    currentToastRef: MutableState<Toast?>
): Toast {
    currentToastRef.value?.cancel()
    val newToast = Toast.makeText(context, message, duration)
    newToast.show()
    currentToastRef.value = newToast
    return newToast
}
