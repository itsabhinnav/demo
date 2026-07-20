package com.test.design.presentation.assistant

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Sparse rain / snow behind the immersive face — never over transcript.
 * Caps at ~20 particles, low alpha, brand-tinted whites/blues.
 */
@Composable
fun WeatherAmbientOverlay(
    kind: WeatherAmbientKind?,
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFB3E5FC),
) {
    val alpha = remember { Animatable(0f) }
    var lastKind by remember { mutableStateOf(WeatherAmbientKind.Snow) }
    LaunchedEffect(kind) {
        if (kind != null) {
            lastKind = kind
            alpha.animateTo(1f, tween(520))
        } else {
            alpha.animateTo(0f, tween(640))
        }
    }
    if (kind == null && alpha.value < 0.02f) return

    val particleCount = 18
    val particles = remember(lastKind, particleCount) {
        List(particleCount) { index ->
            AmbientParticle(
                xFraction = Random(index * 19 + 7).nextFloat(),
                yFraction = Random(index * 41 + 3).nextFloat(),
                size = 2.2f + Random(index * 53).nextFloat() * 3.8f,
                drift = 6f + Random(index * 67).nextFloat() * 14f,
                speed = 0.55f + Random(index * 79).nextFloat() * 0.7f,
                phase = Random(index * 97 + particleCount).nextFloat(),
                particleAlpha = 0.08f + Random(index * 113).nextFloat() * 0.07f,
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "weather_ambient")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (lastKind == WeatherAmbientKind.Rain) 4_200 else 14_000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "weather_progress",
    )

    val activeKind = kind ?: lastKind
    Canvas(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.value.coerceIn(0f, 1f)
        },
    ) {
        val w = size.width
        val h = size.height
        val layer = alpha.value
        particles.forEach { p ->
            val local = ((progress * p.speed) + p.phase) % 1f
            when (activeKind) {
                WeatherAmbientKind.Rain -> {
                    val x = (p.xFraction * w + sin(local * Math.PI.toFloat() * 2f) * p.drift * 0.25f)
                        .mod(w + 20f) - 10f
                    val y = ((p.yFraction + local) % 1f) * (h + 40f) - 20f
                    val len = 10f + p.size * 2.4f
                    drawLine(
                        color = tint.copy(alpha = p.particleAlpha * layer),
                        start = Offset(x, y),
                        end = Offset(x - 1.2f, y + len),
                        strokeWidth = 1.1f,
                        cap = StrokeCap.Round,
                    )
                }
                WeatherAmbientKind.Snow -> {
                    val x = (p.xFraction * w + sin(local * Math.PI.toFloat() * 2f) * p.drift)
                        .mod(w + 24f) - 12f
                    val y = ((p.yFraction + local) % 1f) * (h + 28f) - 14f
                    val r = p.size
                    // Soft hex flake — two thin strokes.
                    drawCircle(
                        color = tint.copy(alpha = p.particleAlpha * layer * 0.85f),
                        radius = r * 0.35f,
                        center = Offset(x, y),
                    )
                    val arm = r * 0.9f
                    for (i in 0 until 3) {
                        val angle = i * 60f * (Math.PI.toFloat() / 180f) + local * 0.4f
                        val dx = cos(angle) * arm
                        val dy = sin(angle) * arm
                        drawLine(
                            color = tint.copy(alpha = p.particleAlpha * layer),
                            start = Offset(x - dx, y - dy),
                            end = Offset(x + dx, y + dy),
                            strokeWidth = 1f,
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }
        }
    }
}

private data class AmbientParticle(
    val xFraction: Float,
    val yFraction: Float,
    val size: Float,
    val drift: Float,
    val speed: Float,
    val phase: Float,
    val particleAlpha: Float,
)

private fun Float.mod(m: Float): Float {
    val r = this % m
    return if (r < 0f) r + m else r
}
