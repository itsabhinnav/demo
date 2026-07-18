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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

/**
 * Eyes-only immersive persona — elliptical eyes + soft face glow, no solid orb body.
 * Mouth appears when speaking or when the mood strongly implies expression.
 */
internal data class ImmersiveEyePose(
    val eyeOpen: Float = 1f,
    val eyeWidth: Float = 1.15f,
    val eyeHeight: Float = 0.85f,
    val eyeGap: Float = 1f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val tilt: Float = 0f,
    val faceGlow: Float = 0.7f,
    val eyeStyle: Float = 0f,
    val mouthCurve: Float = 0.15f,
    val mouthOpen: Float = 0f,
    val mouthVisible: Float = 0f,
    val blinkSpeed: Float = 1f,
)

internal fun AssistantMood.toImmersiveEyePose(): ImmersiveEyePose = when (this) {
    AssistantMood.Idle -> ImmersiveEyePose(
        eyeOpen = 1f,
        eyeWidth = 1.2f,
        eyeHeight = 0.9f,
        faceGlow = 0.55f,
        mouthVisible = 0f,
        tilt = 1.5f,
    )
    AssistantMood.Listening -> ImmersiveEyePose(
        eyeOpen = 1.2f,
        eyeWidth = 1.25f,
        eyeHeight = 1.05f,
        faceGlow = 1f,
        mouthVisible = 0f,
        lookY = -0.05f,
    )
    AssistantMood.Speaking -> ImmersiveEyePose(
        eyeOpen = 1.05f,
        eyeWidth = 1.15f,
        eyeHeight = 0.95f,
        faceGlow = 0.9f,
        mouthCurve = 0.35f,
        mouthOpen = 0.55f,
        mouthVisible = 1f,
        tilt = 2f,
    )
    AssistantMood.Thinking -> ImmersiveEyePose(
        eyeOpen = 0.9f,
        eyeWidth = 1.1f,
        eyeHeight = 0.75f,
        lookX = 0.35f,
        lookY = -0.12f,
        eyeStyle = -0.2f,
        faceGlow = 0.7f,
        mouthVisible = 0f,
        tilt = 8f,
    )
    AssistantMood.Happy -> ImmersiveEyePose(
        eyeOpen = 0.95f,
        eyeWidth = 1.3f,
        eyeHeight = 0.85f,
        eyeStyle = 1f,
        faceGlow = 0.95f,
        mouthCurve = 0.85f,
        mouthOpen = 0.06f,
        mouthVisible = 0.85f,
        tilt = -2f,
    )
    AssistantMood.Sad -> ImmersiveEyePose(
        eyeOpen = 0.7f,
        eyeWidth = 1.15f,
        eyeHeight = 0.65f,
        lookY = 0.22f,
        eyeStyle = -0.65f,
        faceGlow = 0.4f,
        mouthCurve = -0.75f,
        mouthOpen = 0.02f,
        mouthVisible = 0.7f,
        tilt = 6f,
        blinkSpeed = 0.7f,
    )
    AssistantMood.Excited -> ImmersiveEyePose(
        eyeOpen = 1.35f,
        eyeWidth = 1.35f,
        eyeHeight = 1.2f,
        faceGlow = 1.1f,
        mouthCurve = 0.95f,
        mouthOpen = 0.35f,
        mouthVisible = 1f,
        tilt = -4f,
        blinkSpeed = 1.4f,
    )
    AssistantMood.Bored -> ImmersiveEyePose(
        eyeOpen = 0.55f,
        eyeWidth = 1.25f,
        eyeHeight = 0.55f,
        lookX = 0.4f,
        lookY = 0.08f,
        eyeStyle = -0.45f,
        faceGlow = 0.35f,
        mouthCurve = -0.15f,
        mouthVisible = 0.35f,
        tilt = 4f,
        blinkSpeed = 0.55f,
    )
    AssistantMood.Drowsy -> ImmersiveEyePose(
        eyeOpen = 0.35f,
        eyeWidth = 1.3f,
        eyeHeight = 0.4f,
        lookY = 0.12f,
        eyeStyle = -0.85f,
        faceGlow = 0.3f,
        mouthVisible = 0f,
        tilt = 3f,
        blinkSpeed = 0.4f,
    )
    AssistantMood.Tired -> ImmersiveEyePose(
        eyeOpen = 0.45f,
        eyeWidth = 1.2f,
        eyeHeight = 0.5f,
        lookY = 0.18f,
        eyeStyle = -0.75f,
        faceGlow = 0.28f,
        mouthCurve = -0.25f,
        mouthVisible = 0.25f,
        tilt = 5f,
        blinkSpeed = 0.35f,
    )
    AssistantMood.Reading -> ImmersiveEyePose(
        eyeOpen = 0.95f,
        eyeWidth = 1.15f,
        eyeHeight = 0.85f,
        lookX = 0.3f,
        faceGlow = 0.65f,
        mouthVisible = 0f,
    )
    AssistantMood.Searching -> ImmersiveEyePose(
        eyeOpen = 1.15f,
        eyeWidth = 1.2f,
        eyeHeight = 1f,
        faceGlow = 0.95f,
        mouthVisible = 0f,
        tilt = 2f,
        blinkSpeed = 1.2f,
    )
}

