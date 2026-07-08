package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
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
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget
import kotlin.math.roundToInt

private data class WheelLayout(
    val position: String,
    val xFraction: Float,
    val yFraction: Float,
)

private val WheelLayouts = listOf(
    WheelLayout("FL", 0.24f, 0.22f),
    WheelLayout("FR", 0.76f, 0.22f),
    WheelLayout("RL", 0.24f, 0.78f),
    WheelLayout("RR", 0.76f, 0.78f),
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
    val averagePsi = remember(tires) { tires.map { it.psi }.average().toInt() }
    val errorColor = MaterialTheme.colorScheme.error
    val primarySoft = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

    BoxWithConstraints(
        modifier = modifier
            .clip(ExpressiveShapes.large)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        driveModeAccent.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
                    ),
                ),
            ),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.roundToPx() }
        val heightPx = with(density) { maxHeight.roundToPx() }
        val wheelSize = minOf(maxWidth, maxHeight) * 0.28f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val bodyWidth = size.width * 0.34f
            val bodyHeight = size.height * 0.62f
            val bodyLeft = (size.width - bodyWidth) / 2f
            val bodyTop = (size.height - bodyHeight) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        driveModeAccent.copy(alpha = 0.28f),
                        driveModeAccent.copy(alpha = 0.10f),
                    ),
                ),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(bodyWidth * 0.22f, bodyWidth * 0.22f),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.14f),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(bodyWidth * 0.22f, bodyWidth * 0.22f),
                style = Stroke(width = 2.5.dp.toPx()),
            )

            val windshieldHeight = bodyHeight * 0.22f
            drawRoundRect(
                color = Color.White.copy(alpha = 0.08f),
                topLeft = Offset(bodyLeft + bodyWidth * 0.12f, bodyTop + bodyHeight * 0.08f),
                size = Size(bodyWidth * 0.76f, windshieldHeight),
                cornerRadius = CornerRadius(bodyWidth * 0.08f, bodyWidth * 0.08f),
            )

            val center = Offset(size.width / 2f, size.height / 2f)
            WheelLayouts.forEach { layout ->
                val wheelCenter = Offset(size.width * layout.xFraction, size.height * layout.yFraction)
                val tire = tiresByPosition[layout.position]
                val lineColor = when (tire?.status) {
                    TirePressureStatus.Low, TirePressureStatus.High -> errorColor
                    else -> Color.White.copy(alpha = 0.16f)
                }
                drawLine(
                    color = lineColor.copy(alpha = if (tire?.isOptimal == false) 0.75f else 0.35f),
                    start = center,
                    end = wheelCenter,
                    strokeWidth = if (tire?.isOptimal == false) 3.dp.toPx() else 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        TpmsHub(
            alertCount = alertCount,
            averagePsi = averagePsi,
            modifier = Modifier.align(Alignment.Center),
        )

        WheelLayouts.forEach { layout ->
            val tire = tiresByPosition[layout.position] ?: return@forEach
            val offsetX = ((widthPx * layout.xFraction) - with(density) { wheelSize.roundToPx() / 2 }).roundToInt()
            val offsetY = ((heightPx * layout.yFraction) - with(density) { wheelSize.roundToPx() / 2 }).roundToInt()
            TirePressureWheel(
                tire = tire,
                selected = selectedPosition == tire.position,
                optimalBandColor = primarySoft,
                onClick = { onTireClick(tire.position) },
                modifier = Modifier
                    .offset { IntOffset(offsetX, offsetY) }
                    .size(wheelSize),
            )
        }

        TirePressureLegend(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp, start = 12.dp, end = 12.dp),
        )
    }
}

@Composable
private fun TpmsHub(
    alertCount: Int,
    averagePsi: Int,
    modifier: Modifier = Modifier,
) {
    val statusColor by animateColorAsState(
        targetValue = if (alertCount == 0) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "tpms_status",
    )
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text("TPMS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "$averagePsi",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text("avg psi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.18f),
            ) {
                Text(
                    text = if (alertCount == 0) "All OK" else "$alertCount alert",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun TirePressureWheel(
    tire: TirePressure,
    selected: Boolean,
    optimalBandColor: Color,
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
    val emphasisScale by animateFloatAsState(
        targetValue = when {
            selected -> 1.12f
            !tire.isOptimal -> 1.06f
            else -> 1f
        },
        animationSpec = motionScheme.defaultSpatialSpec(),
        label = "tire_scale_${tire.position}",
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.primary
            !tire.isOptimal -> MaterialTheme.colorScheme.error
            else -> Color.Transparent
        },
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "tire_border_${tire.position}",
    )

    Box(
        modifier = modifier
            .scale(emphasisScale)
            .carTouchTarget()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 7.dp.toPx()
            val optimalStart = ((TirePressure.OPTIMAL_MIN_PSI - TirePressure.MIN_PSI).toFloat() /
                (TirePressure.MAX_PSI - TirePressure.MIN_PSI).toFloat()) * 270f
            val optimalSweep = ((TirePressure.OPTIMAL_MAX_PSI - TirePressure.OPTIMAL_MIN_PSI).toFloat() /
                (TirePressure.MAX_PSI - TirePressure.MIN_PSI).toFloat()) * 270f

            drawArc(
                color = Color.White.copy(alpha = 0.12f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = optimalBandColor,
                startAngle = 135f + optimalStart,
                sweepAngle = optimalSweep,
                useCenter = false,
                style = Stroke(width = stroke * 0.55f, cap = StrokeCap.Round),
            )
            drawArc(
                color = arcColor,
                startAngle = 135f,
                sweepAngle = 270f * animatedFill,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            if (!tire.isOptimal) {
                drawCircle(
                    color = arcColor.copy(alpha = 0.15f),
                    radius = size.minDimension * 0.52f,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }

        Column(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
                .border(
                    width = if (selected || !tire.isOptimal) 2.dp else 0.dp,
                    color = borderColor,
                    shape = CircleShape,
                )
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                tire.position,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
                    style = MaterialTheme.typography.titleLarge,
                    color = arcColor,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = tire.status.label(),
                style = MaterialTheme.typography.labelSmall,
                color = arcColor.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun TirePressureLegend(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendItem(color = MaterialTheme.colorScheme.primary, label = "Optimal ${TirePressure.OPTIMAL_MIN_PSI}–${TirePressure.OPTIMAL_MAX_PSI}")
            LegendItem(color = MaterialTheme.colorScheme.error, label = "Low / High")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    TirePressureStatus.Low -> "Low"
    TirePressureStatus.High -> "High"
}
