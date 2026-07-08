package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.vehicle.TirePressure
import com.test.design.presentation.ivi.vehicle.TirePressureStatus
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget
import kotlin.math.roundToInt

private data class WheelLayout(
    val position: String,
    val xFraction: Float,
    val yFraction: Float,
)

private val WheelLayouts = listOf(
    WheelLayout("FL", 0.24f, 0.26f),
    WheelLayout("FR", 0.76f, 0.26f),
    WheelLayout("RL", 0.24f, 0.74f),
    WheelLayout("RR", 0.76f, 0.74f),
)

@Composable
fun VehicleSchematic(
    tires: List<TirePressure>,
    selectedPosition: String?,
    driveModeAccent: Color,
    onTireClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tiresByPosition = remember(tires) { tires.associateBy { it.position } }
    val alertCount = tires.count { !it.isOptimal }
    val avgPsi = remember(tires) { tires.map { it.psi }.average().toInt() }

    BoxWithConstraints(
        modifier = modifier
            .clip(ExpressiveShapes.large)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f),
                    ),
                ),
            ),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.roundToPx() }
        val heightPx = with(density) { maxHeight.roundToPx() }
        val wheelSize = minOf(maxWidth, maxHeight) * 0.27f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val bodyWidth = size.width * 0.30f
            val bodyHeight = size.height * 0.64f
            val bodyLeft = (size.width - bodyWidth) / 2f
            val bodyTop = (size.height - bodyHeight) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        driveModeAccent.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.04f),
                    ),
                ),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(bodyWidth * 0.26f, bodyWidth * 0.26f),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.16f),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(bodyWidth * 0.26f, bodyWidth * 0.26f),
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        PressureStatusStrip(
            alertCount = alertCount,
            avgPsi = avgPsi,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
        )

        WheelLayouts.forEach { layout ->
            val tire = tiresByPosition[layout.position] ?: return@forEach
            val offsetX = ((widthPx * layout.xFraction) - with(density) { wheelSize.roundToPx() / 2 }).roundToInt()
            val offsetY = ((heightPx * layout.yFraction) - with(density) { wheelSize.roundToPx() / 2 }).roundToInt()
            TirePressureWheel(
                tire = tire,
                selected = selectedPosition == tire.position,
                onClick = { onTireClick(tire.position) },
                modifier = Modifier
                    .offset { IntOffset(offsetX, offsetY) }
                    .size(wheelSize),
            )
        }
    }
}

@Composable
private fun PressureStatusStrip(
    alertCount: Int,
    avgPsi: Int,
    modifier: Modifier = Modifier,
) {
    val statusColor by animateColorAsState(
        targetValue = if (alertCount == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "pressure_status_strip_color",
    )
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("TPMS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$avgPsi psi avg", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = if (alertCount == 0) "Balanced" else "$alertCount alert",
                style = MaterialTheme.typography.labelMedium,
                color = statusColor,
            )
        }
    }
}

@Composable
private fun TirePressureWheel(
    tire: TirePressure,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    val animatedFill by animateFloatAsState(
        targetValue = tire.fillFraction,
        animationSpec = motionScheme.slowSpatialSpec(),
        label = "tire_fill_${tire.position}",
    )
    val arcColor by animateColorAsState(
        targetValue = tire.status.color(),
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "tire_arc_${tire.position}",
    )
    val cardColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        },
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "tire_card_${tire.position}",
    )
    val emphasisScale by animateFloatAsState(
        targetValue = when {
            selected -> 1.08f
            !tire.isOptimal -> 1.04f
            else -> 1f
        },
        animationSpec = motionScheme.defaultSpatialSpec(),
        label = "tire_scale_${tire.position}",
    )

    Surface(
        modifier = modifier
            .scale(emphasisScale)
            .carTouchTarget()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = cardColor,
        shadowElevation = if (!tire.isOptimal) 8.dp else 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(tire.position, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    val stroke = 6.dp.toPx()
                    drawArc(
                        color = Color.White.copy(alpha = 0.16f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = arcColor,
                        startAngle = 135f,
                        sweepAngle = 270f * animatedFill,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
                AnimatedContent(
                    targetState = tire.psi,
                    transitionSpec = {
                        val direction = if (targetState > initialState) 1 else -1
                        slideInVertically(animationSpec = motionScheme.defaultSpatialSpec()) { h -> direction * h } togetherWith
                            slideOutVertically(animationSpec = motionScheme.defaultSpatialSpec()) { h -> -direction * h }
                    },
                    label = "psi_${tire.position}",
                ) { psi ->
                    Text(
                        text = "$psi",
                        style = MaterialTheme.typography.titleMedium,
                        color = arcColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Text(
                text = tire.status.label(),
                style = MaterialTheme.typography.labelSmall,
                color = arcColor,
            )
        }
    }
}

@Composable
private fun TirePressureStatus.color(): Color = when (this) {
    TirePressureStatus.Optimal -> MaterialTheme.colorScheme.primary
    TirePressureStatus.Low -> MaterialTheme.colorScheme.error
    TirePressureStatus.High -> MaterialTheme.colorScheme.tertiary
}

private fun TirePressureStatus.label(): String = when (this) {
    TirePressureStatus.Optimal -> "OK"
    TirePressureStatus.Low -> "LOW"
    TirePressureStatus.High -> "HIGH"
}
