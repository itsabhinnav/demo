package com.test.design.presentation.demos.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomTabs
import com.test.design.component.theme.NissanSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun TabsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Tabs & Navigation",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Use tabs for 2–5 related sections",
                    "Scrollable tabs for 6+ items",
                    "Keep tab labels short",
                    "Place tabs in the blue zone",
                ),
            )
        },
    ) {
        FixedTabsSection()
        ScrollableTabsSection()
    }
}

@Composable
private fun FixedTabsSection() {
    var selected by remember { mutableIntStateOf(0) }
    val tabs = listOf("Vehicle", "Trip", "Energy")
    val contents = listOf(
        "Vehicle status, tire pressure, and maintenance alerts.",
        "Current trip distance, duration, and average efficiency.",
        "Battery level, charging status, and range estimate.",
    )

    CustomSectionHeader(title = "Fixed Tabs", subtitle = "TabRow for up to 5 sections")
    CustomTabs(
        tabs = tabs,
        selectedIndex = selected,
        onTabSelected = { selected = it },
        modifier = Modifier.padding(vertical = NissanSpacing.md),
    )
    TabContent(text = contents[selected])
}

@Composable
private fun ScrollableTabsSection() {
    var selected by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Climate", "Seats", "Lights", "Mirrors", "Assists", "Display")

    CustomSectionHeader(title = "Scrollable Tabs", subtitle = "ScrollableTabRow for many sections")
    CustomTabs(
        tabs = tabs,
        selectedIndex = selected,
        onTabSelected = { selected = it },
        scrollable = true,
        modifier = Modifier.padding(vertical = NissanSpacing.md),
    )
    TabContent(text = "Content for ${tabs[selected]} settings and controls.")
}

@Composable
private fun TabContent(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = NissanSpacing.lg),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
