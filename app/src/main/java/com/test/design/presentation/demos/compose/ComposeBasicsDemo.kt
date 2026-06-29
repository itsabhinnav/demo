package com.test.design.presentation.demos.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.test.design.component.components.CustomListTile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun ComposeBasicsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Compose Basics",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "State changes trigger recomposition",
                    "Use remember for local UI state",
                    "Modifiers chain left-to-right",
                    "Composables are functions, not views",
                ),
            )
        },
    ) {
        StateSection()
        RecompositionSection()
        ModifierSection()
    }
}

@Composable
private fun StateSection() {
    var count by remember { mutableIntStateOf(0) }
    CustomSectionHeader(
        title = "State & Recomposition",
        subtitle = "Tap the button to increment — only this section recomposes",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        Text(
            text = "Counter: $count",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        CustomButton(
            text = "Increment",
            onClick = { count++ },
            modifier = Modifier.padding(top = OemSpacing.md),
        )
    }
}

@Composable
private fun RecompositionSection() {
    CustomSectionHeader(
        title = "Declarative UI",
        subtitle = "Describe what the UI should look like for a given state",
    )
    Column(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        ConceptRow("Composable", "A Kotlin function that describes UI")
        ConceptRow("State", "Data that can change over time")
        ConceptRow("Recomposition", "Re-running composables when state changes")
        ConceptRow("Side Effect", "Operations tied to lifecycle (LaunchedEffect)")
    }
}

@Composable
private fun ModifierSection() {
    CustomSectionHeader(
        title = "Modifiers",
        subtitle = "Chain layout, drawing, and interaction behavior",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        Text("Modifier.fillMaxWidth()", style = MaterialTheme.typography.bodyLarge)
        Text("Modifier.padding(16.dp)", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = OemSpacing.sm))
        Text("Modifier.clickable { }", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = OemSpacing.sm))
    }
}

@Composable
private fun ConceptRow(term: String, definition: String) {
    CustomListTile(
        title = term,
        subtitle = definition,
        showChevron = false,
        modifier = Modifier.padding(vertical = OemSpacing.xs),
    )
}
