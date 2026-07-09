package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.vehicle.DriveMode
import com.test.design.presentation.ivi.vehicle.RegenLevel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes

@Composable
fun VehicleDriveInsightsCard(
    driveMode: DriveMode,
    regenLevel: RegenLevel,
    efficiencyMpkWh: Float,
    rangeMiles: Int,
    modifier: Modifier = Modifier,
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ExpressiveShapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.92f),
        shadowElevation = 4.dp,
    ) {
        AnimatedContent(
            targetState = driveMode,
            transitionSpec = {
                fadeIn(animationSpec = effectsSpec) togetherWith fadeOut(animationSpec = effectsSpec)
            },
            label = "drive_insights_content",
            modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
        ) { mode ->
            when (mode) {
                DriveMode.Eco -> EcoInsightsContent(
                    efficiencyMpkWh = efficiencyMpkWh,
                    rangeMiles = rangeMiles,
                    regenLevel = regenLevel,
                )
                DriveMode.Comfort -> ComfortInsightsContent()
                DriveMode.Sport -> SportInsightsContent(driveMode = mode)
            }
        }
    }
}

@Composable
private fun EcoInsightsContent(
    efficiencyMpkWh: Float,
    rangeMiles: Int,
    regenLevel: RegenLevel,
) {
    val regenStrength by animateFloatAsState(
        targetValue = regenLevel.strength,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        label = "eco_regen_strength",
    )
    InsightsHeader(
        icon = Icons.Default.Eco,
        title = "Range coach",
        subtitle = "Optimizing for efficiency",
    )
    InsightMetricRow(label = "Trip efficiency", value = "$efficiencyMpkWh mi/kWh")
    InsightMetricRow(label = "Projected range", value = "$rangeMiles mi")
    InsightMetricRow(
        label = "Regen capture",
        value = "${(regenStrength * 100).toInt()}%",
        emphasized = true,
    )
}

@Composable
private fun ComfortInsightsContent() {
    InsightsHeader(
        icon = Icons.Default.Shield,
        title = "Driver assists",
        subtitle = "All systems active",
    )
    AssistRow(label = "Adaptive cruise", status = "Engaged")
    AssistRow(label = "Lane keep", status = "Ready")
    AssistRow(label = "Blind-spot monitor", status = "Clear")
}

@Composable
private fun SportInsightsContent(driveMode: DriveMode) {
    val peakPower = driveMode.peakPowerKw()
    val torque = driveMode.peakTorqueNm()
    val powerColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "sport_power_color",
    )
    InsightsHeader(
        icon = Icons.Default.Speed,
        title = "Performance",
        subtitle = "Peak output available",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        PerformanceStat(
            label = "Power",
            value = "$peakPower kW",
            icon = Icons.Default.Bolt,
            color = powerColor,
            modifier = Modifier.weight(1f),
        )
        PerformanceStat(
            label = "Torque",
            value = "$torque Nm",
            icon = Icons.Default.Speed,
            color = powerColor,
            modifier = Modifier.weight(1f),
        )
    }
    Text(
        text = "0–60 in ${driveMode.zeroToSixtySeconds()} s",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun InsightsHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InsightMetricRow(
    label: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun AssistRow(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(status, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun PerformanceStat(
    label: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = ExpressiveShapes.medium,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        }
    }
}

private fun DriveMode.peakPowerKw(): Int = when (this) {
    DriveMode.Eco -> 120
    DriveMode.Comfort -> 180
    DriveMode.Sport -> 285
}

private fun DriveMode.peakTorqueNm(): Int = when (this) {
    DriveMode.Eco -> 240
    DriveMode.Comfort -> 320
    DriveMode.Sport -> 420
}

private fun DriveMode.zeroToSixtySeconds(): String = when (this) {
    DriveMode.Eco -> "6.8"
    DriveMode.Comfort -> "5.4"
    DriveMode.Sport -> "4.1"
}
