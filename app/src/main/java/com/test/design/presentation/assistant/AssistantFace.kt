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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Minimal virtual-assistant face — soft eyes + mouth with mood-driven motion and eye glow.
 */
@Composable
fun AssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    faceColor: Color = Color.White,
) {
    val blink = remember { Animatable(1f) }
    val lookX = remember { Animatable(0f) }
    val lookY = remember { Animatable(0f) }

    val infinite = rememberInfiniteTransition(label = "assistant_face")
    val pulse by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(moodPulseMs(mood), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_pulse",
    )
    val speakPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "speak_phase",
    )
    val thinkPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "think_phase",
    )
    val searchPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "search_phase",
    )
    val readPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "read_phase",
    )
    val listenPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "listen_phase",
    )

    LaunchedEffect(mood) {
        when (mood) {
            AssistantMood.Thinking -> {
                lookX.animateTo(0.35f, tween(500, easing = FastOutSlowInEasing))
                lookY.animateTo(-0.45f, tween(500, easing = FastOutSlowInEasing))
            }
            AssistantMood.Sad -> {
                lookX.animateTo(0f, tween(400))
                lookY.animateTo(0.35f, tween(400))
            }
            AssistantMood.Happy, AssistantMood.Idle, AssistantMood.Speaking, AssistantMood.Listening -> {
                lookX.animateTo(0f, tween(350))
                lookY.animateTo(0f, tween(350))
            }
            AssistantMood.Reading, AssistantMood.Searching -> {
                lookY.animateTo(0f, tween(300))
            }
        }
    }

    LaunchedEffect(mood) {
        while (isActive) {
            val openMs = when (mood) {
                AssistantMood.Listening -> Random.nextLong(2200, 3800)
                AssistantMood.Thinking -> Random.nextLong(2800, 4500)
                AssistantMood.Reading -> Random.nextLong(1800, 2800)
                AssistantMood.Searching -> Random.nextLong(900, 1600)
                AssistantMood.Happy -> Random.nextLong(2500, 4000)
                AssistantMood.Sad -> Random.nextLong(3200, 5200)
                else -> Random.nextLong(2400, 4200)
            }
            delay(openMs)
            // Happy uses smile-eyes; skip full blink close so crescents stay readable.
            if (mood == AssistantMood.Happy) {
                blink.animateTo(0.55f, tween(70))
                blink.animateTo(1f, tween(110))
            } else {
                blink.animateTo(0.08f, tween(70))
                delay(40)
                blink.animateTo(1f, tween(120))
            }
        }
    }

    LaunchedEffect(mood) {
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) return@LaunchedEffect
        while (isActive) {
            if (mood == AssistantMood.Reading) {
                lookX.animateTo(0.55f, tween(900, easing = FastOutSlowInEasing))
                delay(180)
                lookX.animateTo(-0.55f, tween(70))
                delay(120)
            } else {
                lookX.animateTo(0.6f, tween(180))
                lookX.animateTo(-0.55f, tween(220))
                lookX.animateTo(0.2f, tween(160))
                delay(90)
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.15f),
    ) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val eyeY = h * 0.42f
        val eyeGap = w * 0.22f
        val eyeW = w * 0.11f
        val eyeH = h * 0.13f

        val gazeX = when (mood) {
            AssistantMood.Reading, AssistantMood.Searching -> lookX.value
            AssistantMood.Thinking -> lookX.value + 0.08f * sin(thinkPhase)
            else -> lookX.value
        }
        val gazeY = when (mood) {
            AssistantMood.Thinking -> lookY.value + 0.06f * sin(thinkPhase * 0.7f)
            else -> lookY.value
        }

        val eyeScale = when (mood) {
            AssistantMood.Listening -> 1f + 0.06f * sin(listenPhase)
            AssistantMood.Speaking -> 1f + 0.03f * sin(speakPhase)
            AssistantMood.Searching -> 1f + 0.05f * sin(searchPhase * 1.5f)
            else -> 1f
        }

        val leftEye = Offset(cx - eyeGap, eyeY)
        val rightEye = Offset(cx + eyeGap, eyeY)
        val glowR = eyeW * (2.4f + pulse * mood.glowIntensity)
        val glowAlpha = (mood.glowIntensity * 0.55f * pulse).coerceIn(0f, 0.9f)

        drawEyeGlow(leftEye, glowR, mood.glowColor, glowAlpha)
        drawEyeGlow(rightEye, glowR, mood.glowColor, glowAlpha)

        drawEye(
            center = leftEye,
            width = eyeW * eyeScale,
            height = eyeH * eyeScale,
            open = blink.value,
            mood = mood,
            faceColor = faceColor,
            pupilOffset = Offset(gazeX * eyeW * 0.35f, gazeY * eyeH * 0.3f),
            speakPhase = speakPhase,
            readPhase = readPhase,
        )
        drawEye(
            center = rightEye,
            width = eyeW * eyeScale,
            height = eyeH * eyeScale,
            open = blink.value,
            mood = mood,
            faceColor = faceColor,
            pupilOffset = Offset(gazeX * eyeW * 0.35f, gazeY * eyeH * 0.3f),
            speakPhase = speakPhase,
            readPhase = readPhase,
        )

        drawMouth(
            center = Offset(cx, h * 0.68f),
            width = w * 0.22f,
            mood = mood,
            faceColor = faceColor,
            speakPhase = speakPhase,
            listenPhase = listenPhase,
        )
    }
}

