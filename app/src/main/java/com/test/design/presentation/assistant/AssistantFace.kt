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
import kotlin.math.sin
import kotlin.random.Random

/**
 * Continuous face pose — eyes + mouth morph as one cute orb persona.
 *
 * Mouth: [mouthCurve] −1 frown … +1 smile; [mouthOpen] 0 closed … 1 talking.
 */
internal data class FacePose(
    val eyeOpen: Float = 1f,
    val eyeWidth: Float = 1f,
    val eyeHeight: Float = 1f,
    val eyeGap: Float = 1f,
    val tilt: Float = 2f,
    val borderGlow: Float = 0.85f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val blush: Float = 0.55f,
    val roundness: Float = 0.9f,
    val mouthCurve: Float = 0.45f,
    val mouthOpen: Float = 0.06f,
    val mouthWidth: Float = 1f,
)

internal fun AssistantMood.toFacePose(): FacePose = when (this) {
    AssistantMood.Idle -> FacePose(
        eyeOpen = 1f,
        tilt = 2f,
        borderGlow = 0.8f,
        blush = 0.55f,
        mouthCurve = 0.5f,
        mouthOpen = 0.04f,
        mouthWidth = 0.95f,
    )
    AssistantMood.Listening -> FacePose(
        eyeOpen = 1.12f,
        eyeWidth = 1.06f,
        eyeHeight = 1.08f,
        tilt = 1.5f,
        borderGlow = 1f,
        blush = 0.65f,
        mouthCurve = 0.2f,
        mouthOpen = 0.22f,
        mouthWidth = 0.8f,
    )
    AssistantMood.Speaking -> FacePose(
        eyeOpen = 1f,
        eyeHeight = 0.98f,
        tilt = 3f,
        borderGlow = 0.95f,
        blush = 0.6f,
        mouthCurve = 0.55f,
        mouthOpen = 0.58f,
        mouthWidth = 1.05f,
    )
    AssistantMood.Thinking -> FacePose(
        eyeOpen = 0.9f,
        eyeHeight = 0.88f,
        lookX = 0.28f,
        lookY = -0.18f,
        tilt = 5f,
        borderGlow = 0.85f,
        blush = 0.4f,
        mouthCurve = 0.05f,
        mouthOpen = 0.03f,
        mouthWidth = 0.72f,
    )
    AssistantMood.Happy -> FacePose(
        eyeOpen = 0.42f,
        eyeWidth = 1.2f,
        eyeHeight = 0.4f,
        tilt = 1f,
        borderGlow = 1f,
        blush = 0.9f,
        mouthCurve = 1f,
        mouthOpen = 0.08f,
        mouthWidth = 1.22f,
    )
    AssistantMood.Sad -> FacePose(
        eyeOpen = 0.78f,
        eyeHeight = 0.72f,
        lookY = 0.2f,
        tilt = 6f,
        borderGlow = 0.65f,
        blush = 0.35f,
        mouthCurve = -0.8f,
        mouthOpen = 0.04f,
        mouthWidth = 0.82f,
    )
    AssistantMood.Reading -> FacePose(
        eyeOpen = 0.95f,
        lookX = 0.32f,
        tilt = 2.5f,
        borderGlow = 0.8f,
        blush = 0.45f,
        mouthCurve = 0.15f,
        mouthOpen = 0.02f,
        mouthWidth = 0.7f,
    )
    AssistantMood.Searching -> FacePose(
        eyeOpen = 1.1f,
        eyeWidth = 1.05f,
        tilt = 2.5f,
        borderGlow = 1f,
        blush = 0.55f,
        mouthCurve = 0.3f,
        mouthOpen = 0.16f,
        mouthWidth = 0.88f,
    )
}

private val PoseSpring = spring<Float>(
    dampingRatio = 0.78f,
    stiffness = Spring.StiffnessMediumLow,
)

// Soft pastel body — friendly mascot, not a hollow shell
private val BodyTop = Color(0xFFFFF4EC)
private val BodyMid = Color(0xFFFFE4F0)
private val BodyBottom = Color(0xFFE8D8FF)
private val BodyRim = Color(0xFFFFFFFF)
private val IrisDeep = Color(0xFF2C2A3A)
private val MouthInk = Color(0xFF3A3348)

/**
 * Redesigned persona: soft pastel orb with glossy cartoon eyes + expressive mouth.
 */
