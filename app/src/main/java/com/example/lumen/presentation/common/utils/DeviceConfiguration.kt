package com.example.lumen.presentation.common.utils

import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

/**
 * Provides current device's screen configuration
 */
enum class DeviceConfiguration {
    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE;

    companion object {
        fun fromWindowSizeClass(windowSizeClass: WindowSizeClass): DeviceConfiguration {
            val isWidthExpanded = windowSizeClass
                .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)
            val isWidthMedium = windowSizeClass
                .isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

            val isHeightExpanded = windowSizeClass
                .isHeightAtLeastBreakpoint(HEIGHT_DP_EXPANDED_LOWER_BOUND)
            val isHeightMedium = windowSizeClass
                .isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)

            // size reference: https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes
            return when {
                isWidthExpanded && isHeightMedium -> TABLET_LANDSCAPE
                isWidthMedium && isHeightExpanded -> TABLET_PORTRAIT
                isWidthExpanded && !isHeightExpanded -> MOBILE_LANDSCAPE
                else -> MOBILE_PORTRAIT
            }
        }
    }
}