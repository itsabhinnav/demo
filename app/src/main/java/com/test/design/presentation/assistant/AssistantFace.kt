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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
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
    val tilt: Float = 4f,
    val borderGlow: Float = 0.85f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val blush: Float = 0.2f,
    val roundness: Float = 0.55f,
)

internal fun AssistantMood.toFacePose(): FacePose = when (this) {
    AssistantMood.Idle -> FacePose(
        eyeOpen = 1f,
        tilt = 3.5f,
        borderGlow = 0.72f,
        blush = 0.25f,
        roundness = 0.58f,
    )
    AssistantMood.Listening -> FacePose(
        eyeOpen = 1.1f,
        eyeWidth = 1.06f,
        eyeHeight = 1.04f,
        tilt = 2.5f,
        borderGlow = 1f,
        blush = 0.45f,
        roundness = 0.6f,
    )
    AssistantMood.Speaking -> FacePose(
        eyeOpen = 0.96f,
        eyeHeight = 0.94f,
        tilt = 5f,
        borderGlow = 0.9f,
        blush = 0.35f,
        roundness = 0.55f,
    )
    AssistantMood.Thinking -> FacePose(
        eyeOpen = 0.86f,
        eyeHeight = 0.82f,
        lookX = 0.22f,
        lookY = -0.22f,
        tilt = 6f,
        borderGlow = 0.8f,
        blush = 0.15f,
        roundness = 0.52f,
    )
    AssistantMood.Happy -> FacePose(
        eyeOpen = 0.58f,
        eyeWidth = 1.14f,
        eyeHeight = 0.52f,
        tilt = 2f,
        borderGlow = 0.95f,
        blush = 0.7f,
        roundness = 0.72f,
    )
    AssistantMood.Sad -> FacePose(
        eyeOpen = 0.72f,
        eyeHeight = 0.68f,
        lookY = 0.18f,
        tilt = 7f,
        borderGlow = 0.55f,
        blush = 0.08f,
        roundness = 0.5f,
    )
    AssistantMood.Reading -> FacePose(
        eyeOpen = 0.92f,
        lookX = 0.3f,
        tilt = 3.5f,
        borderGlow = 0.75f,
        blush = 0.2f,
        roundness = 0.55f,
    )
    AssistantMood.Searching -> FacePose(
        eyeOpen = 1.06f,
        eyeWidth = 1.04f,
        tilt = 4f,
        borderGlow = 0.95f,
        blush = 0.3f,
        roundness = 0.58f,
    )
}

private val PoseSpring = spring<Float>(
    dampingRatio = 0.72f,
    stiffness = Spring.StiffnessMediumLow,
)

private val ShellFill = Color(0xFF0A0C10)
private val ShellBorder = Color(0xFFF2F5FF)
private val EyeWhite = Color(0xFFF7F9FF)
private val AccentWarm = Color(0xFFE85A6B)
private val AccentCool = Color(0xFFB8D4FF)