private val PoseSpring = spring<Float>(
    dampingRatio = 0.78f,
    stiffness = Spring.StiffnessMediumLow,
)

/**
 * Centered elliptical eyes with a soft face-shaped glow. Mouth draws when speaking
 * or when the mood asks for a smile / frown.
 *
 * @param gazeX/gazeY optional cabin gaze override (−1..1); null keeps mood look loops
 * @param mouthAmplitude optional lip-sync 0..1 (drives mouth while speaking)
 * @param brandGlow OEM / Material accent blended into the face aura
 * @param highContrast sunlight-safe eye fill + stronger glow
 * @param gesture nod / shake micro-expressions for yes/no
 */
@Composable
fun ImmersiveEyesFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    gazeX: Float? = null,
    gazeY: Float? = null,
    mouthAmplitude: Float? = null,
    brandGlow: Color = Color(0xFF8AB4F8),
    highContrast: Boolean = false,
    gesture: FaceGesture = FaceGesture.None,
) {
    val target = mood.toImmersiveEyePose()
    val eyeOpen = remember { Animatable(target.eyeOpen) }
    val eyeWidth = remember { Animatable(target.eyeWidth) }
    val eyeHeight = remember { Animatable(target.eyeHeight) }
    val eyeGap = remember { Animatable(target.eyeGap) }
    val lookX = remember { Animatable(target.lookX) }
    val lookY = remember { Animatable(target.lookY) }
    val tilt = remember { Animatable(target.tilt) }
    val faceGlow = remember { Animatable(target.faceGlow) }
    val eyeStyle = remember { Animatable(target.eyeStyle) }
    val mouthCurve = remember { Animatable(target.mouthCurve) }
    val mouthOpen = remember { Animatable(target.mouthOpen) }
    val mouthVisible = remember { Animatable(target.mouthVisible) }
    val blink = remember { Animatable(1f) }
    val externalGaze = gazeX != null || gazeY != null

    LaunchedEffect(mood, highContrast) {
        val glowBoost = if (highContrast) 1.25f else 1f
        launch { eyeOpen.animateTo(target.eyeOpen, PoseSpring) }
        launch { eyeWidth.animateTo(target.eyeWidth, PoseSpring) }
        launch { eyeHeight.animateTo(target.eyeHeight, PoseSpring) }
        launch { eyeGap.animateTo(target.eyeGap, PoseSpring) }
        launch { lookY.animateTo(gazeY ?: target.lookY, PoseSpring) }
        launch { tilt.animateTo(target.tilt, PoseSpring) }
        launch { faceGlow.animateTo((target.faceGlow * glowBoost).coerceAtMost(1.2f), PoseSpring) }
        launch { eyeStyle.animateTo(target.eyeStyle, PoseSpring) }
        launch { mouthCurve.animateTo(target.mouthCurve, PoseSpring) }
        launch { mouthVisible.animateTo(target.mouthVisible, PoseSpring) }
        if (mouthAmplitude == null &&
            mood != AssistantMood.Speaking &&
            mood != AssistantMood.Excited
        ) {
            launch { mouthOpen.animateTo(target.mouthOpen, PoseSpring) }
        }
        if (!externalGaze &&
            mood != AssistantMood.Reading &&
            mood != AssistantMood.Searching &&
            mood != AssistantMood.Bored
        ) {
            launch { lookX.animateTo(target.lookX, PoseSpring) }
        }
    }

    LaunchedEffect(gazeX, gazeY) {
        if (gazeX != null) lookX.animateTo(gazeX, PoseSpring)
        if (gazeY != null) lookY.animateTo(gazeY, PoseSpring)
    }

    LaunchedEffect(mouthAmplitude, mood) {
        if (mouthAmplitude != null) {
            mouthVisible.animateTo(maxOf(target.mouthVisible, 0.85f), PoseSpring)
            mouthOpen.snapTo(mouthAmplitude.coerceIn(0f, 1f))
            return@LaunchedEffect
        }
        if (mood != AssistantMood.Speaking && mood != AssistantMood.Excited) return@LaunchedEffect
        while (isActive) {
            mouthOpen.animateTo(
                Random.nextFloat() * 0.4f + 0.3f,
                tween(Random.nextInt(70, 130)),
            )
            mouthOpen.animateTo(
                Random.nextFloat() * 0.12f + 0.04f,
                tween(Random.nextInt(55, 100)),
            )
        }
    }

    LaunchedEffect(gesture) {
        when (gesture) {
            FaceGesture.None -> Unit
            FaceGesture.Nod -> {
                repeat(2) {
                    tilt.animateTo(target.tilt + 10f, tween(120))
                    tilt.animateTo(target.tilt - 4f, tween(120))
                }
                tilt.animateTo(target.tilt, PoseSpring)
            }
            FaceGesture.Shake -> {
                repeat(2) {
                    lookX.animateTo(0.55f, tween(100))
                    lookX.animateTo(-0.55f, tween(100))
                }
                lookX.animateTo(gazeX ?: target.lookX, PoseSpring)
            }
        }
    }

    val infinite = rememberInfiniteTransition(label = "immersive_eyes")
    val life by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "life",
    )
    val breath by infinite.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath",
    )

    LaunchedEffect(mood) {
        val speed = target.blinkSpeed.coerceIn(0.25f, 1.6f)
        while (isActive) {
            val base = when (mood) {
                AssistantMood.Drowsy, AssistantMood.Tired -> Random.nextLong(900, 1800)
                AssistantMood.Bored -> Random.nextLong(2800, 4800)
                AssistantMood.Excited, AssistantMood.Listening -> Random.nextLong(1800, 3200)
                else -> Random.nextLong(2200, 4000)
            }
            delay((base / speed).toLong().coerceAtLeast(400L))
            if (eyeStyle.value > 0.7f) continue
            val closeTo = when (mood) {
                AssistantMood.Drowsy -> 0.06f
                AssistantMood.Tired -> 0.08f
                else -> 0.12f
            }
            blink.animateTo(closeTo, tween((90 / speed).toInt().coerceAtLeast(40)))
            delay((50 / speed).toLong().coerceAtLeast(20L))
            blink.animateTo(1f, tween((140 / speed).toInt().coerceAtLeast(60)))
            if (mood == AssistantMood.Tired || mood == AssistantMood.Drowsy) {
                delay(120)
                blink.animateTo(closeTo * 1.4f, tween(80))
                delay(40)
                blink.animateTo(1f, tween(160))
            }
        }
    }

    LaunchedEffect(mood, externalGaze) {
        if (externalGaze) return@LaunchedEffect
        if (mood != AssistantMood.Reading &&
            mood != AssistantMood.Searching &&
            mood != AssistantMood.Bored
        ) {
            return@LaunchedEffect
        }
        while (isActive) {
            when (mood) {
                AssistantMood.Reading -> {
                    lookX.animateTo(0.38f, tween(700))
                    delay(100)
                    lookX.animateTo(-0.32f, tween(90))
                    delay(70)
                }
                AssistantMood.Searching -> {
                    lookX.animateTo(0.45f, tween(160))
                    lookX.animateTo(-0.4f, tween(200))
                    lookX.animateTo(0.08f, tween(140))
                    delay(50)
                }
                AssistantMood.Bored -> {
                    lookX.animateTo(0.5f, tween(1600, easing = FastOutSlowInEasing))
                    delay(600)
                    lookX.animateTo(-0.35f, tween(1800, easing = FastOutSlowInEasing))
                    delay(800)
                }
                else -> delay(500)
            }
        }
    }

    Canvas(modifier = modifier.aspectRatio(1.15f)) {
        val side = minOf(size.width, size.height)
        val cx = size.width * 0.5f
        val cy = size.height * 0.48f
        val r = side * 0.42f * breath
        val moodTint = mood.glowColor
        val aura = brandGlow
        val eyeFill = eyeFillForContrast(highContrast)
        val glow = faceGlow.value.coerceIn(0f, 1.2f)
        val bob = sin(life * 0.5f).toFloat() * r * 0.02f

        translate(top = bob) {
            // Soft face-shaped aura (elliptical bloom) — brand + mood tint
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        moodTint.copy(alpha = auraAlphaForContrast(highContrast, 0.28f) * glow),
                        aura.copy(alpha = auraAlphaForContrast(highContrast, 0.18f) * glow),
                        Color.Transparent,
                    ),
                    center = Offset(cx, cy + r * 0.05f),
                    radius = r * 1.45f,
                ),
                topLeft = Offset(cx - r * 1.15f, cy - r * 1.05f),
                size = Size(r * 2.3f, r * 2.2f),
            )
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = auraAlphaForContrast(highContrast, 0.08f) * glow),
                        Color.Transparent,
                    ),
                    center = Offset(cx, cy),
                    radius = r * 0.95f,
                ),
                topLeft = Offset(cx - r * 0.85f, cy - r * 0.75f),
                size = Size(r * 1.7f, r * 1.55f),
            )

            val liveTilt = tilt.value + 0.5f * sin(life * 0.32f).toFloat()
            rotate(liveTilt, pivot = Offset(cx, cy)) {
                val open = (eyeOpen.value * blink.value).coerceIn(0.05f, 1.4f)
                val eW = r * 0.14f * eyeWidth.value
                val eH = r * 0.2f * eyeHeight.value * open
                val gap = r * 0.28f * eyeGap.value
                val eyeY = cy - r * 0.06f + lookY.value * r * 0.14f
                val gaze = lookX.value * r * 0.08f
                val left = Offset(cx - gap + gaze, eyeY)
                val right = Offset(cx + gap + gaze, eyeY)
                val strokeBoost = if (highContrast) 1.15f else 1f

                drawImmersiveEye(
                    left, eW * strokeBoost, eH, eyeStyle.value, eyeFill, moodTint, glow,
                )
                drawImmersiveEye(
                    right, eW * strokeBoost, eH, eyeStyle.value, eyeFill, moodTint, glow,
                )

                val speaking = mouthAmplitude != null ||
                    mood == AssistantMood.Speaking ||
                    mood == AssistantMood.Excited
                if (mouthVisible.value > 0.08f || (mouthAmplitude != null && mouthAmplitude > 0.05f)) {
                    drawImmersiveMouth(
                        center = Offset(cx, cy + r * 0.38f),
                        faceR = r,
                        curve = mouthCurve.value,
                        open = mouthOpen.value,
                        visible = maxOf(mouthVisible.value, if (mouthAmplitude != null) 0.9f else 0f),
                        color = eyeFill,
                        speaking = speaking,
                        life = life,
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawImmersiveEye(
    center: Offset,
    width: Float,
    height: Float,
    style: Float,
    color: Color,
    glowColor: Color,
    glow: Float,
) {
    // Soft per-eye bloom
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                glowColor.copy(alpha = 0.35f * glow),
                Color.Transparent,
            ),
            center = center,
            radius = maxOf(width, height) * 2.4f,
        ),
        topLeft = Offset(center.x - width * 2.2f, center.y - height * 2.2f),
        size = Size(width * 4.4f, height * 4.4f),
    )

    when {
        style > 0.35f -> {
            val path = Path().apply {
                moveTo(center.x - width * 1.2f, center.y + height * 0.2f)
                quadraticTo(
                    center.x,
                    center.y - height * (0.5f + 0.5f * style),
                    center.x + width * 1.2f,
                    center.y + height * 0.2f,
                )
            }
            drawPath(
                path,
                color,
                style = Stroke(width = width * 0.75f, cap = StrokeCap.Round),
            )
        }
        style < -0.25f -> {
            val flatten = (-style).coerceIn(0.25f, 1f)
            val w = width * 1.45f
            val h = (height * (1f - 0.7f * flatten)).coerceAtLeast(width * 0.28f)
            drawRoundRect(
                color = color,
                topLeft = Offset(center.x - w, center.y - h * 0.5f),
                size = Size(w * 2f, h),
                cornerRadius = CornerRadius(h, h),
            )
        }
        else -> {
            // Elliptical eyes (wider than tall — not perfect circles)
            val w = width * 1.15f
            val h = height.coerceAtLeast(w * 0.55f)
            drawOval(
                color = color,
                topLeft = Offset(center.x - w, center.y - h),
                size = Size(w * 2f, h * 2f),
            )
            // Specular highlight
            drawOval(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(center.x - w * 0.55f, center.y - h * 0.75f),
                size = Size(w * 0.7f, h * 0.45f),
            )
        }
    }
}

