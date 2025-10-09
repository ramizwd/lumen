package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    currentColor: Color?,
    onSetHsvColor: (String) -> Unit,
) {
    val controller = rememberColorPickerController()
    var pickedHexColor by remember { mutableStateOf("") }

    LaunchedEffect(key1 = currentColor) {
        currentColor?.let { color ->
            controller.selectByColor(
                color = color,
                fromUser = false
            )
        }
    }

    HsvColorPicker(
        modifier = modifier,
        controller = controller,
        initialColor = currentColor,
        onColorChanged = { colorEnvelope: ColorEnvelope ->
            // Drop the alpha value
            val pickedHex = colorEnvelope.hexCode.drop(2)

            pickedHexColor = pickedHex
            onSetHsvColor(pickedHex)
        },
    )

    Text(text = "#$pickedHexColor".uppercase())
}

@PreviewLightDark
@Composable
fun ColorPickerPreview() {
    LumenTheme {
        Surface {
            ColorPicker(
                currentColor = null,
                onSetHsvColor = {},
            )
        }
    }
}