package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.TemperatureUnit
import com.test.design.presentation.ivi.climate.rememberClimateHaptics
import com.test.design.presentation.ivi.climate.temperatureScaleDragSteps
import com.test.design.presentation.ivi.climate.temperatureScaleEdgeFade
import com.test.design.presentation.ivi.climate.temperatureScaleIndex
import com.test.design.presentation.ivi.climate.temperatureScaleIsMajorTick
import com.test.design.presentation.ivi.climate.temperatureScaleSegmentStep
import com.test.design.presentation.ivi.climate.temperatureScaleTickHeightFraction
import com.test.design.presentation.ivi.climate.toDisplayTemperature
import kotlin.math.abs
import kotlin.math.roundToInt

private val TickSpacing = 12.dp
private val MaxTickLength = 22.dp

/**
 * Fixed needle, sliding scale. Drag follows the finger: the value under the
 * needle is the set-point. Cooler degrees sit to the left, warmer to the right.
 */
@Composable
fun TemperatureScale(
    temperature: Float,
    temperatureLabel: String,
    sortKey: Float,
    minTemperature: Float,
    maxTemperature: Float,
    temperatureStepCelsius: Float,
    temperatureStepFahrenheit: Float,
    minTemperatureFahrenheit: Float?,
    temperatureUnit: TemperatureUnit,
    onTemperatureSteps: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val step = temperatureScaleSegmentStep(temperatureUnit, temperatureStepCelsius)
    val tickCount = temperatureScaleIndex(maxTemperature, minTemperature, step).coerceAtLeast(0)
    val selectedIndex = temperatureScaleIndex(temperature, minTemperature, step)
        .coerceIn(0, tickCount)
    val density = LocalDensity.current
    val spacingPx = with(density) { TickSpacing.toPx() }
    var residual by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val haptics = rememberClimateHaptics()
    val latestHaptics by rememberUpdatedState(haptics)
    val latestSteps by rememberUpdatedState(onTemperatureSteps)
    val latestIndex by rememberUpdatedState(selectedIndex)
    val latestTickCount by rememberUpdatedState(tickCount)
    val releaseSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = if (dragging) snap() else motionSpec,
        label = "temperature_scale_index",
    )
    val shownShift by animateFloatAsState(
        targetValue = if (dragging) residual else 0f,
        animationSpec = if (dragging) snap() else releaseSpec,
        label = "temperature_scale_shift",
    )
    val tickColor = MaterialTheme.colorScheme.primary
    val labelStyle = MaterialTheme.typography.labelLarge
    val readoutStyle = MaterialTheme.typography.headlineLarge
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .pointerInput(spacingPx) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        residual = 0f
                        dragging = true
                    },
                    onDragEnd = { dragging = false },
                    onDragCancel = { dragging = false },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val next = residual + dragAmount
                        val atCoolEnd = latestIndex <= 0 && next > 0f
                        val atWarmEnd = latestIndex >= latestTickCount && next < 0f
                        if (atCoolEnd || atWarmEnd) {
                            residual = 0f
                        } else {
                            residual = next
                            val rawSteps = temperatureScaleDragSteps(residual, spacingPx)
                            val steps = rawSteps.coerceIn(-latestIndex, latestTickCount - latestIndex)
                            if (steps != 0) {
                                residual += steps * spacingPx
                                latestHaptics.tick()
                                latestSteps(steps)
                            }
                        }
                    },
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp),
        ) {
            val shift = shownShift
            val center = size.width / 2f
            val halfWidth = (size.width / 2f).coerceAtLeast(1f)
            val labelGap = 2.dp.toPx()
            val maxLen = MaxTickLength.toPx()
            val baseline = 36.dp.toPx() + maxLen
            val stroke = 2.dp.toPx()
            for (index in 0..tickCount) {
                val x = center + (index - animatedIndex) * spacingPx + shift
                if (x < -TickSpacing.toPx() || x > size.width + TickSpacing.toPx()) continue
                val distance = abs(x - center)
                val fade = temperatureScaleEdgeFade(distance, halfWidth)
                if (fade <= 0.02f) continue
                val arc = temperatureScaleTickHeightFraction(distance, halfWidth)
                val celsius = minTemperature + index * step
                val display = celsius.toDisplayTemperature(
                    unit = temperatureUnit,
                    minCelsius = minTemperature,
                    celsiusStep = temperatureStepCelsius,
                    minFahrenheit = minTemperatureFahrenheit,
                    fahrenheitStep = temperatureStepFahrenheit,
                )
                val major = temperatureScaleIsMajorTick(display)
                val tickLen = maxLen * arc
                val tickTop = baseline - tickLen
                val blur = (1f - fade) * 8.dp.toPx()
                if (blur > 0.5f) {
                    val passes = 4
                    for (pass in passes downTo 1) {
                        val spread = blur * pass / passes
                        drawLine(
                            color = tickColor.copy(alpha = fade * 0.16f),
                            start = Offset(x, tickTop),
                            end = Offset(x, baseline),
                            strokeWidth = stroke + spread * 2f,
                            cap = StrokeCap.Round,
                        )
                    }
                }
                drawLine(
                    color = tickColor.copy(alpha = fade),
                    start = Offset(x, tickTop),
                    end = Offset(x, baseline),
                    strokeWidth = stroke + blur * 0.35f,
                    cap = StrokeCap.Round,
                )
                if (major) {
                    val label = display.roundToInt().toString()
                    val layout = textMeasurer.measure(
                        text = label,
                        style = labelStyle.merge(TextStyle(textAlign = TextAlign.Center)),
                    )
                    drawText(
                        textLayoutResult = layout,
                        color = tickColor.copy(alpha = fade),
                        topLeft = Offset(x - layout.size.width / 2f, baseline - maxLen - layout.size.height - labelGap),
                    )
                }
            }
            val apexY = baseline + 4.dp.toPx()
            val baseY = apexY + 8.dp.toPx()
            val half = 6.dp.toPx()
            drawPath(
                path = Path().apply {
                    moveTo(center, apexY)
                    lineTo(center - half, baseY)
                    lineTo(center + half, baseY)
                    close()
                },
                color = tickColor,
            )
        }
        AnimatedTemperatureCounter(
            temperatureLabel = temperatureLabel,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            sortKey = sortKey,
            textStyle = readoutStyle,
        )
    }
}
