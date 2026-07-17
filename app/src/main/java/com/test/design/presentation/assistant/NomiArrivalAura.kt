package com.test.design.presentation.assistant

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Arrival “wow” — expanding rings + sparkle burst behind the NOMI body.
 */
@Composable
fun NomiArrivalAura(
    mood: AssistantMood,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val ring = remember { Animatable(0f) }
    val burst = remember { Animatable(0f) }

    LaunchedEffect(active) {
        if (!active) {
            ring.snapTo(0f)
            burst.snapTo(0f)
            return@LaunchedEffect
        }
        ring.snapTo(0f)
        burst.snapTo(0f)
        launch {
            ring.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }
        launch {
            burst.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        }
    }

    val infinite = rememberInfiniteTransition(label = "aura")
    val spin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "spin",
    )

    Canvas(modifier = modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val base = size.minDimension * 0.28f
        val tint = mood.glowColor

        // Soft floor contact shadow
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.35f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy + base * 1.15f),
                radius = base * 1.1f,
            ),
            topLeft = Offset(cx - base * 1.05f, cy + base * 0.85f),
            size = androidx.compose.ui.geometry.Size(base * 2.1f, base * 0.55f),
        )

        // Expanding arrival rings
        val t = ring.value
        if (t > 0.01f) {
            for (i in 0..2) {
                val p = ((t + i * 0.18f) % 1.05f).coerceIn(0f, 1f)
                val rr = base * (1.1f + p * 1.8f)
                drawCircle(
                    color = tint.copy(alpha = (1f - p) * 0.45f),
                    radius = rr,
                    center = Offset(cx, cy),
                    style = Stroke(width = base * 0.045f * (1f - p * 0.5f), cap = StrokeCap.Round),
                )
            }
        }

        // Ambient halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    tint.copy(alpha = 0.28f),
                    tint.copy(alpha = 0.08f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = base * 2.2f,
            ),
            radius = base * 2.2f,
            center = Offset(cx, cy),
        )

        // Sparkle burst
        val b = burst.value
        if (b > 0.02f) {
            for (i in 0 until 12) {
                val a = spin * 0.35f + i * (2f * PI.toFloat() / 12f)
                val dist = base * (0.9f + 1.4f * b) * (0.7f + 0.3f * ((i % 3) / 2f))
                val px = cx + cos(a) * dist
                val py = cy + sin(a) * dist * 0.85f
                val twinkle = (1f - b) * (0.45f + 0.55f * ((sin(spin * 2f + i) + 1f) * 0.5f).toFloat())
                val s = base * 0.04f * (0.6f + twinkle)
                drawLine(
                    Color.White.copy(alpha = 0.75f * twinkle),
                    Offset(px, py - s),
                    Offset(px, py + s),
                    strokeWidth = s * 0.35f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    Color.White.copy(alpha = 0.75f * twinkle),
                    Offset(px - s, py),
                    Offset(px + s, py),
                    strokeWidth = s * 0.35f,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
