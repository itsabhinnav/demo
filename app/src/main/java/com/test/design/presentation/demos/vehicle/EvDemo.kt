package com.test.design.presentation.demos.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomAssistChip
import com.test.design.component.components.CustomLinearProgress
import com.test.design.component.components.CustomMetricCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSegmentedButtonRow
import com.test.design.component.components.CustomStatRow
import com.test.design.component.components.CustomStatusIndicator
import com.test.design.component.components.StatusLevel
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold

@Composable
fun EvDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var driveMode by remember { mutableIntStateOf(1) }
    val batteryLevel by remember { mutableFloatStateOf(0.72f) }

    DemoScaffold(
        title = "EV Dashboard",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            Column {
                CustomStatRow(
                    label = "Est. arrival charge",
                    value = "84%",
                )
                CustomStatRow(
                    label = "Last charge",
                    value = "Today, 7:42 AM",
                )
            }
        },
    ) {
        BatterySection(batteryLevel = batteryLevel)
        MetricsSection()
        DriveModeSection(
            selectedIndex = driveMode,
            onModeSelected = { driveMode = it },
        )
        ChargingActionsSection()
    }
}

@Composable
private fun BatterySection(batteryLevel: Float) {
    CustomSectionHeader(
        title = "Battery",
        subtitle = "State of charge and charging status",
    )
    CustomLinearProgress(
        progress = { batteryLevel },
        label = "State of charge — ${(batteryLevel * 100).toInt()}%",
        modifier = Modifier.padding(vertical = OemSpacing.md),
    )
    CustomStatusIndicator(
        label = "Charging — DC fast, 48 kW",
        level = StatusLevel.Info,
        modifier = Modifier.padding(bottom = OemSpacing.md),
    )
}

@Composable
private fun MetricsSection() {
    CustomSectionHeader(
        title = "Range & Efficiency",
        subtitle = "Live EV metrics",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomMetricCard(
            label = "Range",
            value = "214",
            unit = "miles remaining",
            modifier = Modifier.weight(1f),
        )
        CustomMetricCard(
            label = "Efficiency",
            value = "3.4",
            unit = "mi/kWh",
            modifier = Modifier.weight(1f),
        )
        CustomMetricCard(
            label = "Charge rate",
            value = "48",
            unit = "kW",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DriveModeSection(
    selectedIndex: Int,
    onModeSelected: (Int) -> Unit,
) {
    CustomSectionHeader(
        title = "Drive Mode",
        subtitle = "Eco, Normal, or Sport",
    )
    CustomSegmentedButtonRow(
        options = listOf("Eco", "Normal", "Sport"),
        selectedIndex = selectedIndex,
        onOptionSelected = onModeSelected,
        modifier = Modifier.padding(vertical = OemSpacing.md),
    )
}

@Composable
private fun ChargingActionsSection() {
    CustomSectionHeader(
        title = "Charging",
        subtitle = "Quick actions",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomAssistChip(
            label = "Precondition",
            onClick = {},
            leadingIcon = Icons.Default.Bolt,
        )
        CustomAssistChip(
            label = "Find charger",
            onClick = {},
            leadingIcon = Icons.Default.EvStation,
        )
        CustomAssistChip(
            label = "Schedule",
            onClick = {},
            leadingIcon = Icons.Default.Schedule,
        )
    }
}
