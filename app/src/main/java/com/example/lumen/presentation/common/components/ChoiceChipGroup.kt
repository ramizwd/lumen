package com.example.lumen.presentation.common.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun ChoiceChipGroup(
    choices: List<String>,
    selectedChoice: String,
    onChoiceSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        choices.forEach { choice ->
            FilterChip(
                selected = choice == selectedChoice,
                onClick = { onChoiceSelected(choice) },
                label = { Text(text = choice) }
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ChoiceChipGroupPreview() {
    LumenTheme {
        val choices = listOf("All", "Favorites")
        var selectedItem by remember { mutableStateOf(choices.first()) }

        Surface {
            ChoiceChipGroup(
                choices = choices,
                selectedChoice = selectedItem,
                onChoiceSelected = { selectedItem = it },
            )
        }
    }
}