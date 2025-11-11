package com.example.lumen.presentation.ble.led_control.components

import android.graphics.BlurMaskFilter
import android.graphics.SweepGradient
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.example.lumen.presentation.theme.LumenTheme
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint

@Composable
fun ColorPicker(
    controller: ColorPickerController,
    onSetHsvColor: (String) -> Unit,
    modifier: Modifier = Modifier,
    onStartInteraction: () -> Unit = {},
    onEndInteraction: () -> Unit = {},
    glowRadius: Float = 120f,
    glowAlpha: Float = 0.3f,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            drawIntoCanvas {
                val nativeCanvas = it.nativeCanvas

                val argbColors = listOf(
                    Color.Red,
                    Color(0xFFFF00FF),
                    Color.Blue,
                    Color.Cyan,
                    Color.Green,
                    Color(0xFFFFEA00),
                    Color.Red
                ).map { color -> color.copy(alpha = glowAlpha).toArgb() }.toIntArray()

                val sweepGradient = SweepGradient(
                    center.x,
                    center.y,
                    argbColors,
                    null
                )

                val nativePaint = Paint().asFrameworkPaint().apply {
                    shader = sweepGradient
                    maskFilter = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.NORMAL)
                }

                nativeCanvas.drawCircle(
                    center.x,
                    center.y,
                    size.minDimension / 2 + glowRadius / 2, // cover glow area
                    nativePaint
                )
            }
        }

        HsvColorPicker(
            modifier = Modifier
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
                onSetHsvColor(colorEnvelope.hexCode.drop(2))
            },
            wheelImageBitmap = wheelBitmap()
        )
    }
}

@Composable
private fun wheelBitmap(): ImageBitmap {
    val density = LocalDensity.current.density

    val radius =  14.dp
    val circleColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f).toArgb()
    val borderWidth = 2f * density
    val boarderColor = AndroidColor.WHITE

    val contentRadiusPx = radius.value * density

    val totalRequiredSize = contentRadiusPx * 2
    // + 2dp buffer in case of slight overspill
    val bitmapDiameter = (totalRequiredSize + 2 * density).toInt()

    val bitmap = createBitmap(bitmapDiameter, bitmapDiameter)
    val canvas = AndroidCanvas(bitmap)

    // Make the center to be in the middle of the bitmap
    val centerX = bitmapDiameter / 2f
    val centerY = bitmapDiameter / 2f

    val circlePaint = AndroidPaint().apply {
        color = circleColor
    }
    canvas.drawCircle(
        centerX,
        centerY,
        contentRadiusPx - borderWidth,
        circlePaint
    )

    val borderPaint = AndroidPaint().apply {
        style = AndroidPaint.Style.STROKE
        strokeWidth = borderWidth
        color = boarderColor
    }
    canvas.drawCircle(
        centerX,
        centerY,
        contentRadiusPx - borderWidth / 2f, // prevent gaps between circle and its border
        borderPaint)

    return bitmap.asImageBitmap()
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