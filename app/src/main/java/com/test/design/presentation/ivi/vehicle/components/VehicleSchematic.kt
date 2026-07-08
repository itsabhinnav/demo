package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.vehicle.TirePressure
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget

@Composable
fun VehicleSchematic(
    tires: List<TirePressure>,
    driveModeAccent: Color,
    onTireClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    val bodyPulse by animateFloatAsState(
        targetValue = 1f,
        animationSpec = motionScheme.slowSpatialSpec(),
        label = "body_pulse",
    )

    BoxWithConstraints(
        modifier = modifier
            .clip(ExpressiveShapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.65f)),
    ) {
        val w = maxWidth
        val h = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            val bodyWidth = size.width * 0.52f
            val bodyHeight = size.height * 0.72f
            val left = (size.width - bodyWidth) / 2f
            val top = (size.height - bodyHeight) / 2f
            drawRoundRect(
                color = driveModeAccent.copy(alpha = 0.18f),
                topLeft = Offset(left, top),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(bodyWidth * 0.18f, bodyWidth * 0.18f),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.12f),
                topLeft = Offset(left, top),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(bodyWidth * 0.18f, bodyWidth * 0.18f),
                style = Stroke(width = 3.dp.toPx()),
            )
            val wheelRadius = bodyWidth * 0.11f
            val wheelXs = listOf(left + bodyWidth * 0.18f, left + bodyWidth * 0.82f)
            val wheelYs = listOf(top + bodyHeight * 0.14f, top + bodyHeight * 0.86f)
            wheelXs.forEach { x ->
                wheelYs.forEach { y ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = wheelRadius,
                        center = Offset(x, y),
                        style = Stroke(width = 4.dp.toPx()),
                    )
                }
            }
        }

        tires.forEach { tire ->
            val alignment = when (tire.position) {
                "FL" -> Alignment.TopStart
                "FR" -> Alignment.TopEnd
                "RL" -> Alignment.BottomStart
                "RR" -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            TireBadge(
                tire = tire,
                onClick = { onTireClick(tire.position) },
                modifier = Modifier
                    .align(alignment)
                    .padding(CarDesignTokens.TouchTargetSpacing)
                    .scale(bodyPulse),
            )
        }

        Text(
            text = "Top view",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp),
        )
    }
}

@Composable
private fun TireBadge(
    tire: TirePressure,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val psiColor by animateColorAsState(
        targetValue = if (tire.isOptimal) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.error
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "tire_${tire.position}",
    )
    val badgeScale by animateFloatAsState(
        targetValue = if (tire.isOptimal) 1f else 1.08f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "tire_scale_${tire.position}",
    )
    Surface(
        modifier = modifier
            .scale(badgeScale)
            .carTouchTarget()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = if (tire.isOptimal) 2.dp else 8.dp,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            ColumnBadgeContent(tire = tire, psiColor = psiColor)
        }
    }
}

@Composable
private fun ColumnBadgeContent(tire: TirePressure, psiColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(tire.position, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("${tire.psi}", style = MaterialTheme.typography.titleLarge, color = psiColor)
        Text("psi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
