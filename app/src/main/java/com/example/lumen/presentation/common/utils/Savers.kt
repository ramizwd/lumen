package com.example.lumen.presentation.common.utils

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import com.example.lumen.utils.hexToComposeColor
import com.example.lumen.utils.toHexString

// Custom savers

val ColorSaver = Saver<Color?, String>(
    save = { it?.toHexString() },
    restore = { it.hexToComposeColor() }
)
