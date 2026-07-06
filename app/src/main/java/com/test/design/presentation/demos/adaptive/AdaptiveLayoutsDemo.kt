package com.test.design.presentation.demos.adaptive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.components.CardStyle
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomStatRow
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
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
        CustomSectionHeader(title = "Live Display", subtitle = info.layoutLabel)
        CustomStatRow(label = "Resolution", value = info.displayLabel)
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
            ZoneBlock("Blue Zone", "Title, tabs, navigation", Modifier.height(56.dp))
            ZoneBlock("Green Zone", "Main content — 70%", Modifier.weight(1f))
        }
        ZoneBlock("Yellow Zone", "Info panel — 30%", Modifier.weight(0.3f).fillMaxHeight())
    }
}

@Composable
private fun ZoneBlock(
    name: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    CustomCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(OemSpacing.xs),
        style = CardStyle.Outlined,
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium, color = OemOnSurface)
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
    }
}

@Composable
private fun DisplayProfilesSection() {
    CustomSectionHeader(title = "Display Profiles", subtitle = "Supported automotive screen sizes")
    CustomStatRow(label = "12.3\"", value = "1920 × 720 — 70/30")
    CustomStatRow(label = "14.3\"", value = "2240 × 820 — 70/30")
    CustomStatRow(label = "15.3\"", value = "2560 × 960 — 75/25")
}
