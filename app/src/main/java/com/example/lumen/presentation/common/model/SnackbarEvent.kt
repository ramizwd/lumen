package com.example.lumen.presentation.common.model

import androidx.compose.material3.SnackbarDuration

/**
 * Represents the Snackbar properties
 */
data class SnackbarEvent(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Long,
)