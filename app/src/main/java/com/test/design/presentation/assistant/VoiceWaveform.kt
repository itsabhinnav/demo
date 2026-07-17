package com.test.design.presentation.assistant

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Wave layer weights — same visual system for every mood; amplitudes morph.
 */
internal data class WavePose(
    val ringAmount: Float = 0.15f,
    val ribbonAmount: Float = 0.35f,
    val barAmount: Float = 0f,
    val haloAmount: Float = 0.2f,
    val energy: Float = 0.35f,
)

internal fun AssistantMood.toWavePose(): WavePose = when (this) {
    AssistantMood.Listening -> WavePose(
        ringAmount = 1f,
        ribbonAmount = 0.85f,
        barAmount = 0.15f,
        haloAmount = 0.45f,
        energy = 0.9f,
    )
    AssistantMood.Thinking -> WavePose(
        ringAmount = 0.2f,
        ribbonAmount = 1f,
        barAmount = 0.05f,
        haloAmount = 0.7f,
        energy = 0.55f,
    )
    AssistantMood.Speaking -> WavePose(
        ringAmount = 0.25f,
        ribbonAmount = 0.35f,
        barAmount = 1f,
        haloAmount = 0.35f,
        energy = 0.85f,
    )
    AssistantMood.Searching -> WavePose(
        ringAmount = 0.75f,
        ribbonAmount = 0.7f,
        barAmount = 0.2f,
        haloAmount = 0.4f,
        energy = 0.8f,
    )
    AssistantMood.Happy -> WavePose(
        ringAmount = 0.35f,
        ribbonAmount = 0.45f,
        barAmount = 0.1f,
        haloAmount = 0.5f,
        energy = 0.65f,
    )
    AssistantMood.Sad -> WavePose(
        ringAmount = 0.12f,
        ribbonAmount = 0.25f,
        barAmount = 0f,
        haloAmount = 0.25f,
        energy = 0.3f,
    )
    AssistantMood.Reading -> WavePose(
        ringAmount = 0.2f,
        ribbonAmount = 0.4f,
        barAmount = 0.08f,
        haloAmount = 0.3f,
        energy = 0.45f,
    )
    AssistantMood.Idle -> WavePose(
        ringAmount = 0.18f,
        ribbonAmount = 0.32f,
        barAmount = 0f,
        haloAmount = 0.22f,
        energy = 0.35f,
    )
}

private val WaveSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

/**
 * Unified voice waves — listening / working / speaking share one layer that morphs.
 */
@Composable
fun VoiceWaveform(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    color: Color = mood.glowColor,
) {
    val target = mood.toWavePose()
    val ringAmount = remember { Animatable(target.ringAmount) }
    val ribbonAmount = remember { Animatable(target.ribbonAmount) }
    val barAmount = remember { Animatable(target.barAmount) }
    val haloAmount = remember { Animatable(target.haloAmount) }
    val energy = remember { Animatable(target.energy) }

    val waveColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "wave_color",
    )

    LaunchedEffect(mood) {
        launch { ringAmount.animateTo(target.ringAmount, WaveSpring) }
        launch { ribbonAmount.animateTo(target.ribbonAmount, WaveSpring) }
        launch { barAmount.animateTo(target.barAmount, WaveSpring) }
        launch { haloAmount.animateTo(target.haloAmount, WaveSpring) }
        launch { energy.animateTo(target.energy, WaveSpring) }
    }

    val infinite = rememberInfiniteTransition(label = "voice_wave")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_phase",
    )
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_pulse",
    )

    Canvas(modifier = modifier) {
        drawUnifiedWaves(
            color = waveColor,
            phase = phase,
            pulse = pulse,
            ringAmount = ringAmount.value,
            ribbonAmount = ribbonAmount.value,
            barAmount = barAmount.value,
            haloAmount = haloAmount.value,
            energy = energy.value,
        )
    }
}

private fun DrawScope.drawUnifiedWaves(
    color: Color,
    phase: Float,
    pulse: Float,
    ringAmount: Float,
    ribbonAmount: Float,
    barAmount: Float,
    haloAmount: Float,
    energy: Float,
) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.48f
    val maxR = minOf(size.width, size.height) * 0.48f

    if (haloAmount > 0.02f) {
        val breath = 0.88f + 0.12f * sin(phase).toFloat()
        drawCircle(
            color = color.copy(alpha = 0.16f * haloAmount * energy),
            radius = maxR * 0.72f * breath,
            center = Offset(cx, cy),
        )
    }

    if (ringAmount > 0.02f) {
        for (i in 0..3) {
            val t = ((pulse + i * 0.22f) % 1f)
            val r = maxR * (0.28f + t * 0.72f)
            val alpha = (1f - t) * 0.42f * ringAmount * energy
            if (alpha < 0.01f) continue
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = (3.5f - t * 1.5f) * (0.6f + 0.4f * ringAmount)),
            )
        }
    }

    if (ribbonAmount > 0.02f) {
        val ampBase = size.height * 0.055f * ribbonAmount * (0.65f + 0.35f * energy)
        drawSineRibbon(
            color = color.copy(alpha = 0.5f * ribbonAmount),
            y = size.height * 0.78f,
            amplitude = ampBase,
            wavelength = size.width * 0.3f,
            phase = phase,
            stroke = 3.2f,
        )
        drawSineRibbon(
            color = color.copy(alpha = 0.28f * ribbonAmount),
            y = size.height * 0.84f,
            amplitude = ampBase * 0.65f,
            wavelength = size.width * 0.36f,
            phase = -phase * 1.15f,
            stroke = 2.4f,
        )
    }

    if (barAmount > 0.02f) {
        val barCount = 28
        val gap = size.width * 0.012f
        val totalGap = gap * (barCount - 1)
        val barW = ((size.width * 0.72f) - totalGap) / barCount
        val startX = size.width * 0.14f
        val midY = size.height * 0.78f
        val maxH = size.height * 0.2f * barAmount

        for (i in 0 until barCount) {
            val n = i / barCount.toFloat()
            val envelope = sin(n * PI).toFloat().coerceAtLeast(0.15f)
            val wobble = abs(sin(phase * 1.6f + i * 0.55f)).toFloat()
            val h = maxH * envelope * (0.22f + 0.78f * wobble) * energy
            val x = startX + i * (barW + gap)
            drawLine(
                color = color.copy(alpha = (0.3f + 0.5f * wobble) * barAmount),
                start = Offset(x + barW * 0.5f, midY - h),
                end = Offset(x + barW * 0.5f, midY + h * 0.5f),
                strokeWidth = barW.coerceAtMost(8f),
                cap = StrokeCap.Round,
            )
        }
    }
}

private fun DrawScope.drawSineRibbon(
    color: Color,
    y: Float,
    amplitude: Float,
    wavelength: Float,
    phase: Float,
    stroke: Float,
) {
    if (size.width <= 0f || amplitude < 0.5f) return
    val path = Path()
    val steps = 64
    for (i in 0..steps) {
        val x = size.width * i / steps.toFloat()
        val yy = y + amplitude * sin((x / wavelength) * 2f * PI + phase).toFloat()
        if (i == 0) path.moveTo(x, yy) else path.lineTo(x, yy)
    }
    drawPath(path, color, style = Stroke(width = stroke, cap = StrokeCap.Round))
}
