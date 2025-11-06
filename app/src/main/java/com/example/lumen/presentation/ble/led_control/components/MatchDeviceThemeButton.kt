package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing
import android.graphics.Color as AndroidColor

@Composable
fun MatchDeviceThemeButton(
    currentHexColor: String?,
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

    FilledTonalButton(
        modifier = modifier,
        onClick = { onMatchWithDeviceTheme(hexPrimaryColorSaturated) },
        enabled = hexPrimaryColorSaturated != currentHexColor,
    ) {
        Icon(
            painter = painterResource(R.drawable.colorize_24px),
            contentDescription = "Match with device color"
        )

        Spacer(Modifier.width(MaterialTheme.spacing.smallIncreased))

        Text(text = "Match with device theme")
    }
}

@PreviewDynamicColors
@Composable
fun MatchDeviceThemeButtonPreview() {
    LumenTheme {
        Surface {
            MatchDeviceThemeButton(
                currentHexColor = "ffffff",
                onMatchWithDeviceTheme = { },
            )
        }
    }
}