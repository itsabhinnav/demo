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
import androidx.compose.foundation.layout.aspectRatio
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
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Continuous facial pose — every mood is the same character with different
 * parameter targets. Eyes stay soft ovals; mouth stays one curve that can open.
 */
internal data class FacePose(
    val eyeOpen: Float = 1f,
    val eyeSmile: Float = 0f,
    val eyeDroop: Float = 0f,
    val eyeScale: Float = 1f,
    val pupilAlpha: Float = 0.2f,
    val mouthCurve: Float = 0.18f,
    val mouthOpen: Float = 0f,
    val mouthWidth: Float = 0.35f,
    val glowIntensity: Float = 0.35f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
)

internal fun AssistantMood.toFacePose(): FacePose = when (this) {
    AssistantMood.Idle -> FacePose(
        eyeOpen = 1f,
        mouthCurve = 0.18f,
        mouthWidth = 0.35f,
        glowIntensity = glowIntensity,
        pupilAlpha = 0.12f,
    )
    AssistantMood.Listening -> FacePose(
        eyeOpen = 1.08f,
        eyeScale = 1.06f,
        mouthCurve = 0.08f,
        mouthOpen = 0.22f,
        mouthWidth = 0.32f,
        glowIntensity = glowIntensity,
        pupilAlpha = 0.45f,
    )
    AssistantMood.Speaking -> FacePose(
        eyeOpen = 0.95f,
        mouthCurve = 0.12f,
        mouthOpen = 0.7f,
        mouthWidth = 0.42f,
        glowIntensity = glowIntensity,
        pupilAlpha = 0.2f,
    )
    AssistantMood.Thinking -> FacePose(
        eyeOpen = 0.88f,
        mouthCurve = 0.02f,
        mouthWidth = 0.18f,
        glowIntensity = glowIntensity,
        lookX = 0.32f,
        lookY = -0.42f,
        pupilAlpha = 0.4f,
    )
    AssistantMood.Happy -> FacePose(
        eyeOpen = 0.72f,
        eyeSmile = 0.85f,
        mouthCurve = 0.48f,
        mouthWidth = 0.52f,
        glowIntensity = glowIntensity,
        pupilAlpha = 0.08f,
    )
    AssistantMood.Sad -> FacePose(
        eyeOpen = 0.78f,
        eyeDroop = 0.7f,
        mouthCurve = -0.28f,
        mouthWidth = 0.34f,
        glowIntensity = glowIntensity,
        lookY = 0.28f,
        pupilAlpha = 0.15f,
    )
    AssistantMood.Reading -> FacePose(
        eyeOpen = 0.92f,
        mouthCurve = 0.04f,
        mouthWidth = 0.28f,
        glowIntensity = glowIntensity,
        pupilAlpha = 0.5f,
    )
    AssistantMood.Searching -> FacePose(
        eyeOpen = 1.02f,
        eyeScale = 1.04f,
        mouthCurve = 0.06f,
        mouthWidth = 0.3f,
        glowIntensity = glowIntensity,
        pupilAlpha = 0.48f,
    )
}

private val PoseSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

/**
 * Minimal virtual-assistant face — one continuous character; moods morph pose.
 */
