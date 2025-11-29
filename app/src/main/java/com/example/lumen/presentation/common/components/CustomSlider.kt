package com.example.lumen.presentation.common.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme

enum class SliderOrientation {
    VERTICAL,
    HORIZONTAL
}

/**
 * Height and Width properties are flipped in the vertical orientation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    enabled: Boolean,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    orientation: SliderOrientation = SliderOrientation.VERTICAL,
    height: Dp = 96.dp,
    width: Dp = 400.dp,
    stiffness: Float = Spring.StiffnessMedium,
    @DrawableRes icon: Int? = null,
    iconDescription: String? = null,
    iconSize: Dp = 32.dp
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(stiffness = stiffness),
        label = "value"
    )

    val animatedInactiveTrackColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.outlineVariant,
        label = "inactive_track_color"
    )

    val animatedActiveTrackColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.outline,
        label = "active_track_color"
    )

    val modifierVertical = modifier
        .graphicsLayer {
            rotationZ = 270f
            transformOrigin = TransformOrigin(0f, 0f)
        }
        .layout { measurable, constraints ->
            val placeable = measurable.measure(
                Constraints(
                    minWidth = constraints.minHeight,
                    maxWidth = constraints.maxHeight,
                    minHeight = constraints.minWidth,
                    maxHeight = constraints.maxWidth,
                )
            )
            layout(placeable.height, placeable.width) {
                placeable.place(-placeable.width, 0)
            }
        }
        .width(width)
        .height(height)

    val modifierHorizontal = modifier
        .width(width)
        .height(height)

    val iconOrientation = if (orientation == SliderOrientation.VERTICAL) 90f else 0f

    Slider(
        enabled = enabled,
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        valueRange = valueRange,
        modifier = if (orientation == SliderOrientation.VERTICAL) modifierVertical
        else modifierHorizontal,
        thumb = {},
        track = { sliderState ->
            val fraction by remember(key1 = animatedValue) {
                derivedStateOf {
                    (animatedValue - sliderState.valueRange.start) /
                            (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                }
            }

            Box(
                modifier = Modifier
                    .width(width)
                    .clip(shape = MaterialTheme.shapes.extraLarge)
                    .background(color = animatedInactiveTrackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .clip(shape = MaterialTheme.shapes.extraLarge)
                        .background(color = animatedActiveTrackColor)
                )

                if (icon != null) {
                    val isIconCovered = fraction > 0.08f

                    val iconTint by animateColorAsState(
                        targetValue = when {
                            isIconCovered && enabled -> MaterialTheme.colorScheme.inversePrimary
                            isIconCovered && !enabled -> MaterialTheme.colorScheme.outlineVariant
                            !isIconCovered && enabled -> MaterialTheme.colorScheme.onPrimaryContainer
                            !isIconCovered && !enabled -> MaterialTheme.colorScheme.outline
                            else -> MaterialTheme.colorScheme.error
                        },
                        label = "icon_tint"
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(64.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(iconSize)
                                .rotate(iconOrientation),
                            painter = painterResource(icon),
                            contentDescription = iconDescription,
                            tint = iconTint
                        )
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
fun CustomSliderPreview() {
    var sliderValue by remember { mutableFloatStateOf(70f) }
    var isEnabled by remember { mutableStateOf(true) }

    LumenTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.SpaceAround
            ) {
                CustomSlider(
                    enabled = isEnabled,
                    value = sliderValue,
                    valueRange = 0f..100f,
                    onValueChange = {
                        sliderValue = it
                    },
                    icon = R.drawable.brightness_medium_24px,
                )

                Button(onClick = {isEnabled = !isEnabled}) {
                    Text(text = if (isEnabled) "Disable" else "Enable")
                }
            }

        }
    }
}

@PreviewLightDark
@Composable
fun CustomSliderHorizontalPreview() {
    var sliderValue by remember { mutableFloatStateOf(70f) }
    var isEnabled by remember { mutableStateOf(true) }

    LumenTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.SpaceAround
            ) {
                CustomSlider(
                    enabled = isEnabled,
                    value = sliderValue,
                    valueRange = 0f..100f,
                    onValueChange = {
                        sliderValue = it
                    },
                    icon = R.drawable.brightness_medium_24px,
                    orientation = SliderOrientation.HORIZONTAL
                )

                Button(onClick = {isEnabled = !isEnabled}) {
                    Text(text = if (isEnabled) "Disable" else "Enable")
                }
            }

        }
    }
}

@PreviewLightDark
@Composable
fun CustomSliderDisabledPreview() {
    var sliderValue by remember { mutableFloatStateOf(70f) }

    LumenTheme {
        Surface {
            CustomSlider(
                enabled = false,
                value = sliderValue,
                valueRange = 0f..100f,
                onValueChange = {
                    sliderValue = it
                },
                icon = R.drawable.brightness_medium_24px,
            )
        }
    }
}