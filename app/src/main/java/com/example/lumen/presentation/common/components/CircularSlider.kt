package com.example.lumen.presentation.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.theme.LumenTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CircularSlider(
    enabled: Boolean,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    indicatorCount: Int = 0,
    defaultValue: Int? = null,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
) {
    val radiusRatio = 0.7f
    val gapAngle = 40f
    val sweepAngle = 360f - gapAngle
    val startAngle = -90f + gapAngle / 2f

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentValue by rememberUpdatedState(value)
    val currentValueRange by rememberUpdatedState(valueRange)
    val currentDefaultValue by rememberUpdatedState(defaultValue)

    var isDraggingThumb by remember { mutableStateOf(false) }

    val inactiveTrackColor by animateColorAsState(
        targetValue =
            if (enabled) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        label = "inactive_track_color",
    )

    val activeTrackColor by animateColorAsState(
        targetValue =
            if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            },
        label = "active_track_color",
    )

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                var lastSentValue: Int? = null

                detectDragGestures(
                    onDragStart = { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val radius = min(size.width, size.height) / 2f * radiusRatio

                        val angleRad = if (currentValue == currentDefaultValue) {
                            -PI / 2 // Top center
                        } else {
                            val p =
                                (currentValue - currentValueRange.first).toFloat() /
                                    (currentValueRange.last - currentValueRange.first)
                            Math.toRadians((startAngle + p * sweepAngle).toDouble())
                        }

                        val thumbX = centerX + radius * cos(angleRad).toFloat()
                        val thumbY = centerY + radius * sin(angleRad).toFloat()
                        val thumbOffset = Offset(thumbX, thumbY)

                        if ((offset - thumbOffset).getDistance() <= 40.dp.toPx()) {
                            isDraggingThumb = true
                            lastSentValue = currentValue
                        }
                    },
                    onDragEnd = {
                        isDraggingThumb = false
                        lastSentValue = null
                    },
                    onDragCancel = {
                        isDraggingThumb = false
                        lastSentValue = null
                    },
                    onDrag = { change, _ ->
                        if (!isDraggingThumb) return@detectDragGestures

                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val touchPosition = change.position
                        val touchAngle = atan2(
                            touchPosition.y - centerY,
                            touchPosition.x - centerX,
                        ).toDouble()

                        val degrees = Math.toDegrees(touchAngle).toFloat()
                        // Normalize to 0..360 starting from -90 (top)
                        var normalizedDegrees = (degrees + 90f) % 360f
                        if (normalizedDegrees < 0) normalizedDegrees += 360f

                        val newValue = if (normalizedDegrees < gapAngle / 2f ||
                            normalizedDegrees > 360f - gapAngle / 2f
                        ) {
                            // Snap to start or end
                            if (normalizedDegrees < 180f) {
                                currentValueRange.first
                            } else {
                                currentValueRange.last
                            }
                        } else {
                            val p = ((normalizedDegrees - gapAngle / 2f) / sweepAngle).coerceIn(
                                0f,
                                1f,
                            )
                            (
                                currentValueRange.first +
                                    p * (currentValueRange.last - currentValueRange.first)
                            ).roundToInt()
                        }

                        if (newValue != lastSentValue) {
                            lastSentValue = newValue
                            currentOnValueChange(newValue)
                        }
                        change.consume()
                    },
                )
            },
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * radiusRatio
        val strokeWidth = 16.dp.toPx()

        // Draw Indicators
        if (indicatorCount > 0) {
            val indicatorRadius = radius + 24.dp.toPx()

            for (i in 0 until indicatorCount) {
                val currentValue = valueRange.first + i
                val p = i.toFloat() / (indicatorCount - 1)
                val angleDeg = startAngle + p * sweepAngle
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val isSelected = currentValue == value

                val tickLength = if (isSelected) 12.dp.toPx() else 8.dp.toPx()
                val strokeWidth = if (isSelected) 3.dp.toPx() else 1.dp.toPx()

                val startX = center.x + indicatorRadius * cos(angleRad).toFloat()
                val startY = center.y + indicatorRadius * sin(angleRad).toFloat()
                val endX = center.x + (indicatorRadius + tickLength) * cos(angleRad).toFloat()
                val endY = center.y + (indicatorRadius + tickLength) * sin(angleRad).toFloat()

                drawLine(
                    color = if (isSelected) activeTrackColor else inactiveTrackColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }

        // Inactive track
        drawArc(
            color = inactiveTrackColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )

        // Active track and thumb position
        val thumbAngleDeg = if (value == defaultValue) {
            -90f
        } else {
            val p = (value - valueRange.first).toFloat() / (valueRange.last - valueRange.first)
            startAngle + p * sweepAngle
        }

        if (value != defaultValue) {
            val p = (value - valueRange.first).toFloat() / (valueRange.last - valueRange.first)
            drawArc(
                color = activeTrackColor,
                startAngle = startAngle,
                sweepAngle = p * sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        // Thumb
        val thumbAngleRad = Math.toRadians(thumbAngleDeg.toDouble())
        val thumbX = center.x + radius * cos(thumbAngleRad).toFloat()
        val thumbY = center.y + radius * sin(thumbAngleRad).toFloat()

        drawCircle(
            color = thumbColor,
            radius = 16.dp.toPx(),
            center = Offset(thumbX, thumbY),
        )
    }
}

@PreviewLightDark
@Composable
fun CircularSliderPreview() {
    var index by remember { mutableIntStateOf(1) }
    val range = 1..100
    val specialValue = 101

    LumenTheme {
        Surface {
            CircularSlider(
                enabled = true,
                value = index,
                onValueChange = {
                    index = it
                },
                valueRange = range,
                indicatorCount = range.last,
                defaultValue = specialValue,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CircularSliderDisabledPreview() {
    val range = 1..100

    LumenTheme {
        Surface {
            CircularSlider(
                enabled = false,
                value = 10,
                onValueChange = { },
                valueRange = range,
                indicatorCount = range.last,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CircularSliderDefaultPreview() {
    var index by remember { mutableIntStateOf(0) }
    val range = 1..100

    LumenTheme {
        Surface {
            CircularSlider(
                enabled = false,
                defaultValue = 0,
                value = index,
                onValueChange = {
                    index = it
                },
                valueRange = range,
                indicatorCount = range.last,
            )
        }
    }
}