/**
 * Cute squircle persona — soft eyes, blush, floating mood props.
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
    val blush = remember { Animatable(target.blush) }
    val roundness = remember { Animatable(target.roundness) }
    val propVisibility = remember { Animatable(0f) }
    val blink = remember { Animatable(1f) }

    LaunchedEffect(mood) {
        launch { eyeOpen.animateTo(target.eyeOpen, PoseSpring) }
        launch { eyeWidth.animateTo(target.eyeWidth, PoseSpring) }
        launch { eyeHeight.animateTo(target.eyeHeight, PoseSpring) }
        launch { eyeGap.animateTo(target.eyeGap, PoseSpring) }
        launch { tilt.animateTo(target.tilt, PoseSpring) }
        launch { borderGlow.animateTo(target.borderGlow, PoseSpring) }
        launch { blush.animateTo(target.blush, PoseSpring) }
        launch { roundness.animateTo(target.roundness, PoseSpring) }
        launch {
            propVisibility.animateTo(
                if (mood == AssistantMood.Idle) 0f else 1f,
                tween(420, easing = FastOutSlowInEasing),
            )
        }
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
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "life",
    )
    val scan by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan",
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "border_pulse",
    )

    LaunchedEffect(mood) {
        while (isActive) {
            val wait = when (mood) {
                AssistantMood.Listening -> Random.nextLong(2000, 3600)
                AssistantMood.Searching -> Random.nextLong(1100, 1800)
                AssistantMood.Happy -> Random.nextLong(2400, 4000)
                AssistantMood.Sad -> Random.nextLong(3000, 5000)
                AssistantMood.Thinking -> Random.nextLong(2600, 4200)
                else -> Random.nextLong(2200, 4200)
            }
            delay(wait)
            val closeTo = when (mood) {
                AssistantMood.Happy -> 0.4f
                else -> 0.1f
            }
            // Soft, natural blink — not snappy
            blink.animateTo(closeTo, tween(90, easing = FastOutSlowInEasing))
            delay(50)
            blink.animateTo(1f, tween(150, easing = FastOutSlowInEasing))
            if (Random.nextFloat() < 0.32f) {
                delay(110)
                blink.animateTo(closeTo, tween(70))
                delay(40)
                blink.animateTo(1f, tween(130))
            }
        }
    }

    LaunchedEffect(mood) {
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) return@LaunchedEffect
        while (isActive) {
            if (mood == AssistantMood.Reading) {
                lookX.animateTo(0.4f, tween(900, easing = FastOutSlowInEasing))
                delay(160)
                lookX.animateTo(-0.35f, tween(100, easing = FastOutSlowInEasing))
                delay(100)
            } else {
                lookX.animateTo(0.45f, tween(220, easing = FastOutSlowInEasing))
                lookX.animateTo(-0.4f, tween(260, easing = FastOutSlowInEasing))
                lookX.animateTo(0.08f, tween(180, easing = FastOutSlowInEasing))
                delay(80)
            }
        }
    }

    Canvas(modifier = modifier.aspectRatio(1.15f)) {
        val side = minOf(size.width, size.height)
        val cx = size.width * 0.5f
        val cy = size.height * 0.52f
        val shell = side * 0.68f
        val corner = shell * 0.32f
        val border = shell * 0.052f

        // Gentle natural bob
        val bobY = sin(life * 0.7f).toFloat() * shell * 0.02f
        translate(top = bobY) {
            val liveTilt = tilt.value + 0.9f * sin(life * 0.45f).toFloat()

            // Props float outside rotation for a softer read
            drawMoodProp(
                mood = mood,
                center = Offset(cx, cy),
                shell = shell,
                visibility = propVisibility.value,
                life = life,
            )

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

                val glowR = shell * 0.62f * pulse * borderGlow.value
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentCool.copy(alpha = 0.16f * borderGlow.value),
                            AccentWarm.copy(alpha = 0.1f * borderGlow.value),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = glowR,
                    ),
                    radius = glowR,
                    center = Offset(cx, cy),
                )

                drawPath(shellPath, ShellFill)

                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ShellBorder,
                            AccentCool,
                            ShellBorder,
                            AccentWarm.copy(alpha = 0.8f),
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

                clipPath(shellPath) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentWarm.copy(alpha = 0.18f),
                                Color.Transparent,
                            ),
                            center = Offset(cx + shell * 0.22f, cy - shell * 0.18f),
                            radius = shell * 0.35f,
                        ),
                        radius = shell * 0.35f,
                        center = Offset(cx + shell * 0.22f, cy - shell * 0.18f),
                    )
                }

                val open = (eyeOpen.value * blink.value).coerceIn(0.08f, 1.25f)
                val eW = shell * 0.125f * eyeWidth.value
                val eH = shell * 0.175f * eyeHeight.value * open
                val gap = shell * 0.125f * eyeGap.value
                val eyeY = cy + lookY.value * shell * 0.08f
                val gaze = lookX.value * shell * 0.04f +
                    if (mood == AssistantMood.Thinking) 0.018f * shell * sin(life) else 0f

                val leftEye = Offset(cx - gap - eW * 0.5f + gaze, eyeY)
                val rightEye = Offset(cx + gap + eW * 0.5f + gaze, eyeY)

                drawCheekBlush(
                    left = Offset(cx - shell * 0.28f, cy + shell * 0.16f),
                    right = Offset(cx + shell * 0.28f, cy + shell * 0.16f),
                    radius = shell * 0.07f,
                    amount = blush.value,
                )

                drawCircle(faceColor.copy(alpha = 0.18f * open), eW * 1.35f, leftEye)
                drawCircle(faceColor.copy(alpha = 0.18f * open), eW * 1.35f, rightEye)

                drawScanlineEye(
                    center = leftEye,
                    width = eW,
                    height = eH,
                    color = faceColor,
                    scanPhase = scan,
                    roundness = roundness.value,
                )
                drawScanlineEye(
                    center = rightEye,
                    width = eW,
                    height = eH,
                    color = faceColor,
                    scanPhase = scan + 0.12f,
                    roundness = roundness.value,
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawScanlineEye(
    center: Offset,
    width: Float,
    height: Float,
    color: Color,
    scanPhase: Float,
    roundness: Float,
) {
    val left = center.x - width
    val top = center.y - height
    val eyeW = width * 2f
    val eyeH = height * 2f
    val rx = width * (0.4f + roundness * 0.45f)
    val ry = height * (0.4f + roundness * 0.45f)
    val radius = CornerRadius(rx, ry)
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
        // Softer scanlines — cute digital eyes, not harsh CRT
        val lineGap = (eyeH / 8f).coerceAtLeast(1.8f)
        var y = top + lineGap * 0.5f
        var i = 0
        while (y < top + eyeH) {
            val shimmer = 0.05f + 0.07f * ((sin((scanPhase + i * 0.12f) * PI * 2).toFloat() + 1f) * 0.5f)
            drawLine(
                color = Color(0xFF0A0C10).copy(alpha = shimmer),
                start = Offset(left, y),
                end = Offset(left + eyeW, y),
                strokeWidth = lineGap * 0.28f,
            )
            y += lineGap
            i++
        }
    }
}
