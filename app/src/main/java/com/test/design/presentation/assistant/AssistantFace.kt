package com.test.design.presentation.assistant

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Continuous eye pose for the squircle character — same eyes, morphing openness.
 */
internal data class FacePose(
    val eyeOpen: Float = 1f,
    val eyeWidth: Float = 1f,
    val eyeHeight: Float = 1f,
    val eyeGap: Float = 1f,
    val tilt: Float = 6f,
    val borderGlow: Float = 0.85f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
)

internal fun AssistantMood.toFacePose(): FacePose = when (this) {
    AssistantMood.Idle -> FacePose(eyeOpen = 1f, tilt = 5f, borderGlow = 0.7f)
    AssistantMood.Listening -> FacePose(
        eyeOpen = 1.12f,
        eyeWidth = 1.08f,
        eyeHeight = 1.05f,
        tilt = 4f,
        borderGlow = 1f,
    )
    AssistantMood.Speaking -> FacePose(
        eyeOpen = 0.95f,
        eyeHeight = 0.92f,
        tilt = 7f,
        borderGlow = 0.9f,
    )
    AssistantMood.Thinking -> FacePose(
        eyeOpen = 0.82f,
        eyeHeight = 0.78f,
        lookX = 0.2f,
        lookY = -0.25f,
        tilt = 8f,
        borderGlow = 0.8f,
    )
    AssistantMood.Happy -> FacePose(
        eyeOpen = 0.55f,
        eyeWidth = 1.12f,
        eyeHeight = 0.55f,
        tilt = 3f,
        borderGlow = 0.95f,
    )
    AssistantMood.Sad -> FacePose(
        eyeOpen = 0.7f,
        eyeHeight = 0.65f,
        lookY = 0.2f,
        tilt = 10f,
        borderGlow = 0.55f,
    )
    AssistantMood.Reading -> FacePose(
        eyeOpen = 0.9f,
        lookX = 0.35f,
        tilt = 5f,
        borderGlow = 0.75f,
    )
    AssistantMood.Searching -> FacePose(
        eyeOpen = 1.05f,
        eyeWidth = 1.05f,
        tilt = 6f,
        borderGlow = 0.95f,
    )
}

private val PoseSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

private val ShellFill = Color(0xFF0A0C10)
private val ShellBorder = Color(0xFFF2F5FF)
private val EyeWhite = Color(0xFFF7F9FF)
private val AccentWarm = Color(0xFFE85A6B)
private val AccentCool = Color(0xFFB8D4FF)

/**
 * Squircle character shell with animated rectangular scanline eyes.
 */
