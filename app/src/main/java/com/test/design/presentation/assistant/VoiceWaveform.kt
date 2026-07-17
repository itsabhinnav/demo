package com.test.design.presentation.assistant

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Voice-reactive wave visuals for listening / working / speaking states.
 * Other moods render a soft idle ripple so the plate never feels static.
 */
@Composable
fun VoiceWaveform(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    color: Color = mood.glowColor,
) {
    val infinite = rememberInfiniteTransition(label = "voice_wave")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(waveCycleMs(mood), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_phase",
    )
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(wavePulseMs(mood), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_pulse",
    )

    Canvas(modifier = modifier) {
        when (mood) {
            AssistantMood.Listening -> drawListeningWaves(color, phase, pulse)
            AssistantMood.Thinking -> drawWorkingWaves(color, phase, pulse)
            AssistantMood.Speaking -> drawSpeakingBars(color, phase)
            AssistantMood.Searching -> drawListeningWaves(color, phase, pulse)
            else -> drawIdleRipple(color, phase, pulse)
        }
    }
}

private fun waveCycleMs(mood: AssistantMood): Int = when (mood) {
    AssistantMood.Listening -> 1400
    AssistantMood.Speaking -> 900
    AssistantMood.Thinking -> 2200
    AssistantMood.Searching -> 1100
    else -> 2800
}

private fun wavePulseMs(mood: AssistantMood): Int = when (mood) {
    AssistantMood.Listening -> 1600
    AssistantMood.Speaking -> 1000
    AssistantMood.Thinking -> 2400
    else -> 3000
}

private fun DrawScope.drawListeningWaves(color: Color, phase: Float, pulse: Float) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.48f
    val maxR = minOf(size.width, size.height) * 0.48f
    // Expanding rings
    for (i in 0..3) {
        val t = ((pulse + i * 0.22f) % 1f)
        val r = maxR * (0.28f + t * 0.72f)
        val alpha = (1f - t) * 0.45f
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = 3.5f - t * 1.5f),
        )
    }
    // Side sine ribbons
    drawSineRibbon(
        color = color.copy(alpha = 0.55f),
        y = size.height * 0.78f,
        amplitude = size.height * 0.07f,
        wavelength = size.width * 0.28f,
        phase = phase,
        stroke = 3.5f,
    )
    drawSineRibbon(
        color = color.copy(alpha = 0.28f),
        y = size.height * 0.84f,
        amplitude = size.height * 0.045f,
        wavelength = size.width * 0.34f,
        phase = -phase * 1.2f,
        stroke = 2.5f,
    )
}

private fun DrawScope.drawWorkingWaves(color: Color, phase: Float, pulse: Float) {
    val baseY = size.height * 0.72f
    for (i in 0..2) {
        val amp = size.height * (0.035f + i * 0.018f) * (0.7f + 0.3f * sin(pulse * PI * 2 + i).toFloat())
        drawSineRibbon(
            color = color.copy(alpha = 0.5f - i * 0.12f),
            y = baseY + i * size.height * 0.06f,
            amplitude = amp,
            wavelength = size.width * (0.4f + i * 0.08f),
            phase = phase * (0.7f + i * 0.2f) + i,
            stroke = 3f - i * 0.4f,
        )
    }
    // Soft breathing halo
    val cx = size.width * 0.5f
    val cy = size.height * 0.42f
    val breath = 0.85f + 0.15f * sin(phase).toFloat()
    drawCircle(
        color = color.copy(alpha = 0.18f),
        radius = minOf(size.width, size.height) * 0.32f * breath,
        center = Offset(cx, cy),
    )
}

private fun DrawScope.drawSpeakingBars(color: Color, phase: Float) {
    val barCount = 28
    val gap = size.width * 0.012f
    val totalGap = gap * (barCount - 1)
    val barW = ((size.width * 0.72f) - totalGap) / barCount
    val startX = size.width * 0.14f
    val midY = size.height * 0.78f
    val maxH = size.height * 0.22f

    for (i in 0 until barCount) {
        val n = i / barCount.toFloat()
        val envelope = sin(n * PI).toFloat().coerceAtLeast(0.15f)
        val wobble = abs(sin(phase * 1.6f + i * 0.55f)).toFloat()
        val h = maxH * envelope * (0.25f + 0.75f * wobble)
        val x = startX + i * (barW + gap)
        drawLine(
            color = color.copy(alpha = 0.35f + 0.5f * wobble),
            start = Offset(x + barW * 0.5f, midY - h),
            end = Offset(x + barW * 0.5f, midY + h * 0.55f),
            strokeWidth = barW.coerceAtMost(8f),
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawIdleRipple(color: Color, phase: Float, pulse: Float) {
    drawSineRibbon(
        color = color.copy(alpha = 0.22f),
        y = size.height * 0.8f,
        amplitude = size.height * 0.03f,
        wavelength = size.width * 0.45f,
        phase = phase * 0.6f,
        stroke = 2.5f,
    )
    val t = pulse
    val r = minOf(size.width, size.height) * (0.3f + t * 0.2f)
    drawCircle(
        color = color.copy(alpha = (1f - t) * 0.12f),
        radius = r,
        center = Offset(size.width * 0.5f, size.height * 0.45f),
        style = Stroke(width = 2f),
    )
}

private fun DrawScope.drawSineRibbon(
    color: Color,
    y: Float,
    amplitude: Float,
    wavelength: Float,
    phase: Float,
    stroke: Float,
) {
    if (size.width <= 0f) return
    val path = Path()
    val steps = 64
    for (i in 0..steps) {
        val x = size.width * i / steps.toFloat()
        val yy = y + amplitude * sin((x / wavelength) * 2f * PI + phase).toFloat()
        if (i == 0) path.moveTo(x, yy) else path.lineTo(x, yy)
    }
    drawPath(path, color, style = Stroke(width = stroke, cap = StrokeCap.Round))
}
