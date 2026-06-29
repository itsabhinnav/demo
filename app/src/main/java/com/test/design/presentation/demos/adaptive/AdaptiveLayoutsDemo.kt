package com.test.design.presentation.demos.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel
import com.test.design.presentation.home.mapper.mapToSystemInfoUiState
import com.test.design.template.LocalAutomotiveWindowInfo

@Composable
fun AdaptiveLayoutsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Adaptive Layouts",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            AdaptiveLayoutInfoPanel()
        },
    ) {
        CustomSectionHeader(
            title = "Zone Architecture",
            subtitle = "Blue, green, and yellow zones adapt to display size",
        )
        ZoneDiagram()
        DisplayProfilesSection()
    }
}

@Composable
private fun AdaptiveLayoutInfoPanel() {
    val windowInfo = LocalAutomotiveWindowInfo.current
    val density = LocalDensity.current.density
    val info = mapToSystemInfoUiState(windowInfo, density)

    Column {
        Text("Live Display", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Text(info.displayLabel, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = OemSpacing.sm))
        Text(info.layoutLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = OemSpacing.xs))
        DemoTipsPanel(
            tips = listOf("70/30 split on standard displays", "75/25 on 15.3\"+ screens"),
            modifier = Modifier.padding(top = OemSpacing.lg),
        )
    }
}

@Composable
private fun ZoneDiagram() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = OemSpacing.lg),
    ) {
        Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
            ZoneBlock("Blue Zone", "Title, tabs, navigation", MaterialTheme.colorScheme.surfaceVariant, Modifier.height(56.dp))
            ZoneBlock("Green Zone", "Main content — 70%", MaterialTheme.colorScheme.background, Modifier.weight(1f))
        }
        ZoneBlock("Yellow Zone", "Info panel — 30%", MaterialTheme.colorScheme.surface, Modifier.weight(0.3f).fillMaxHeight())
    }
}

@Composable
private fun ZoneBlock(
    name: String,
    description: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .padding(OemSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DisplayProfilesSection() {
    CustomSectionHeader(title = "Display Profiles", subtitle = "Supported automotive screen sizes")
    ProfileRow("12.3\"", "1920 × 720", "70% / 30%")
    ProfileRow("14.3\"", "2240 × 820", "70% / 30%")
    ProfileRow("15.3\"", "2560 × 960", "75% / 25%")
}

@Composable
private fun ProfileRow(size: String, resolution: String, split: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.sm),
    ) {
        Text(size, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Text(resolution, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(split, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}