@Composable
fun AssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    faceColor: Color = EyeWhite,
) {
    val target = mood.toFacePose()
    val eyeOpen = remember { Animatable(target.eyeOpen) }
    val eyeWidth = remember { Animatable(target.eyeWidth) }
    val eyeHeight = remember { Animatable(target.eyeHeight) }
    val eyeGap = remember { Animatable(target.eyeGap) }
    val tilt = remember { Animatable(target.tilt) }
    val borderGlow = remember { Animatable(target.borderGlow) }
    val lookX = remember { Animatable(target.lookX) }
    val lookY = remember { Animatable(target.lookY) }
    val blink = remember { Animatable(1f) }

    LaunchedEffect(mood) {
        launch { eyeOpen.animateTo(target.eyeOpen, PoseSpring) }
        launch { eyeWidth.animateTo(target.eyeWidth, PoseSpring) }
        launch { eyeHeight.animateTo(target.eyeHeight, PoseSpring) }
        launch { eyeGap.animateTo(target.eyeGap, PoseSpring) }
        launch { tilt.animateTo(target.tilt, PoseSpring) }
        launch { borderGlow.animateTo(target.borderGlow, PoseSpring) }
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) {
            launch { lookX.animateTo(target.lookX, PoseSpring) }
        }
        launch { lookY.animateTo(target.lookY, PoseSpring) }
    }

    val infinite = rememberInfiniteTransition(label = "squircle_face")
    val life by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "life",
    )
    val scan by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan",
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "border_pulse",
    )

    LaunchedEffect(mood) {
        while (isActive) {
            val wait = when (mood) {
                AssistantMood.Listening -> Random.nextLong(1800, 3200)
                AssistantMood.Searching -> Random.nextLong(900, 1600)
                AssistantMood.Happy -> Random.nextLong(2200, 3800)
                AssistantMood.Sad -> Random.nextLong(2800, 4800)
                else -> Random.nextLong(2000, 4000)
            }
            delay(wait)
            val closeTo = when (mood) {
                AssistantMood.Happy -> 0.35f
                else -> 0.08f
            }
            blink.animateTo(closeTo, tween(70))
            delay(40)
            blink.animateTo(1f, tween(120, easing = FastOutSlowInEasing))
            // Occasional double-blink for life
            if (Random.nextFloat() < 0.28f) {
                delay(90)
                blink.animateTo(closeTo, tween(60))
                delay(30)
                blink.animateTo(1f, tween(100))
            }
        }
    }

    LaunchedEffect(mood) {
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) return@LaunchedEffect
        while (isActive) {
            if (mood == AssistantMood.Reading) {
                lookX.animateTo(0.45f, tween(850, easing = FastOutSlowInEasing))
                delay(140)
                lookX.animateTo(-0.4f, tween(80))
                delay(90)
            } else {
                lookX.animateTo(0.5f, tween(180))
                lookX.animateTo(-0.45f, tween(220))
                lookX.animateTo(0.1f, tween(160))
                delay(70)
            }
        }
    }

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val side = minOf(size.width, size.height)
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val shell = side * 0.78f
        val corner = shell * 0.28f
        val border = shell * 0.055f

        val liveTilt = tilt.value + 1.2f * sin(life * 0.5f).toFloat()
        rotate(liveTilt, pivot = Offset(cx, cy)) {
            val left = cx - shell * 0.5f
            val top = cy - shell * 0.5f
            val shellRect = RoundRect(
                left = left,
                top = top,
                right = left + shell,
                bottom = top + shell,
                radiusX = corner,
                radiusY = corner,
            )
            val shellPath = Path().apply { addRoundRect(shellRect) }

            // Outer glow (cool + warm chromatic rim)
            val glowR = shell * 0.62f * pulse * borderGlow.value
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentCool.copy(alpha = 0.18f * borderGlow.value),
                        AccentWarm.copy(alpha = 0.12f * borderGlow.value),
                        Color.Transparent,
                    ),
                    center = Offset(cx, cy),
                    radius = glowR,
                ),
                radius = glowR,
                center = Offset(cx, cy),
            )

            // Shell fill
            drawPath(shellPath, ShellFill)

            // Thick luminous border
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ShellBorder,
                        AccentCool,
                        ShellBorder,
                        AccentWarm.copy(alpha = 0.85f),
                        ShellBorder,
                    ),
                    start = Offset(left, top + shell),
                    end = Offset(left + shell, top),
                ),
                topLeft = Offset(left, top),
                size = Size(shell, shell),
                cornerRadius = CornerRadius(corner, corner),
                style = Stroke(width = border * (0.95f + 0.08f * borderGlow.value * pulse)),
            )

            // Inner warm specular on upper-right wall
            clipPath(shellPath) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentWarm.copy(alpha = 0.22f),
                            Color.Transparent,
                        ),
                        center = Offset(cx + shell * 0.22f, cy - shell * 0.18f),
                        radius = shell * 0.35f,
                    ),
                    radius = shell * 0.35f,
                    center = Offset(cx + shell * 0.22f, cy - shell * 0.18f),
                )
            }

            // Eyes
            val open = (eyeOpen.value * blink.value).coerceIn(0.06f, 1.25f)
            val eW = shell * 0.13f * eyeWidth.value
            val eH = shell * 0.18f * eyeHeight.value * open
            val gap = shell * 0.13f * eyeGap.value
            val eyeY = cy + lookY.value * shell * 0.08f
            val gaze = lookX.value * shell * 0.04f +
                if (mood == AssistantMood.Thinking) 0.02f * shell * sin(life) else 0f

            val leftEye = Offset(cx - gap - eW * 0.5f + gaze, eyeY)
            val rightEye = Offset(cx + gap + eW * 0.5f + gaze, eyeY)

            // Soft eye glow
            drawCircle(
                color = faceColor.copy(alpha = 0.2f * open),
                radius = eW * 1.4f,
                center = leftEye,
            )
            drawCircle(
                color = faceColor.copy(alpha = 0.2f * open),
                radius = eW * 1.4f,
                center = rightEye,
            )

            drawScanlineEye(
                center = leftEye,
                width = eW,
                height = eH,
                color = faceColor,
                scanPhase = scan,
            )
            drawScanlineEye(
                center = rightEye,
                width = eW,
                height = eH,
                color = faceColor,
                scanPhase = scan + 0.15f,
            )
        }
    }
}

private fun DrawScope.drawScanlineEye(
    center: Offset,
    width: Float,
    height: Float,
    color: Color,
    scanPhase: Float,
) {
    val left = center.x - width
    val top = center.y - height
    val eyeW = width * 2f
    val eyeH = height * 2f
    val radius = CornerRadius(width * 0.35f, height * 0.35f)
    val rect = RoundRect(
        left = left,
        top = top,
        right = left + eyeW,
        bottom = top + eyeH,
        cornerRadius = radius,
    )
    val path = Path().apply { addRoundRect(rect) }

    clipPath(path) {
        drawRoundRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(eyeW, eyeH),
            cornerRadius = radius,
        )
        // Horizontal scanline / pixel grid texture
        val lineGap = (eyeH / 9f).coerceAtLeast(1.5f)
        var y = top + lineGap * 0.5f
        var i = 0
        while (y < top + eyeH) {
            val shimmer = 0.08f + 0.1f * ((sin((scanPhase + i * 0.12f) * PI * 2).toFloat() + 1f) * 0.5f)
            drawLine(
                color = Color(0xFF0A0C10).copy(alpha = shimmer),
                start = Offset(left, y),
                end = Offset(left + eyeW, y),
                strokeWidth = lineGap * 0.35f,
            )
            y += lineGap
            i++
        }
        // Soft vertical pixel columns
        val colGap = (eyeW / 6f).coerceAtLeast(2f)
        var x = left + colGap
        while (x < left + eyeW) {
            drawLine(
                color = Color(0xFF0A0C10).copy(alpha = 0.06f),
                start = Offset(x, top),
                end = Offset(x, top + eyeH),
                strokeWidth = 1f,
            )
            x += colGap
        }
    }
}
