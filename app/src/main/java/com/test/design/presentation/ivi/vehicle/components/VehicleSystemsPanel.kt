package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DiscFull
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.vehicle.RegenLevel
import com.test.design.presentation.ivi.vehicle.SystemHealth
import com.test.design.presentation.ivi.vehicle.VehicleSystemMetric
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget

@Composable
fun VehicleSystemsPanel(
    systems: List<VehicleSystemMetric>,
    regenLevel: RegenLevel,
    selectedSystemId: String?,
    isCharging: Boolean,
    onRegenClick: () -> Unit,
    onSystemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val overallHealth = remember(systems) { systems.map { it.valuePercent }.average().toInt() }
    val alertCount = systems.count { it.health != SystemHealth.Good }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ExpressiveShapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.92f),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CarDesignTokens.TouchTargetSpacing),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Vehicle health", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (alertCount == 0) "All systems nominal" else "$alertCount item${if (alertCount == 1) "" else "s"} to review",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                HealthScoreBadge(score = overallHealth)
            }

            PowerFlowStrip(
                regenLevel = regenLevel,
                isCharging = isCharging,
                onRegenClick = onRegenClick,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                systems.forEach { system ->
                    SystemMetricRow(
                        system = system,
                        selected = selectedSystemId == system.id,
                        onClick = { onSystemClick(system.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthScoreBadge(score: Int) {
    val color by animateColorAsState(
        targetValue = when {
            score >= 80 -> MaterialTheme.colorScheme.primary
            score >= 60 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "health_score_color",
    )
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.16f),
    ) {
        Text(
            text = "$score",
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun PowerFlowStrip(
    regenLevel: RegenLevel,
    isCharging: Boolean,
    onRegenClick: () -> Unit,
) {
    val flowStrength by animateFloatAsState(
        targetValue = when {
            isCharging -> 1f
            regenLevel == RegenLevel.Off -> 0.15f
            else -> regenLevel.strength
        },
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        label = "power_flow_strength",
    )
    val flowColor by animateColorAsState(
        targetValue = if (isCharging) Color(0xFF00E5FF) else MaterialTheme.colorScheme.primary,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "power_flow_color",
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .carTouchTarget()
            .clickable(onClick = onRegenClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isCharging) "Charging flow" else "Regenerative braking",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = if (isCharging) "AC" else regenLevel.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = flowColor,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FlowNode(icon = Icons.Default.BatteryChargingFull, active = isCharging, tint = flowColor)
                FlowConnector(strength = flowStrength, color = flowColor, modifier = Modifier.weight(1f))
                FlowNode(icon = Icons.Default.ElectricBolt, active = true, tint = flowColor)
                FlowConnector(strength = flowStrength * 0.85f, color = flowColor, modifier = Modifier.weight(1f))
                FlowNode(
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    active = !isCharging && regenLevel != RegenLevel.Off,
                    tint = flowColor,
                )
            }
        }
    }
}

@Composable
private fun FlowNode(
    icon: ImageVector,
    active: Boolean,
    tint: Color,
) {
    val alpha by animateFloatAsState(
        targetValue = if (active) 1f else 0.35f,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "flow_node_alpha",
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tint.copy(alpha = 0.14f * alpha))
            .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint.copy(alpha = alpha),
        )
    }
}

@Composable
private fun FlowConnector(
    strength: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(strength.coerceIn(0.08f, 1f))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(color.copy(alpha = 0.2f), color),
                    ),
                ),
        )
    }
}

@Composable
private fun SystemMetricRow(
    system: VehicleSystemMetric,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = system.valuePercent / 100f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "system_progress_${system.id}",
    )
    val healthColor by animateColorAsState(
        targetValue = system.health.color(),
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "system_health_${system.id}",
    )
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "system_row_bg_${system.id}",
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .carTouchTarget()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = system.icon(),
                contentDescription = null,
                tint = healthColor,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(system.label, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${system.valuePercent}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = healthColor,
                    )
                }
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = healthColor,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                Text(
                    system.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SystemHealth.color(): Color = when (this) {
    SystemHealth.Good -> MaterialTheme.colorScheme.primary
    SystemHealth.Caution -> MaterialTheme.colorScheme.tertiary
    SystemHealth.Warning -> MaterialTheme.colorScheme.error
}

private fun VehicleSystemMetric.icon(): ImageVector = when (id) {
    "motor" -> Icons.Default.ElectricBolt
    "brakes" -> Icons.Default.DiscFull
    "battery" -> Icons.Default.BatteryChargingFull
    "cabin" -> Icons.Default.AcUnit
    else -> Icons.Default.ElectricBolt
}
