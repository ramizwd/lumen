package com.example.lumen.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppSpacing(
    val extraSmall: Dp = 2.dp,
    val small: Dp = 4.dp,
    val smallIncreased: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val largeIncreased: Dp = 24.dp,
    val extraLarge: Dp = 28.dp,
    val extraLargeIncreased: Dp = 32.dp,
)

val LocalAppSpacing = compositionLocalOf { AppSpacing() }

val MaterialTheme.spacing: AppSpacing
@Composable
@ReadOnlyComposable
get() = LocalAppSpacing.current