package com.example.lumen.presentation.common.model

import androidx.compose.material3.SnackbarDuration
import com.example.lumen.presentation.common.utils.UiText

/**
 * Represents the Snackbar properties
 */
data class SnackbarEvent(
    val message: UiText,
    val actionLabel: UiText? = null,
    val duration: SnackbarDuration = SnackbarDuration.Long,
)