@Composable
fun AssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    faceColor: Color = Color.White,
) {
    val target = mood.toFacePose()

    val eyeOpen = remember { Animatable(target.eyeOpen) }
    val eyeSmile = remember { Animatable(target.eyeSmile) }
    val eyeDroop = remember { Animatable(target.eyeDroop) }
    val eyeScale = remember { Animatable(target.eyeScale) }
    val pupilAlpha = remember { Animatable(target.pupilAlpha) }
    val mouthCurve = remember { Animatable(target.mouthCurve) }
    val mouthOpen = remember { Animatable(target.mouthOpen) }
    val mouthWidth = remember { Animatable(target.mouthWidth) }
    val glowIntensity = remember { Animatable(target.glowIntensity) }
    val lookX = remember { Animatable(target.lookX) }
    val lookY = remember { Animatable(target.lookY) }
    val blink = remember { Animatable(1f) }

    val glowColor by animateColorAsState(
        targetValue = mood.glowColor,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "glow_color",
    )

    LaunchedEffect(mood) {
        launch { eyeOpen.animateTo(target.eyeOpen, PoseSpring) }
        launch { eyeSmile.animateTo(target.eyeSmile, PoseSpring) }
        launch { eyeDroop.animateTo(target.eyeDroop, PoseSpring) }
        launch { eyeScale.animateTo(target.eyeScale, PoseSpring) }
        launch { pupilAlpha.animateTo(target.pupilAlpha, PoseSpring) }
        launch { mouthCurve.animateTo(target.mouthCurve, PoseSpring) }
        launch { mouthOpen.animateTo(target.mouthOpen, PoseSpring) }
        launch { mouthWidth.animateTo(target.mouthWidth, PoseSpring) }
        launch { glowIntensity.animateTo(target.glowIntensity, PoseSpring) }
        // Gaze returns home unless reading/searching own the axis.
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) {
            launch { lookX.animateTo(target.lookX, PoseSpring) }
        }
        launch { lookY.animateTo(target.lookY, PoseSpring) }
    }

    val infinite = rememberInfiniteTransition(label = "assistant_face")
    val pulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(moodPulseMs(mood), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_pulse",
    )
    val lifePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "life_phase",
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
            // Soft blink — never fully collapse happy crescents into a hard cut.
            val close = (0.12f + eyeSmile.value * 0.35f).coerceIn(0.1f, 0.55f)
            blink.animateTo(close, tween(80))
            delay(36)
            blink.animateTo(1f, tween(130, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(mood) {
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) return@LaunchedEffect
        while (isActive) {
            if (mood == AssistantMood.Reading) {
                lookX.animateTo(0.5f, tween(900, easing = FastOutSlowInEasing))
                delay(160)
                lookX.animateTo(-0.5f, tween(90, easing = FastOutSlowInEasing))
                delay(100)
            } else {
                lookX.animateTo(0.55f, tween(200, easing = FastOutSlowInEasing))
                lookX.animateTo(-0.5f, tween(240, easing = FastOutSlowInEasing))
                lookX.animateTo(0.15f, tween(180, easing = FastOutSlowInEasing))
                delay(80)
            }
        }
    }

    Canvas(modifier = modifier.aspectRatio(1.15f)) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val eyeY = h * 0.42f
        val eyeGap = w * 0.22f
        val baseEyeW = w * 0.11f
        val baseEyeH = h * 0.13f

        val gazeX = lookX.value + when (mood) {
            AssistantMood.Thinking -> 0.06f * sin(lifePhase)
            else -> 0f
        }
        val gazeY = lookY.value + when (mood) {
            AssistantMood.Thinking -> 0.05f * sin(lifePhase * 0.7f)
            else -> 0f
        }

        val breathScale = when (mood) {
            AssistantMood.Listening -> 1f + 0.04f * sin(lifePhase)
            AssistantMood.Speaking -> 1f + 0.02f * sin(speakPhase)
            AssistantMood.Searching -> 1f + 0.035f * sin(lifePhase * 1.4f)
            else -> 1f + 0.012f * sin(lifePhase * 0.5f)
        }
        val scale = eyeScale.value * breathScale
        val eyeW = baseEyeW * scale
        val eyeH = baseEyeH * scale

        val leftEye = Offset(cx - eyeGap, eyeY)
        val rightEye = Offset(cx + eyeGap, eyeY)
        val glowR = eyeW * (2.35f + pulse * glowIntensity.value)
        val glowAlpha = (glowIntensity.value * 0.55f * pulse).coerceIn(0f, 0.9f)

        drawEyeGlow(leftEye, glowR, glowColor, glowAlpha)
        drawEyeGlow(rightEye, glowR, glowColor, glowAlpha)

        val openAmount = (eyeOpen.value * blink.value).coerceIn(0.08f, 1.2f)
        val liveMouthOpen = if (mood == AssistantMood.Speaking) {
            mouthOpen.value * (0.45f + 0.55f * ((sin(speakPhase.toDouble()) + 1.0) * 0.5).toFloat())
        } else {
            mouthOpen.value * (1f + 0.06f * sin(lifePhase))
        }

        drawCharacterEye(
            center = leftEye,
            width = eyeW,
            height = eyeH,
            open = openAmount,
            smile = eyeSmile.value,
            droop = eyeDroop.value,
            faceColor = faceColor,
            pupilOffset = Offset(gazeX * eyeW * 0.35f, gazeY * eyeH * 0.3f),
            pupilAlpha = pupilAlpha.value,
        )
        drawCharacterEye(
            center = rightEye,
            width = eyeW,
            height = eyeH,
            open = openAmount,
            smile = eyeSmile.value,
            droop = eyeDroop.value,
            faceColor = faceColor,
            pupilOffset = Offset(gazeX * eyeW * 0.35f, gazeY * eyeH * 0.3f),
            pupilAlpha = pupilAlpha.value,
        )

        drawCharacterMouth(
            center = Offset(cx, h * 0.68f),
            width = w * 0.22f,
            curve = mouthCurve.value,
            open = liveMouthOpen,
            widthFactor = mouthWidth.value,
            faceColor = faceColor,
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

/**
 * Single eye primitive for every mood: soft oval that can squint (smile) or droop.
 */
private fun DrawScope.drawCharacterEye(
    center: Offset,
    width: Float,
    height: Float,
    open: Float,
    smile: Float,
    droop: Float,
    faceColor: Color,
    pupilOffset: Offset,
    pupilAlpha: Float,
) {
    val openH = height * open
    // Smile squeezes the eye into a gentle crescent without swapping draw modes.
    val topInset = smile * openH * 0.72f
    val bottomBoost = smile * openH * 0.18f
    val droopShift = droop * height * 0.2f

    val top = center.y - openH + topInset + droopShift
    val bottom = center.y + openH + bottomBoost + droopShift
    val left = center.x - width
    val right = center.x + width
    val rect = Rect(left, top, right, bottom)
    val corner = CornerRadius(width, (bottom - top) * 0.5f)
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
        if (pupilAlpha > 0.04f && smile < 0.75f) {
            val spark = Offset(center.x + pupilOffset.x, center.y + pupilOffset.y + droopShift * 0.3f)
            drawCircle(
                color = Color(0xFF0D1B2A).copy(alpha = 0.32f * pupilAlpha),
                radius = width * 0.26f,
                center = spark,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.5f * pupilAlpha),
                radius = width * 0.09f,
                center = spark + Offset(-width * 0.07f, -width * 0.07f),
            )
        }
    }

    // Soft lid line that eases in with droop / smile for continuity.
    if (droop > 0.05f || smile > 0.35f) {
        val lidY = top + (bottom - top) * (0.18f + droop * 0.12f)
        drawLine(
            color = faceColor.copy(alpha = 0.22f * (droop + smile * 0.4f).coerceAtMost(1f)),
            start = Offset(left + width * 0.15f, lidY),
            end = Offset(right - width * 0.15f, lidY),
            strokeWidth = height * 0.06f,
            cap = StrokeCap.Round,
        )
    }
}