@Composable
fun AssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    faceColor: Color = Color.Unspecified,
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
    val mouthCurve = remember { Animatable(target.mouthCurve) }
    val mouthOpen = remember { Animatable(target.mouthOpen) }
    val mouthWidth = remember { Animatable(target.mouthWidth) }
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
        launch { mouthCurve.animateTo(target.mouthCurve, PoseSpring) }
        launch { mouthWidth.animateTo(target.mouthWidth, PoseSpring) }
        if (mood != AssistantMood.Speaking) {
            launch { mouthOpen.animateTo(target.mouthOpen, PoseSpring) }
        }
        launch {
            propVisibility.animateTo(
                if (mood == AssistantMood.Idle) 0f else 1f,
                tween(380, easing = FastOutSlowInEasing),
            )
        }
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) {
            launch { lookX.animateTo(target.lookX, PoseSpring) }
        }
        launch { lookY.animateTo(target.lookY, PoseSpring) }
    }

    LaunchedEffect(mood) {
        if (mood != AssistantMood.Speaking) return@LaunchedEffect
        while (isActive) {
            mouthOpen.animateTo(
                Random.nextFloat() * 0.4f + 0.38f,
                tween(Random.nextInt(90, 150), easing = FastOutSlowInEasing),
            )
            mouthOpen.animateTo(
                Random.nextFloat() * 0.14f + 0.06f,
                tween(Random.nextInt(70, 120), easing = FastOutSlowInEasing),
            )
        }
    }

    val infinite = rememberInfiniteTransition(label = "persona_orb")
    val life by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "life",
    )
    val breath by infinite.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath",
    )

    LaunchedEffect(mood) {
        while (isActive) {
            val wait = when (mood) {
                AssistantMood.Listening -> Random.nextLong(1800, 3200)
                AssistantMood.Searching -> Random.nextLong(1000, 1700)
                AssistantMood.Happy -> Random.nextLong(2600, 4200)
                AssistantMood.Sad -> Random.nextLong(3200, 5200)
                else -> Random.nextLong(2200, 4000)
            }
            delay(wait)
            val closeTo = if (mood == AssistantMood.Happy) 0.35f else 0.08f
            blink.animateTo(closeTo, tween(80, easing = FastOutSlowInEasing))
            delay(45)
            blink.animateTo(1f, tween(140, easing = FastOutSlowInEasing))
            if (Random.nextFloat() < 0.28f) {
                delay(100)
                blink.animateTo(closeTo, tween(60))
                delay(35)
                blink.animateTo(1f, tween(120))
            }
        }
    }

    LaunchedEffect(mood) {
        if (mood != AssistantMood.Reading && mood != AssistantMood.Searching) return@LaunchedEffect
        while (isActive) {
            if (mood == AssistantMood.Reading) {
                lookX.animateTo(0.38f, tween(850, easing = FastOutSlowInEasing))
                delay(140)
                lookX.animateTo(-0.32f, tween(110, easing = FastOutSlowInEasing))
                delay(90)
            } else {
                lookX.animateTo(0.4f, tween(200, easing = FastOutSlowInEasing))
                lookX.animateTo(-0.38f, tween(240, easing = FastOutSlowInEasing))
                lookX.animateTo(0.06f, tween(160, easing = FastOutSlowInEasing))
                delay(70)
            }
        }
    }

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val side = minOf(size.width, size.height)
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val r = side * 0.36f * breath
        val moodTint = mood.glowColor

        val bobY = sin(life * 0.65f).toFloat() * r * 0.04f
        translate(top = bobY) {
            // Soft floor shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.18f),
                        Color.Transparent,
                    ),
                    center = Offset(cx, cy + r * 1.05f),
                    radius = r * 0.85f,
                ),
                topLeft = Offset(cx - r * 0.85f, cy + r * 0.78f),
                size = Size(r * 1.7f, r * 0.45f),
            )

            drawMoodProp(
                mood = mood,
                center = Offset(cx, cy),
                shell = r * 2f,
                visibility = propVisibility.value,
                life = life,
            )

            val liveTilt = tilt.value + 0.7f * sin(life * 0.4f).toFloat()
            rotate(liveTilt, pivot = Offset(cx, cy)) {
                // Mood glow halo
                val glowR = r * (1.55f + 0.2f * borderGlow.value)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            moodTint.copy(alpha = 0.45f * borderGlow.value),
                            moodTint.copy(alpha = 0.16f * borderGlow.value),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = glowR,
                    ),
                    radius = glowR,
                    center = Offset(cx, cy),
                )

                // Pastel orb body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BodyTop,
                            BodyMid,
                            BodyBottom,
                        ),
                        center = Offset(cx - r * 0.22f, cy - r * 0.28f),
                        radius = r * 1.35f,
                    ),
                    radius = r,
                    center = Offset(cx, cy),
                )
                // Specular sheen
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BodyRim.copy(alpha = 0.7f),
                            BodyRim.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                        center = Offset(cx - r * 0.28f, cy - r * 0.35f),
                        radius = r * 0.55f,
                    ),
                    radius = r * 0.55f,
                    center = Offset(cx - r * 0.28f, cy - r * 0.35f),
                )
                // Soft rim light tinted by mood
                drawCircle(
                    color = moodTint.copy(alpha = 0.22f * borderGlow.value),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = r * 0.045f),
                )
                drawCircle(
                    color = BodyRim.copy(alpha = 0.55f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = r * 0.018f),
                )

                val open = (eyeOpen.value * blink.value).coerceIn(0.06f, 1.25f)
                val eW = r * 0.22f * eyeWidth.value
                val eH = r * 0.28f * eyeHeight.value * open
                val gap = r * 0.28f * eyeGap.value
                val eyeY = cy - r * 0.08f + lookY.value * r * 0.12f
                val gaze = lookX.value * r * 0.08f +
                    if (mood == AssistantMood.Thinking) 0.03f * r * sin(life) else 0f

                val leftEye = Offset(cx - gap, eyeY)
                val rightEye = Offset(cx + gap, eyeY)

                // Blush
                if (blush.value > 0.05f) {
                    val blushA = 0.38f * blush.value
                    val blushR = r * 0.14f
                    drawCircle(
                        Color(0xFFFF8FA8).copy(alpha = blushA),
                        blushR,
                        Offset(cx - r * 0.48f, cy + r * 0.18f),
                    )
                    drawCircle(
                        Color(0xFFFF8FA8).copy(alpha = blushA),
                        blushR,
                        Offset(cx + r * 0.48f, cy + r * 0.18f),
                    )
                }

                drawGlossyEye(
                    center = leftEye,
                    width = eW,
                    height = eH,
                    iris = moodTint,
                    happySquint = mood == AssistantMood.Happy,
                )
                drawGlossyEye(
                    center = rightEye,
                    width = eW,
                    height = eH,
                    iris = moodTint,
                    happySquint = mood == AssistantMood.Happy,
                )

                drawCuteMouth(
                    center = Offset(cx, cy + r * 0.38f),
                    faceR = r,
                    curve = mouthCurve.value,
                    open = mouthOpen.value,
                    widthScale = mouthWidth.value,
                    life = life,
                    speaking = mood == AssistantMood.Speaking,
                )
            }
        }
    }
}

