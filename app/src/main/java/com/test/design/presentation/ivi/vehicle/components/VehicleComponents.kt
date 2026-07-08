package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.test.design.core.motion.AppMotionScheme
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.vehicle.DriveMode
import com.test.design.presentation.ivi.vehicle.TirePressure
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.VehicleCardActiveRadii
import com.test.design.theme.VehicleCardRestRadii
import com.test.design.theme.carTouchTarget
import kotlin.math.roundToInt

@Composable
fun AnimatedBatteryGauge(
    percent: Int,
    rangeMiles: Int,
    gaugeShape: Shape,
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
            .size(220.dp)
            .clip(gaugeShape)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            .carTouchTarget()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
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
            Text(
                "$percent%",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "$rangeMiles mi range",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun DriveModeSelector(
    selected: DriveMode,
    onSelected: (DriveMode) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
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
            MorphingDetailSurfaceCard(
                morphExpanded = !tire.isOptimal,
                compactRadii = VehicleCardRestRadii,
                expandedRadii = VehicleCardActiveRadii,
                emphasized = !tire.isOptimal,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    tire.position,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val psiColor by animateColorAsState(
                    targetValue = if (tire.isOptimal) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>(),
                    label = "tire_psi_${tire.position}",
                )
                Text(
                    "${tire.psi} psi",
                    style = MaterialTheme.typography.titleLarge,
                    color = psiColor,
                )
            }
        }
    }
}

@Composable
fun VehicleMotionLabPanel(
    selectedScheme: AppMotionScheme,
    expanded: Boolean,
    previewTrigger: Int,
    onSchemeSelected: (AppMotionScheme) -> Unit,
    onReplayPreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(
            animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        ) + fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
        exit = shrinkVertically(
            animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        ) + fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
        modifier = modifier,
    ) {
        MorphingDetailSurfaceCard(
            morphExpanded = true,
            compactRadii = VehicleCardRestRadii,
            expandedRadii = VehicleCardActiveRadii,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Motion scheme", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Preview Material 3 motion tokens on this screen without changing the global app setting.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            ) {
                AppMotionScheme.entries.forEach { scheme ->
                    FilterChip(
                        selected = selectedScheme == scheme,
                        onClick = { onSchemeSelected(scheme) },
                        label = { Text(scheme.label, style = MaterialTheme.typography.labelLarge) },
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = CarDesignTokens.TouchTargetSpacing),
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MotionTokenPreview(
                    label = "Spatial",
                    previewTrigger = previewTrigger,
                    modifier = Modifier.weight(1f),
                    spatial = true,
                )
                MotionTokenPreview(
                    label = "Effects",
                    previewTrigger = previewTrigger,
                    modifier = Modifier.weight(1f),
                    spatial = false,
                )
                Button(onClick = onReplayPreview) {
                    Text("Replay", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun MotionTokenPreview(
    label: String,
    previewTrigger: Int,
    spatial: Boolean,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    val spatialOffset by animateDpAsState(
        targetValue = if (previewTrigger % 2 == 0) 8.dp else 96.dp,
        animationSpec = motionScheme.defaultSpatialSpec(),
        label = "vehicle_motion_spatial_$label",
    )
    val effectsScale by animateFloatAsState(
        targetValue = if (previewTrigger % 2 == 0) 0.65f else 1f,
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "vehicle_motion_effects_$label",
    )
    Column(
        modifier = modifier
            .clip(ExpressiveShapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(CarDesignTokens.TouchTargetSpacing),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(ExpressiveShapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            if (spatial) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(spatialOffset.roundToPx(), 12.dp.roundToPx()) }
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            } else {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                        .size(32.dp)
                        .scale(effectsScale)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary),
                )
            }
        }
    }
}