private fun moodPulseMs(mood: AssistantMood): Int = when (mood) {
    AssistantMood.Listening -> 900
    AssistantMood.Speaking -> 700
    AssistantMood.Searching -> 650
    AssistantMood.Thinking -> 1400
    AssistantMood.Happy -> 1100
    AssistantMood.Sad -> 1800
    AssistantMood.Reading -> 1200
    AssistantMood.Idle -> 1600
}

private fun DrawScope.drawEyeGlow(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float,
) {
    if (alpha <= 0.01f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                color.copy(alpha = alpha * 0.35f),
                Color.Transparent,
            ),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawEye(
    center: Offset,
    width: Float,
    height: Float,
    open: Float,
    mood: AssistantMood,
    faceColor: Color,
    pupilOffset: Offset,
    speakPhase: Float,
    readPhase: Float,
) {
    val openH = height * open.coerceIn(0.06f, 1.2f)
    when (mood) {
        AssistantMood.Happy -> {
            // Crescent smile-eyes
            val path = Path().apply {
                moveTo(center.x - width, center.y)
                quadraticTo(center.x, center.y + openH * 0.85f, center.x + width, center.y)
            }
            drawPath(
                path = path,
                color = faceColor,
                style = Stroke(width = height * 0.28f, cap = StrokeCap.Round),
            )
        }
        AssistantMood.Sad -> {
            val lidDrop = height * 0.22f
            val rect = Rect(
                center.x - width,
                center.y - openH * 0.35f + lidDrop,
                center.x + width,
                center.y + openH * 0.55f + lidDrop,
            )
            drawOval(color = faceColor, topLeft = rect.topLeft, size = rect.size)
            // Soft upper lid shadow to suggest droop
            drawOval(
                color = faceColor.copy(alpha = 0.25f),
                topLeft = Offset(rect.left, rect.top - openH * 0.15f),
                size = Size(rect.width, openH * 0.35f),
            )
        }
        else -> {
            val rect = Rect(
                center.x - width,
                center.y - openH,
                center.x + width,
                center.y + openH,
            )
            val corner = CornerRadius(width, openH)
            val eyePath = Path().apply {
                addRoundRect(RoundRect(rect, corner, corner, corner, corner))
            }
            clipPath(eyePath) {
                drawRoundRect(
                    color = faceColor,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    cornerRadius = corner,
                )
                // Subtle pupil / focus spark for attentive moods
                if (mood == AssistantMood.Listening ||
                    mood == AssistantMood.Reading ||
                    mood == AssistantMood.Searching ||
                    mood == AssistantMood.Thinking
                ) {
                    val spark = Offset(
                        center.x + pupilOffset.x,
                        center.y + pupilOffset.y +
                            if (mood == AssistantMood.Reading) 0.04f * height * sin(readPhase) else 0f,
                    )
                    drawCircle(
                        color = Color(0xFF0D1B2A).copy(alpha = 0.35f),
                        radius = width * 0.28f,
                        center = spark,
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.55f),
                        radius = width * 0.1f,
                        center = spark + Offset(-width * 0.08f, -width * 0.08f),
                    )
                }
            }
            if (mood == AssistantMood.Speaking) {
                // Tiny bounce of the lower lid while talking
                val bounce = 0.04f * height * sin(speakPhase)
                drawLine(
                    color = faceColor.copy(alpha = 0.35f),
                    start = Offset(center.x - width * 0.7f, center.y + openH * 0.7f + bounce),
                    end = Offset(center.x + width * 0.7f, center.y + openH * 0.7f + bounce),
                    strokeWidth = height * 0.08f,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

private fun DrawScope.drawMouth(
    center: Offset,
    width: Float,
    mood: AssistantMood,
    faceColor: Color,
    speakPhase: Float,
    listenPhase: Float,
) {
    val stroke = width * 0.12f
    when (mood) {
        AssistantMood.Speaking -> {
            val open = (0.35f + 0.55f * ((sin(speakPhase.toDouble()) + 1.0) * 0.5).toFloat())
            val mouthH = width * 0.22f * open
            val rect = Rect(
                center.x - width * 0.45f,
                center.y - mouthH * 0.2f,
                center.x + width * 0.45f,
                center.y + mouthH,
            )
            drawRoundRect(
                color = faceColor,
                topLeft = rect.topLeft,
                size = rect.size,
                cornerRadius = CornerRadius(width * 0.35f, mouthH),
            )
        }
        AssistantMood.Happy -> {
            val path = Path().apply {
                moveTo(center.x - width * 0.55f, center.y - width * 0.05f)
                quadraticTo(center.x, center.y + width * 0.45f, center.x + width * 0.55f, center.y - width * 0.05f)
            }
            drawPath(path, faceColor, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        AssistantMood.Sad -> {
            val path = Path().apply {
                moveTo(center.x - width * 0.4f, center.y + width * 0.18f)
                quadraticTo(center.x, center.y - width * 0.22f, center.x + width * 0.4f, center.y + width * 0.18f)
            }
            drawPath(path, faceColor, style = Stroke(stroke * 0.9f, cap = StrokeCap.Round))
        }
        AssistantMood.Listening -> {
            // Soft attentive oval that gently breathes
            val breath = 1f + 0.08f * sin(listenPhase)
            drawOval(
                color = faceColor,
                topLeft = Offset(center.x - width * 0.22f * breath, center.y - width * 0.08f),
                size = Size(width * 0.44f * breath, width * 0.2f),
            )
        }
        AssistantMood.Thinking -> {
            drawCircle(
                color = faceColor,
                radius = stroke * 0.7f,
                center = center,
            )
        }
        AssistantMood.Reading, AssistantMood.Searching -> {
            drawLine(
                color = faceColor,
                start = Offset(center.x - width * 0.28f, center.y),
                end = Offset(center.x + width * 0.28f, center.y),
                strokeWidth = stroke * 0.85f,
                cap = StrokeCap.Round,
            )
        }
        AssistantMood.Idle -> {
            val path = Path().apply {
                moveTo(center.x - width * 0.35f, center.y)
                quadraticTo(center.x, center.y + width * 0.18f, center.x + width * 0.35f, center.y)
            }
            drawPath(path, faceColor, style = Stroke(stroke * 0.85f, cap = StrokeCap.Round))
        }
    }
}
