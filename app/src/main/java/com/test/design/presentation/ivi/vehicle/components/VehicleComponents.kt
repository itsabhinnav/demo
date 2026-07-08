package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.vehicle.DriveMode
import com.test.design.presentation.ivi.vehicle.TirePressure
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@Composable
fun AnimatedBatteryGauge(
    percent: Int,
    rangeMiles: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
    val animatedPercent by animateFloatAsState(
        targetValue = percent / 100f,
        animationSpec = motionSpec,
        label = "battery_percent",
    )
    val arcColor by animateColorAsState(
        targetValue = when {
            percent >= 60 -> MaterialTheme.colorScheme.primary
            percent >= 30 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>(),
        label = "battery_color",
    )
    Box(
        modifier = modifier
            .size(200.dp)
            .carTouchTarget()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth()) {
            val stroke = 18.dp.toPx()
            drawArc(
                color = Color.White.copy(alpha = 0.12f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = arcColor,
                startAngle = 135f,
                sweepAngle = 270f * animatedPercent,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
            Text("$rangeMiles mi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DriveModeSelector(
    selected: DriveMode,
    onSelected: (DriveMode) -> Unit,
    modifier: Modifier = Modifier,
) {
  // Reuse morphing segmented pattern via mapping DriveMode to a single-select row
    val modes = DriveMode.entries
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(CarDesignTokens.MinTouchTarget)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        modes.forEach { mode ->
            val isSelected = mode == selected
            val bg by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Color>(),
                label = "drive_mode_${mode.name}",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(CarDesignTokens.MinTouchTarget)
                    .background(bg)
                    .carTouchTarget()
                    .clickable { onSelected(mode) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = mode.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun TirePressureGrid(
    tires: List<TirePressure>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        tires.forEach { tire ->
            DetailSurfaceCard(
                modifier = Modifier.weight(1f),
                emphasized = !tire.isOptimal,
            ) {
                Text(tire.position, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${tire.psi} psi", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
