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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Gemini-like ambient presence — soft layered orbs instead of a voice waveform.
 * Same visual language for every mood; energy / color morph.
 */
internal data class PresencePose(
    val energy: Float = 0.4f,
    val spread: Float = 0.55f,
    val sparkle: Float = 0.25f,
)

internal fun AssistantMood.toPresencePose(): PresencePose = when (this) {
    AssistantMood.Listening -> PresencePose(energy = 0.9f, spread = 0.75f, sparkle = 0.55f)
    AssistantMood.Speaking -> PresencePose(energy = 0.85f, spread = 0.7f, sparkle = 0.45f)
    AssistantMood.Thinking -> PresencePose(energy = 0.55f, spread = 0.5f, sparkle = 0.7f)
    AssistantMood.Searching -> PresencePose(energy = 0.8f, spread = 0.65f, sparkle = 0.85f)
    AssistantMood.Happy -> PresencePose(energy = 0.7f, spread = 0.7f, sparkle = 0.9f)
    AssistantMood.Sad -> PresencePose(energy = 0.3f, spread = 0.4f, sparkle = 0.15f)
    AssistantMood.Reading -> PresencePose(energy = 0.45f, spread = 0.5f, sparkle = 0.35f)
    AssistantMood.Idle -> PresencePose(energy = 0.35f, spread = 0.48f, sparkle = 0.2f)
}

private val PresenceSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

private val OrbPalette = listOf(
    Color(0xFF8AB4F8),
    Color(0xFFC58AF9),
    Color(0xFF78D9B8),
    Color(0xFFF6A5C0),
)

@Composable
fun AssistantPresence(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
) {
    val target = mood.toPresencePose()
    val energy = remember { Animatable(target.energy) }
    val spread = remember { Animatable(target.spread) }
    val sparkle = remember { Animatable(target.sparkle) }
    val tint by animateColorAsState(
        targetValue = mood.glowColor,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "presence_tint",
    )

    LaunchedEffect(mood) {
        launch { energy.animateTo(target.energy, PresenceSpring) }
        launch { spread.animateTo(target.spread, PresenceSpring) }
        launch { sparkle.animateTo(target.sparkle, PresenceSpring) }
    }

    val infinite = rememberInfiniteTransition(label = "presence")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )
    val breath by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath",
    )

    Canvas(modifier = modifier) {
        drawAmbientPresence(
            phase = phase,
            breath = breath,
            energy = energy.value,
            spread = spread.value,
            sparkle = sparkle.value,
            tint = tint,
        )
    }
}

private fun DrawScope.drawAmbientPresence(
    phase: Float,
    breath: Float,
    energy: Float,
    spread: Float,
    sparkle: Float,
    tint: Color,
) {
    if (size.minDimension <= 0f) return
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val base = size.minDimension * 0.42f * spread * breath

    // Soft wash
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                tint.copy(alpha = 0.22f * energy),
                Color.Transparent,
            ),
            center = Offset(cx, cy),
            radius = base * 1.6f,
        ),
        radius = base * 1.6f,
        center = Offset(cx, cy),
    )

    // Layered drifting orbs (Gemini-like nebula)
    OrbPalette.forEachIndexed { i, color ->
        val ang = phase * (0.55f + i * 0.12f) + i * 1.4f
        val orbit = base * (0.35f + i * 0.08f)
        val ox = cx + cos(ang) * orbit * 0.55f
        val oy = cy + sin(ang * 0.9f) * orbit * 0.4f
        val r = base * (0.55f - i * 0.07f) * (0.75f + 0.25f * energy)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.35f * energy),
                    color.copy(alpha = 0.08f * energy),
                    Color.Transparent,
                ),
                center = Offset(ox, oy),
                radius = r,
            ),
            radius = r,
            center = Offset(ox, oy),
        )
    }

    // Core glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.28f * energy),
                tint.copy(alpha = 0.18f * energy),
                Color.Transparent,
            ),
            center = Offset(cx, cy),
            radius = base * 0.55f,
        ),
        radius = base * 0.55f,
        center = Offset(cx, cy),
    )

    // Soft sparkles for thinking / searching / happy
    if (sparkle > 0.05f) {
        val count = 8
        for (i in 0 until count) {
            val a = phase * 1.3f + i * (2f * PI.toFloat() / count)
            val dist = base * (0.7f + 0.25f * sin(phase + i).toFloat())
            val px = cx + cos(a) * dist
            val py = cy + sin(a * 1.1f) * dist * 0.75f
            val twinkle = 0.35f + 0.65f * ((sin(phase * 2f + i) + 1f) * 0.5f).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = 0.55f * sparkle * twinkle),
                radius = 2.2f * twinkle,
                center = Offset(px, py),
            )
        }
    }
}
