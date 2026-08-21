package com.example.lumen.presentation.common.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.theme.LumenTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

@Composable
fun <T> WheelPicker(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemHeight: Dp = 48.dp,
    visibleItemsCount: Int = 5,
    initialIndex: Int,
    onItemSelected: (T) -> Unit,
    labelExtractor: (T) -> String = { it.toString() },
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf initialIndex

            // Determine which item is the closest to the center of the viewport
            val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            visibleItems
                .minByOrNull {
                    abs((it.offset + it.size / 2) - center)
                }?.index ?: initialIndex
        }
    }

    val selectedIndex by remember {
        derivedStateOf { centerIndex.coerceIn(0, items.size - 1) }
    }

    LaunchedEffect(listState) {
        snapshotFlow { selectedIndex }
            .distinctUntilChanged()
            .collect { index ->
                onItemSelected(items[index])
            }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val verticalPadding = itemHeight * (visibleItemsCount - 1) / 2

        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = verticalPadding),
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = index == selectedIndex

                val targetAlpha = remember(index, selectedIndex) {
                    val distance = abs(index - selectedIndex)
                    when (distance) {
                        0 -> 1f
                        1 -> 0.5f
                        else -> 0.2f
                    }
                }
                val alpha by animateFloatAsState(targetValue = targetAlpha, label = "alpha")

                val targetScale = remember(index, selectedIndex) {
                    val distance = abs(index - selectedIndex)
                    when (distance) {
                        0 -> 1.2f
                        1 -> 0.9f
                        else -> 0.7f
                    }
                }
                val scale by animateFloatAsState(targetValue = targetScale, label = "scale")

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = labelExtractor(item),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun WheelPickerPreview() {
    val items = listOf("100", "200", "300", "400", "500", "600")
    var selectedItem by remember { mutableStateOf(items[0]) }

    LumenTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Selected value: $selectedItem")

                WheelPicker(
                    items = items,
                    initialIndex = 0,
                    onItemSelected = { selectedItem = it },
                )
            }
        }
    }
}
