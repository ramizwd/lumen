package com.example.lumen.presentation.common.utils

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import java.util.Random
import android.graphics.Color as AndroidColor

/**
 * Converts 6 digit hex color string to Compose Color object.
 * @return [Color] object.
 */
fun String.hexToComposeColor(): Color {
    if (this.length != 6) {
        throw IllegalArgumentException("Hex string must be 6 characters long. Found: $this")
    }

    val colorInt = "#$this".toColorInt()
    return Color(colorInt)
}

/**
 * Generates random 6 digit hex color string with random saturation and max brightness
 */
fun Random.nextColorHexString(): String {
    val hsv = floatArrayOf(
        nextFloat() * 360f,
        nextFloat(),
        1.0f
    )

    // convert HSV to an ARGB int color
    val colorInt = AndroidColor.HSVToColor(hsv)

    return colorInt.toNoAlphaHexString()
}

/**
 * Converts an ARGB int color to 6 digit hex string, dropping first two digits that are
 * associated with the alpha value
 */
fun @receiver:ColorInt Int.toNoAlphaHexString(): String {
    return String.format("%02X", this).drop(2)
}
