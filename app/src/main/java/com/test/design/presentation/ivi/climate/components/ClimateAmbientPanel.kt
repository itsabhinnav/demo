package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.ClimateTemperatureBand
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.theme.ClimateCardActiveRadii
import com.test.design.theme.ClimateCardRestRadii

@Composable
fun ClimateAmbientPanel(
    temperature: Int,
    temperatureFraction: Float,
    band: ClimateTemperatureBand,
    isAcEnabled: Boolean,
    fanSpeed: Int,
    maxFanSpeed: Int,
    morphExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<androidx.compose.ui.unit.IntSize>()
    val animatedFraction by animateFloatAsState(
        targetValue = temperatureFraction,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        label = "ambient_fraction",
    )
    val fanFraction = fanSpeed / maxFanSpeed.toFloat()
    val outlineColor = MaterialTheme.colorScheme.outline
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val primaryColor = MaterialTheme.colorScheme.primary

    MorphingDetailSurfaceCard(
        morphExpanded = morphExpanded,
        compactRadii = ClimateCardRestRadii,
        expandedRadii = ClimateCardActiveRadii,
        modifier = modifier.animateContentSize(animationSpec = spatialSpec),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Cabin feel", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = band.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    AmbientStat(
                        label = "Target",
                        value = "$temperature°",
                    )
                    AmbientStat(
                        label = "Airflow",
                        value = if (isAcEnabled) "Active" else "Idle",
                    )
                }
            }
            Canvas(
                modifier = Modifier
                    .height(72.dp)
                    .weight(0.55f),
            ) {
                val stroke = 8.dp.toPx()
                val center = Offset(size.width / 2f, size.height * 0.82f)
                val radius = size.width * 0.42f
                drawArc(
                    color = outlineColor.copy(alpha = 0.25f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            tertiaryColor,
                            primaryColor,
                        ),
                    ),
                    startAngle = 180f,
                    sweepAngle = 180f * animatedFraction,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = primaryColor.copy(alpha = 0.35f * fanFraction),
                    startAngle = 180f,
                    sweepAngle = 180f * fanFraction,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                    style = Stroke(width = stroke * 2.4f, cap = StrokeCap.Round),
                )
            }
        }
    }
}

@Composable
private fun AmbientStat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
    }
}