private fun DrawScope.drawImmersiveMouth(
    center: Offset,
    faceR: Float,
    curve: Float,
    open: Float,
    visible: Float,
    color: Color,
    speaking: Boolean,
    life: Float,
) {
    val alpha = visible.coerceIn(0f, 1f)
    val halfW = faceR * 0.18f
    val smile = faceR * 0.08f * curve
    val openH = faceR * 0.075f * open.coerceIn(0f, 1f)
    val wobble = if (speaking) sin(life * 3.4f).toFloat() * faceR * 0.012f else 0f
    val tint = color.copy(alpha = 0.95f * alpha)

    if (openH > faceR * 0.018f) {
        val w = halfW * 1.15f
        val h = openH * 1.4f
        drawRoundRect(
            color = tint,
            topLeft = Offset(center.x - w * 0.5f, center.y - h * 0.3f + wobble),
            size = Size(w, h),
            cornerRadius = CornerRadius(w * 0.5f, h * 0.5f),
        )
    } else if (abs(curve) > 0.1f) {
        val path = Path().apply {
            val y0 = center.y + wobble
            moveTo(center.x - halfW, y0)
            quadraticTo(center.x, y0 + smile, center.x + halfW, y0)
        }
        drawPath(
            path,
            tint,
            style = Stroke(width = faceR * 0.038f, cap = StrokeCap.Round),
        )
    }
}
