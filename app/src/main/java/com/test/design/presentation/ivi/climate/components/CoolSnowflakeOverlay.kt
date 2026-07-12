package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Soft drifting snowflakes for cool cabin temperatures.
 * Intensity follows [coolIntensity] in 0..1 (higher = cooler).
 */
@Composable
fun CoolSnowflakeOverlay(
    coolIntensity: Float,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    flakeCount: Int = 10,
    sizeScale: Float = 1f,
) {
    val intensity = coolIntensity.coerceIn(0f, 1f)
    if (intensity <= 0.02f) return

    val count = flakeCount.coerceIn(1, 48)
    val flakes = remember(count, sizeScale) {
        List(count) { index ->
            Snowflake(
                xFraction = Random(index * 17 + count).nextFloat(),
                yFraction = Random(index * 31 + count).nextFloat(),
                size = (3.5f + Random(index * 47).nextFloat() * 5.5f) * sizeScale,
                drift = 8f + Random(index * 59).nextFloat() * 18f,
                spin = 10f + Random(index * 71).nextFloat() * 25f,
                phase = Random(index * 83 + count).nextFloat(),
                alpha = 0.10f + Random(index * 97).nextFloat() * 0.18f,
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "cool_snow")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "snow_progress",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        flakes.forEach { flake ->
            val localProgress = (progress + flake.phase) % 1f
            val x = (flake.xFraction * w + sin(localProgress * Math.PI.toFloat() * 2f) * flake.drift)
                .mod(w + 24f) - 12f
            val y = ((flake.yFraction + localProgress) % 1f) * (h + 28f) - 14f
            val alpha = flake.alpha * intensity
            rotate(degrees = localProgress * flake.spin * 12f, pivot = Offset(x, y)) {
                drawSnowflake(
                    center = Offset(x, y),
                    radius = flake.size,
                    color = tint.copy(alpha = alpha),
                )
            }
        }
    }
}

private data class Snowflake(
    val xFraction: Float,
    val yFraction: Float,
    val size: Float,
    val drift: Float,
    val spin: Float,
    val phase: Float,
    val alpha: Float,
)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSnowflake(
    center: Offset,
    radius: Float,
    color: Color,
) {
    val stroke = Stroke(width = (radius * 0.22f).coerceAtLeast(1f))
    repeat(3) { arm ->
        val angle = arm * 60f * (Math.PI.toFloat() / 180f)
        val dx = cos(angle) * radius
        val dy = sin(angle) * radius
        drawLine(
            color = color,
            start = Offset(center.x - dx, center.y - dy),
            end = Offset(center.x + dx, center.y + dy),
            strokeWidth = stroke.width,
        )
        val branch = radius * 0.38f
        listOf(-1f, 1f).forEach { side ->
            val bx = cos(angle) * radius * 0.45f * side
            val by = sin(angle) * radius * 0.45f * side
            val perp = angle + Math.PI.toFloat() / 2f
            drawLine(
                color = color,
                start = Offset(center.x + bx, center.y + by),
                end = Offset(
                    center.x + bx + cos(perp) * branch * 0.55f,
                    center.y + by + sin(perp) * branch * 0.55f,
                ),
                strokeWidth = stroke.width * 0.85f,
            )
            drawLine(
                color = color,
                start = Offset(center.x + bx, center.y + by),
                end = Offset(
                    center.x + bx - cos(perp) * branch * 0.55f,
                    center.y + by - sin(perp) * branch * 0.55f,
                ),
                strokeWidth = stroke.width * 0.85f,
            )
        }
    }
}
