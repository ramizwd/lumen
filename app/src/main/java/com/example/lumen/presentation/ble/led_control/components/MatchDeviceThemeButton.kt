package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.lumen.utils.hexToComposeColor
import android.graphics.Color as AndroidColor

@Composable
fun MatchDeviceThemeButton(
    currentColor: Color?,
    onMatchWithDeviceTheme: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    // Set the luminosity of primary color to max, convert to hex, and drop alpha
    val argbInt = onPrimaryColor.toArgb()
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(argbInt, hsv)
    hsv[2] = 1f

    val hexPrimaryColorSaturated = AndroidColor.HSVToColor(hsv).toHexString().drop(2)

    Button(
        modifier = modifier,
        onClick = { onMatchWithDeviceTheme(hexPrimaryColorSaturated) },
        enabled = hexPrimaryColorSaturated.hexToComposeColor() != currentColor) {
        Text(text = "Match with device theme")
    }
}