package com.example.lumen.presentation.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.theme.spacing

@Composable
fun <T> PullToRefresh(
    items: List<T>,
    emptyContent: @Composable () -> Unit,
    content: @Composable (T) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    gridCellMinSize: Dp = 400.dp,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scrollState: ScrollState = rememberScrollState(),
    keySelector: ((T) -> Any)? = null,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                emptyContent()
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.extraSmall
                ),
                columns = GridCells.Adaptive(gridCellMinSize),
                state = lazyGridState,
            ) {
                if (keySelector != null) {
                    items(items, key = keySelector) {
                        content(it)
                    }
                } else {
                    items(items) {
                        content(it)
                    }
                }
            }
        }
    }
}