/**
 * Single mouth primitive: curved stroke that blooms into an open oval when speaking.
 */
private fun DrawScope.drawCharacterMouth(
    center: Offset,
    width: Float,
    curve: Float,
    open: Float,
    widthFactor: Float,
    faceColor: Color,
) {
    val halfW = width * widthFactor.coerceIn(0.12f, 0.7f)
    val stroke = width * 0.11f
    val curveAmt = curve * width

    if (open > 0.08f) {
        val mouthH = width * 0.26f * open
        // Blend open oval with the smile/frown curve so speaking still feels like the same mouth.
        val midY = center.y + curveAmt * 0.35f
        val rect = Rect(
            center.x - halfW,
            midY - mouthH * 0.25f,
            center.x + halfW,
            midY + mouthH,
        )
        drawRoundRect(
            color = faceColor,
            topLeft = rect.topLeft,
            size = Size(rect.width, rect.height),
            cornerRadius = CornerRadius(halfW * 0.85f, mouthH * 0.85f),
        )
        // Keep a faint lip curve on top for identity while open.
        val lip = Path().apply {
            moveTo(center.x - halfW * 0.92f, midY)
            quadraticTo(center.x, midY + curveAmt * 0.55f, center.x + halfW * 0.92f, midY)
        }
        drawPath(
            lip,
            faceColor.copy(alpha = 0.35f),
            style = Stroke(stroke * 0.55f, cap = StrokeCap.Round),
        )
    } else {
        val path = Path().apply {
            moveTo(center.x - halfW, center.y)
            quadraticTo(center.x, center.y + curveAmt, center.x + halfW, center.y)
        }
        drawPath(path, faceColor, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}
