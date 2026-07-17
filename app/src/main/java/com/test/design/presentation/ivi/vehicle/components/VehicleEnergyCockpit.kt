package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@Composable
fun VehicleEnergyCockpit(
    percent: Int,
    rangeMiles: Int,
    maxRangeMiles: Int,
    isCharging: Boolean,
    chargeRateKw: Float,
    gaugeShape: Shape,
    onGaugeClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    controlsModifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val gaugeSize = if (compact) 100.dp else 260.dp
    val strokeDp = if (compact) 6.dp else 14.dp
    val rangeStrokeDp = if (compact) 4.dp else 8.dp
    val padding = if (compact) 4.dp else CarDesignTokens.TouchTargetSpacing
    val motionScheme = MaterialTheme.motionScheme
    val animatedPercent by animateFloatAsState(
        targetValue = percent / 100f,
        animationSpec = motionScheme.slowSpatialSpec(),
        label = "battery_percent",
    )
    val animatedRange by animateFloatAsState(
        targetValue = rangeMiles / maxRangeMiles.toFloat(),
        animationSpec = motionScheme.defaultSpatialSpec(),
        label = "range_fraction",
    )
    val arcColor by animateColorAsState(
        targetValue = when {
            isCharging -> Color(0xFF00E5FF)
            percent >= 60 -> MaterialTheme.colorScheme.primary
            percent >= 30 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        },
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "battery_color",
    )
    val pulseTransition = rememberInfiniteTransition(label = "charge_pulse")
    val chargePulse by pulseTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "charge_pulse_scale",
    )
    val orbDrift by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb_drift",
    )

    Box(
        modifier = modifier
            .then(if (compact) Modifier.wrapContentHeight() else Modifier)
            .clip(gaugeShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                    ),
                    center = Offset(0.3f + orbDrift * 0.15f, 0.25f),
                    radius = 600f,
                ),
            )
            .carTouchTarget()
            .clickable(onClick = onGaugeClick),
    ) {
        if (!compact) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            arcColor.copy(alpha = 0.18f),
                            Color.Transparent,
                        ),
                        center = center,
                        radius = size.minDimension * 0.55f,
                    ),
                    radius = size.minDimension * 0.55f,
                    center = center,
                )
            }
        }

        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                GaugeWithStats(
                    percent = percent,
                    rangeMiles = rangeMiles,
                    gaugeSize = gaugeSize,
                    strokeDp = strokeDp,
                    rangeStrokeDp = rangeStrokeDp,
                    animatedPercent = animatedPercent,
                    animatedRange = animatedRange,
                    arcColor = arcColor,
                    isCharging = isCharging,
                    chargePulse = chargePulse,
                    compact = true,
                    contentModifier = contentModifier,
                )
                Text(
                    text = when {
                        isCharging -> "Charging · ${chargeRateKw.toInt()} kW"
                        percent < 30 -> "Low · $rangeMiles mi"
                        else -> "Ready · $rangeMiles mi"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = controlsModifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = if (isCharging) "Charging" else "Energy",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            GaugeWithStats(
                percent = percent,
                rangeMiles = rangeMiles,
                gaugeSize = gaugeSize,
                strokeDp = strokeDp,
                rangeStrokeDp = rangeStrokeDp,
                animatedPercent = animatedPercent,
                animatedRange = animatedRange,
                arcColor = arcColor,
                isCharging = isCharging,
                chargePulse = chargePulse,
                compact = false,
                contentModifier = contentModifier,
            )

            Column(
                modifier = controlsModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = when {
                        isCharging -> "${chargeRateKw.toInt()} kW"
                        percent < 30 -> "Low battery"
                        else -> "Ready to drive"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = when {
                        isCharging -> "Plugged in"
                        else -> "$maxRangeMiles mi capacity"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
        }
    }
}

@Composable
private fun GaugeWithStats(
    percent: Int,
    rangeMiles: Int,
    gaugeSize: androidx.compose.ui.unit.Dp,
    strokeDp: androidx.compose.ui.unit.Dp,
    rangeStrokeDp: androidx.compose.ui.unit.Dp,
    animatedPercent: Float,
    animatedRange: Float,
    arcColor: Color,
    isCharging: Boolean,
    chargePulse: Float,
    compact: Boolean,
    contentModifier: Modifier,
) {
    Box(
        modifier = Modifier.size(gaugeSize),
        contentAlignment = Alignment.Center,
    ) {
        val gaugeScale = if (isCharging) chargePulse else 1f
        Canvas(
            modifier = Modifier
                .size(gaugeSize)
                .graphicsLayer {
                    scaleX = gaugeScale
                    scaleY = gaugeScale
                },
        ) {
            val stroke = strokeDp.toPx()
            val rangeStroke = rangeStrokeDp.toPx()
            rotate(135f) {
                drawArc(
                    color = Color.White.copy(alpha = 0.08f),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = rangeStroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = arcColor.copy(alpha = 0.35f),
                    startAngle = 0f,
                    sweepAngle = 270f * animatedRange,
                    useCenter = false,
                    style = Stroke(width = rangeStroke, cap = StrokeCap.Round),
                )
            }
            rotate(135f) {
                drawArc(
                    color = Color.White.copy(alpha = 0.14f),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = arcColor,
                    startAngle = 0f,
                    sweepAngle = 270f * animatedPercent,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                if (isCharging) {
                    drawArc(
                        color = arcColor.copy(alpha = 0.35f),
                        startAngle = 0f,
                        sweepAngle = 270f * animatedPercent,
                        useCenter = false,
                        style = Stroke(width = stroke * 2.2f, cap = StrokeCap.Round),
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = contentModifier,
        ) {
            AnimatedStatCounter(
                value = percent,
                suffix = "%",
                style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displayLarge,
            )
            Text(
                text = "$rangeMiles mi",
                style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun AnimatedStatCounter(
    value: Int,
    suffix: String = "",
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium,
    modifier: Modifier = Modifier,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<androidx.compose.ui.unit.IntOffset>()
    AnimatedContent(
        targetState = value,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            slideInVertically(animationSpec = motionSpec) { h -> direction * h } togetherWith
                slideOutVertically(animationSpec = motionSpec) { h -> -direction * h }
        },
        label = "stat_counter",
    ) { target ->
        Text(
            text = "$target$suffix",
            style = style,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}
