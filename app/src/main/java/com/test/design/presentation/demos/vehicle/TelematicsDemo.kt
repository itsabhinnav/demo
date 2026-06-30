package com.test.design.presentation.demos.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomBadge
import com.test.design.component.components.CustomList
import com.test.design.component.components.CustomListItem
import com.test.design.component.components.CustomListItemRow
import com.test.design.component.components.CustomListTile
import com.test.design.component.components.CustomMetricCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomStatRow
import com.test.design.component.components.CustomStatusIndicator
import com.test.design.component.components.CustomTabs
import com.test.design.component.components.StatusLevel
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

private val tripHistory = listOf(
    CustomListItem("1", "Home → Office", "24.3 mi · 38 min"),
    CustomListItem("2", "Office → Grocery", "3.1 mi · 12 min"),
    CustomListItem("3", "Grocery → Home", "26.8 mi · 42 min"),
)

private val alerts = listOf(
    Triple("Tire pressure low — front left", "28 PSI · below 32 PSI", StatusLevel.Warning),
    Triple("Service due in 1,200 mi", "Oil change recommended", StatusLevel.Info),
    Triple("Geofence exit — Home zone", "Today, 8:15 AM", StatusLevel.Info),
)

@Composable
fun TelematicsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    DemoScaffold(
        title = "Telematics",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            Column {
                DemoTipsPanel(
                    tips = listOf(
                        "Live data should be glanceable while driving",
                        "Alerts use badges and status indicators",
                        "Deep trip history is best when parked",
                    ),
                )
                CustomStatRow(
                    label = "Odometer",
                    value = "18,432 mi",
                    modifier = Modifier.padding(top = OemSpacing.md),
                )
                CustomStatRow(
                    label = "VIN",
                    value = "1HGBH41JXMN109186",
                )
            }
        },
    ) {
        LiveMetricsSection()
        TabsSection(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        TabContentSection(selectedTab = selectedTab)
    }
}

@Composable
private fun LiveMetricsSection() {
    CustomSectionHeader(
        title = "Live Vehicle Data",
        subtitle = "Real-time telematics feed",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomMetricCard(
            label = "Speed",
            value = "0",
            unit = "mph",
            modifier = Modifier.weight(1f),
        )
        CustomMetricCard(
            label = "Fuel / SOC",
            value = "72",
            unit = "%",
            modifier = Modifier.weight(1f),
        )
        CustomMetricCard(
            label = "Engine",
            value = "Off",
            unit = "ignition",
            modifier = Modifier.weight(1f),
        )
    }
    CustomStatusIndicator(
        label = "Vehicle connected — last sync 2 min ago",
        level = StatusLevel.Normal,
        modifier = Modifier.padding(bottom = OemSpacing.md),
    )
}

@Composable
private fun TabsSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomTabs(
            tabs = listOf("Live", "Trips", "Alerts"),
            selectedIndex = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.weight(1f),
        )
        if (selectedTab == 2) {
            CustomBadge(count = alerts.size)
        }
    }
}

@Composable
private fun TabContentSection(selectedTab: Int) {
    when (selectedTab) {
        0 -> LiveTabContent()
        1 -> TripsTabContent()
        2 -> AlertsTabContent()
    }
}

@Composable
private fun LiveTabContent() {
    CustomSectionHeader(
        title = "Current Status",
        subtitle = "Parked at home",
    )
    CustomList(
        items = listOf(
            CustomListItem("l1", "Location", "742 Evergreen Terrace"),
            CustomListItem("l2", "Heading", "N — stationary"),
            CustomListItem("l3", "Avg. speed (trip)", "34 mph"),
        ),
        modifier = Modifier.padding(vertical = OemSpacing.md),
        key = { it.id },
        scrollable = false,
    ) { item ->
        CustomListItemRow(title = item.title, subtitle = item.subtitle)
    }
}

@Composable
private fun TripsTabContent() {
    CustomSectionHeader(
        title = "Recent Trips",
        subtitle = "Last 7 days",
    )
    CustomList(
        items = tripHistory,
        modifier = Modifier.padding(vertical = OemSpacing.md),
        key = { it.id },
        scrollable = false,
    ) { item ->
        CustomListItemRow(title = item.title, subtitle = item.subtitle)
    }
}

@Composable
private fun AlertsTabContent() {
    CustomSectionHeader(
        title = "Vehicle Alerts",
        subtitle = "${alerts.size} active notifications",
    )
    Column(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        alerts.forEach { (title, subtitle, level) ->
            CustomStatusIndicator(
                label = title,
                level = level,
                modifier = Modifier.padding(bottom = OemSpacing.xs),
            )
            CustomListTile(
                title = subtitle,
                showChevron = false,
                onClick = null,
            )
        }
    }
}
