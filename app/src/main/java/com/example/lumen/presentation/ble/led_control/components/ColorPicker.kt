package com.example.lumen.presentation.ble.led_control.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.lumen.presentation.theme.LumenTheme
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPicker(
    controller: ColorPickerController,
    onSetHsvColor: (String) -> Unit,
    modifier: Modifier = Modifier,
    onStartInteraction: () -> Unit = {},
    onEndInteraction: () -> Unit = {},
) {
    var pickedHexColor by remember { mutableStateOf("") }

    HsvColorPicker(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onStartInteraction()
                        try {
                            awaitRelease()
                        } finally {
                            onEndInteraction()
                        }
                    }
                )
            }.then(modifier),
        controller = controller,
        onColorChanged = { colorEnvelope: ColorEnvelope ->
            // Drop the alpha value
            pickedHexColor = colorEnvelope.hexCode.drop(2)
            onSetHsvColor(pickedHexColor)
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
                controller = rememberColorPickerController(),
                onSetHsvColor = {},
            )
        }
    }
}