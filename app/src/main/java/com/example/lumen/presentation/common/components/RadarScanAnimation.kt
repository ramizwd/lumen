package com.example.lumen.presentation.common.components

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun RadarScanAnimation(
    isScanning: Boolean,
    modifier: Modifier = Modifier,
    animationColor: Color = MaterialTheme.colorScheme.outline,
    maxRadius: Dp = 36.dp,
    strokeWidth: Dp = 2.dp,
    numberOfRings: Int = 3,
    animationDurationMillis: Int = 2000,
) {
    if (!isScanning) return

    val infiniteTransition = rememberInfiniteTransition(label = "radar_rings_transition")
    val density = LocalDensity.current

    val maxRadiusPx = remember(key1 = density, key2 = maxRadius) {
        with(receiver = density) { maxRadius.toPx() }
    }

    val ringRadii = List(size = numberOfRings) { i ->
        val delay = (animationDurationMillis / numberOfRings) * i
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = maxRadiusPx,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDurationMillis,
                    delayMillis = delay,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "radar_ring_${i}_radius"
        )
    }

    val ringAlphas = List(size = numberOfRings) { i ->
        val delay = (animationDurationMillis / numberOfRings) * i
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDurationMillis,
                    delayMillis = delay,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "radar_ring_${i}_alpha"
        )
    }

    Box(
        modifier = modifier.size(maxRadius)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerCircleRadius = maxRadiusPx * 0.1f

            drawCircle(
                color = animationColor.copy(alpha = 0.5f),
                radius = centerCircleRadius,
                style = Fill,
            )

            for (i in 0 until numberOfRings) {
                val radius = ringRadii[i].value
                val alpha = ringAlphas[i].value

                if (radius > centerCircleRadius) {
                    drawCircle(
                        color = animationColor.copy(alpha = alpha),
                        radius = radius,
                        style = Stroke(width = strokeWidth.toPx())
                    )
                }
            }
        }
    }
}

@Preview(heightDp = 100, widthDp = 100)
@Preview(heightDp = 100, widthDp = 100, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RadarScanAnimationPreview() {
    LumenTheme {
        Surface {
            RadarScanAnimation(
                isScanning = true,
            )
        }
    }
}