private fun DrawScope.drawGlossyEye(
    center: Offset,
    width: Float,
    height: Float,
    iris: Color,
    happySquint: Boolean,
) {
    if (height < width * 0.18f || happySquint) {
        // Happy crescent eyes
        val path = Path().apply {
            moveTo(center.x - width, center.y)
            quadraticTo(center.x, center.y + height * 1.6f, center.x + width, center.y)
        }
        drawPath(
            path,
            MouthInk.copy(alpha = 0.85f),
            style = Stroke(width = width * 0.28f, cap = StrokeCap.Round),
        )
        return
    }

    // Soft white sclera
    drawOval(
        color = Color.White,
        topLeft = Offset(center.x - width, center.y - height),
        size = Size(width * 2f, height * 2f),
    )
    // Iris
    val irisR = minOf(width, height) * 0.72f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                iris.copy(alpha = 0.95f),
                iris,
                IrisDeep.copy(alpha = 0.9f),
            ),
            center = Offset(center.x - irisR * 0.1f, center.y - irisR * 0.15f),
            radius = irisR,
        ),
        radius = irisR,
        center = center,
    )
    // Pupil
    drawCircle(
        color = IrisDeep,
        radius = irisR * 0.42f,
        center = center,
    )
    // Twin catch lights
    drawCircle(
        color = Color.White.copy(alpha = 0.95f),
        radius = irisR * 0.28f,
        center = Offset(center.x - irisR * 0.32f, center.y - irisR * 0.35f),
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.55f),
        radius = irisR * 0.12f,
        center = Offset(center.x + irisR * 0.22f, center.y + irisR * 0.18f),
    )
}

private fun DrawScope.drawCuteMouth(
    center: Offset,
    faceR: Float,
    curve: Float,
    open: Float,
    widthScale: Float,
    life: Float,
    speaking: Boolean,
) {
    val halfW = faceR * 0.22f * widthScale
    val smileLift = faceR * 0.1f * curve
    val openH = faceR * 0.1f * open.coerceIn(0f, 1f)
    val wobble = if (speaking) sin(life * 3.4f).toFloat() * faceR * 0.012f else 0f

    if (openH > faceR * 0.02f) {
        val w = halfW * 1.35f
        val h = openH * 1.4f + smileLift.coerceAtLeast(0f) * 0.2f
        val left = center.x - w * 0.5f
        val top = center.y - h * 0.35f + wobble
        val rr = CornerRadius(w * 0.5f, h * 0.5f)
        drawRoundRect(
            color = MouthInk,
            topLeft = Offset(left, top),
            size = Size(w, h),
            cornerRadius = rr,
        )
        // Soft tongue hint when open
        if (open > 0.35f) {
            drawCircle(
                color = Color(0xFFFF8FA8).copy(alpha = 0.55f),
                radius = h * 0.28f,
                center = Offset(center.x, top + h * 0.72f),
            )
        }
    } else {
        val path = Path().apply {
            val y0 = center.y + wobble
            moveTo(center.x - halfW, y0)
            quadraticTo(center.x, y0 + smileLift, center.x + halfW, y0)
        }
        drawPath(
            path,
            MouthInk.copy(alpha = 0.9f),
            style = Stroke(width = faceR * 0.045f, cap = StrokeCap.Round),
        )
    }
}
