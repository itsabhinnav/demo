package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.ClimateTemperatureLayout
import com.test.design.theme.CarDesignTokens

@Composable
fun ClimateFluidDial(
    temperature: Int,
    temperatureFraction: Float,
    isAcEnabled: Boolean,
    dialShape: Shape,
    layoutProfile: ClimateTemperatureLayout,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val motionScheme = MaterialTheme.motionScheme
    val animatedFraction by animateFloatAsState(
        targetValue = temperatureFraction,
        animationSpec = motionScheme.slowSpatialSpec(),
        label = "climate_dial_fraction",
    )
    val fluidTransition = rememberInfiniteTransition(label = "climate_fluid")
    val orbDrift by fluidTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(layoutProfile.fluidDriftMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "climate_orb_drift",
    )
    val secondaryDrift by fluidTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween((layoutProfile.fluidDriftMillis * 0.72f).toInt()),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "climate_secondary_drift",
    )
    val acPulse by fluidTransition.animateFloat(
        initialValue = 1f - layoutProfile.dialPulseIntensity,
        targetValue = 1f + layoutProfile.dialPulseIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "climate_ac_pulse",
    )
    val dialScale = if (isAcEnabled) acPulse else 1f
    val coolBias = 1f - animatedFraction
    val warmBias = animatedFraction
    val gradientCenterX = 0.28f + orbDrift * 0.18f * warmBias + secondaryDrift * 0.1f * coolBias
    val gradientCenterY = 0.22f + secondaryDrift * 0.14f * coolBias + orbDrift * 0.12f * warmBias
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .clip(dialShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.24f + warmBias * 0.08f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f + coolBias * 0.1f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
                    ),
                    center = Offset(gradientCenterX, gradientCenterY),
                    radius = 520f,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val minDim = size.minDimension

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.16f + warmBias * 0.1f),
                        Color.Transparent,
                    ),
                    center = Offset(
                        center.x + (orbDrift - 0.5f) * minDim * 0.22f,
                        center.y + (secondaryDrift - 0.5f) * minDim * 0.18f,
                    ),
                    radius = minDim * 0.42f,
                ),
                radius = minDim * 0.42f,
                center = Offset(
                    center.x + (orbDrift - 0.5f) * minDim * 0.22f,
                    center.y + (secondaryDrift - 0.5f) * minDim * 0.18f,
                ),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        tertiaryColor.copy(alpha = 0.14f + coolBias * 0.12f),
                        Color.Transparent,
                    ),
                    center = Offset(
                        center.x - (orbDrift - 0.5f) * minDim * 0.2f,
                        center.y + (orbDrift - 0.5f) * minDim * 0.16f,
                    ),
                    radius = minDim * 0.3f,
                ),
                radius = minDim * 0.3f,
                center = Offset(
                    center.x - (orbDrift - 0.5f) * minDim * 0.2f,
                    center.y + (orbDrift - 0.5f) * minDim * 0.16f,
                ),
            )

            rotate(135f, center) {
                drawArc(
                    color = Color.White.copy(alpha = 0.1f),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - minDim * 0.38f, center.y - minDim * 0.38f),
                    size = androidx.compose.ui.geometry.Size(minDim * 0.76f, minDim * 0.76f),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    color = primaryColor.copy(alpha = 0.55f),
                    startAngle = 0f,
                    sweepAngle = 270f * animatedFraction,
                    useCenter = false,
                    topLeft = Offset(center.x - minDim * 0.38f, center.y - minDim * 0.38f),
                    size = androidx.compose.ui.geometry.Size(minDim * 0.76f, minDim * 0.76f),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
                )
                if (isAcEnabled) {
                    drawArc(
                        color = primaryColor.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 270f * animatedFraction,
                        useCenter = false,
                        topLeft = Offset(center.x - minDim * 0.38f, center.y - minDim * 0.38f),
                        size = androidx.compose.ui.geometry.Size(minDim * 0.76f, minDim * 0.76f),
                        style = Stroke(width = 22.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = dialScale
                    scaleY = dialScale
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()
                Text(
                    text = if (isAcEnabled) "A/C On" else "A/C Off",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
