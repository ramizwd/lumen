package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
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

    val isSelected = hexPrimaryColorSaturated == currentHexColor

    TooltipBox(
        modifier = Modifier,
        positionProvider = rememberTooltipPositionProvider(positioning = TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip { Text("Match with device theme") }
        },
        state = rememberTooltipState()
    ) {
        FilledTonalIconToggleButton(
            modifier = modifier,
            checked = isSelected,
            onCheckedChange = { onMatchWithDeviceTheme(hexPrimaryColorSaturated) },
        ) {
            Icon(
                painter = if (isSelected) painterResource(R.drawable.colorize_filled_24px)
                else painterResource(R.drawable.colorize_24px),
                contentDescription = "Match with device color"
            )
        }
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