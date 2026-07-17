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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
 * Continuous face pose — eyes + mouth morph together as one persona.
 *
 * Mouth: [mouthCurve] −1 frown … +1 smile; [mouthOpen] 0 closed … 1 talking.
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
    val roundness: Float = 0.78f,
    val mouthCurve: Float = 0.35f,
    val mouthOpen: Float = 0.08f,
    val mouthWidth: Float = 1f,
)

internal fun AssistantMood.toFacePose(): FacePose = when (this) {
    AssistantMood.Idle -> FacePose(
        eyeOpen = 1f,
        tilt = 3.5f,
        borderGlow = 0.88f,
        blush = 0.45f,
        roundness = 0.82f,
        mouthCurve = 0.4f,
        mouthOpen = 0.06f,
        mouthWidth = 0.92f,
    )
    AssistantMood.Listening -> FacePose(
        eyeOpen = 1.12f,
        eyeWidth = 1.08f,
        eyeHeight = 1.06f,
        tilt = 2.5f,
        borderGlow = 1f,
        blush = 0.55f,
        roundness = 0.85f,
        mouthCurve = 0.15f,
        mouthOpen = 0.28f,
        mouthWidth = 0.78f,
    )
    AssistantMood.Speaking -> FacePose(
        eyeOpen = 0.98f,
        eyeHeight = 0.96f,
        tilt = 5f,
        borderGlow = 0.95f,
        blush = 0.5f,
        roundness = 0.8f,
        mouthCurve = 0.45f,
        mouthOpen = 0.55f,
        mouthWidth = 1.05f,
    )
    AssistantMood.Thinking -> FacePose(
        eyeOpen = 0.88f,
        eyeHeight = 0.84f,
        lookX = 0.22f,
        lookY = -0.22f,
        tilt = 6f,
        borderGlow = 0.9f,
        blush = 0.35f,
        roundness = 0.78f,
        mouthCurve = -0.08f,
        mouthOpen = 0.04f,
        mouthWidth = 0.7f,
    )
    AssistantMood.Happy -> FacePose(
        eyeOpen = 0.58f,
        eyeWidth = 1.14f,
        eyeHeight = 0.52f,
        tilt = 2f,
        borderGlow = 1f,
        blush = 0.85f,
        roundness = 0.92f,
        mouthCurve = 0.95f,
        mouthOpen = 0.12f,
        mouthWidth = 1.18f,
    )
    AssistantMood.Sad -> FacePose(
        eyeOpen = 0.72f,
        eyeHeight = 0.68f,
        lookY = 0.18f,
        tilt = 7f,
        borderGlow = 0.7f,
        blush = 0.28f,
        roundness = 0.76f,
        mouthCurve = -0.75f,
        mouthOpen = 0.05f,
        mouthWidth = 0.85f,
    )
    AssistantMood.Reading -> FacePose(
        eyeOpen = 0.94f,
        lookX = 0.3f,
        tilt = 3.5f,
        borderGlow = 0.85f,
        blush = 0.4f,
        roundness = 0.8f,
        mouthCurve = 0.1f,
        mouthOpen = 0.03f,
        mouthWidth = 0.72f,
    )
    AssistantMood.Searching -> FacePose(
        eyeOpen = 1.08f,
        eyeWidth = 1.06f,
        tilt = 4f,
        borderGlow = 1f,
        blush = 0.48f,
        roundness = 0.84f,
        mouthCurve = 0.25f,
        mouthOpen = 0.18f,
        mouthWidth = 0.88f,
    )
}

private val PoseSpring = spring<Float>(
    dampingRatio = 0.72f,
    stiffness = Spring.StiffnessMediumLow,
)

private val ShellCore = Color(0xFF1B1830)
private val ShellWarm = Color(0xFF2A2240)
private val ShellBorder = Color(0xFFFFF6F0)
private val EyeCream = Color(0xFFFFF8F2)
private val AccentWarm = Color(0xFFFF8FA3)
private val AccentCool = Color(0xFFB8D4FF)
private val MouthFill = Color(0xFF2A1E28)
private val MouthLip = Color(0xFFFFD0D8)

/**
 * Cute round persona — warm glow, big eyes, soft mouth (not a hollow ghost).
 */
@Composable
fun AssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    faceColor: Color = EyeCream,
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
        launch { roundness.animateTo(target.roundness, PoseSpring) }
        launch { mouthCurve.animateTo(target.mouthCurve, PoseSpring) }
        launch { mouthWidth.animateTo(target.mouthWidth, PoseSpring) }
        if (mood != AssistantMood.Speaking) {
            launch { mouthOpen.animateTo(target.mouthOpen, PoseSpring) }
        }
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

    // Talking mouth — soft phoneme-like open/close while Speaking
    LaunchedEffect(mood) {
        if (mood != AssistantMood.Speaking) return@LaunchedEffect
        while (isActive) {
            mouthOpen.animateTo(
                Random.nextFloat() * 0.45f + 0.35f,
                tween(Random.nextInt(90, 160), easing = FastOutSlowInEasing),
            )
            mouthOpen.animateTo(
                Random.nextFloat() * 0.18f + 0.08f,
                tween(Random.nextInt(70, 130), easing = FastOutSlowInEasing),
            )
        }
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

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val side = minOf(size.width, size.height)
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val shell = side * 0.68f
        val corner = shell * 0.48f
        val border = shell * 0.042f
        val moodGlow = mood.glowColor

        val bobY = sin(life * 0.7f).toFloat() * shell * 0.02f
        translate(top = bobY) {
            val liveTilt = tilt.value + 0.9f * sin(life * 0.45f).toFloat()

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

                // Outer mood glow halo
                val outerGlow = shell * 0.82f * pulse * (0.75f + 0.25f * borderGlow.value)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            moodGlow.copy(alpha = 0.42f * borderGlow.value),
                            moodGlow.copy(alpha = 0.14f * borderGlow.value),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = outerGlow,
                    ),
                    radius = outerGlow,
                    center = Offset(cx, cy),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentWarm.copy(alpha = 0.22f * borderGlow.value),
                            AccentCool.copy(alpha = 0.12f * borderGlow.value),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = shell * 0.7f * pulse,
                    ),
                    radius = shell * 0.7f * pulse,
                    center = Offset(cx, cy),
                )

                // Warm filled body — soft character, not a black void
                drawPath(
                    path = shellPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ShellWarm,
                            ShellCore,
                            Color(0xFF12101C),
                        ),
                        center = Offset(cx - shell * 0.08f, cy - shell * 0.12f),
                        radius = shell * 0.75f,
                    ),
                )

                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ShellBorder.copy(alpha = 0.95f),
                            moodGlow.copy(alpha = 0.85f),
                            AccentWarm.copy(alpha = 0.75f),
                            ShellBorder.copy(alpha = 0.9f),
                        ),
                        start = Offset(left, top + shell),
                        end = Offset(left + shell, top),
                    ),
                    topLeft = Offset(left, top),
                    size = Size(shell, shell),
                    cornerRadius = CornerRadius(corner, corner),
                    style = Stroke(width = border * (1f + 0.12f * borderGlow.value * pulse)),
                )

                clipPath(shellPath) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentWarm.copy(alpha = 0.28f),
                                Color.Transparent,
                            ),
                            center = Offset(cx + shell * 0.18f, cy - shell * 0.2f),
                            radius = shell * 0.4f,
                        ),
                        radius = shell * 0.4f,
                        center = Offset(cx + shell * 0.18f, cy - shell * 0.2f),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                moodGlow.copy(alpha = 0.16f),
                                Color.Transparent,
                            ),
                            center = Offset(cx, cy + shell * 0.1f),
                            radius = shell * 0.55f,
                        ),
                        radius = shell * 0.55f,
                        center = Offset(cx, cy + shell * 0.1f),
                    )
                }

                val open = (eyeOpen.value * blink.value).coerceIn(0.08f, 1.25f)
                // Bigger cuter eyes
                val eW = shell * 0.145f * eyeWidth.value
                val eH = shell * 0.185f * eyeHeight.value * open
                val gap = shell * 0.115f * eyeGap.value
                val eyeY = cy - shell * 0.05f + lookY.value * shell * 0.08f
                val gaze = lookX.value * shell * 0.04f +
                    if (mood == AssistantMood.Thinking) 0.018f * shell * sin(life) else 0f

                val leftEye = Offset(cx - gap - eW * 0.5f + gaze, eyeY)
                val rightEye = Offset(cx + gap + eW * 0.5f + gaze, eyeY)

                drawCheekBlush(
                    left = Offset(cx - shell * 0.3f, cy + shell * 0.12f),
                    right = Offset(cx + shell * 0.3f, cy + shell * 0.12f),
                    radius = shell * 0.09f,
                    amount = blush.value,
                )

                drawCircle(faceColor.copy(alpha = 0.28f * open), eW * 1.45f, leftEye)
                drawCircle(faceColor.copy(alpha = 0.28f * open), eW * 1.45f, rightEye)

                drawScanlineEye(
                    center = leftEye,
                    width = eW,
                    height = eH,
                    color = faceColor,
                    scanPhase = scan,
                    roundness = roundness.value,
                    glow = moodGlow,
                )
                drawScanlineEye(
                    center = rightEye,
                    width = eW,
                    height = eH,
                    color = faceColor,
                    scanPhase = scan + 0.12f,
                    roundness = roundness.value,
                    glow = moodGlow,
                )

                drawPersonaMouth(
                    center = Offset(cx, cy + shell * 0.24f),
                    shell = shell,
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

/**
 * Soft expressive mouth — smile / frown arcs, and an oval when open (talking).
 */
private fun DrawScope.drawPersonaMouth(
    center: Offset,
    shell: Float,
    curve: Float,
    open: Float,
    widthScale: Float,
    life: Float,
    speaking: Boolean,
) {
    val halfW = shell * 0.14f * widthScale
    val smileLift = shell * 0.07f * curve
    val openH = shell * 0.055f * open.coerceIn(0f, 1f)
    val talkWobble = if (speaking) {
        sin(life * 3.2f).toFloat() * shell * 0.008f
    } else {
        0f
    }

    if (openH > shell * 0.012f) {
        // Open mouth — rounded capsule that breathes while speaking
        val left = center.x - halfW * 0.72f
        val top = center.y - openH * 0.35f + talkWobble
        val w = halfW * 1.44f
        val h = openH * 1.55f + smileLift.coerceAtLeast(0f) * 0.25f
        val rr = CornerRadius(w * 0.45f, h * 0.5f)
        drawRoundRect(
            color = MouthFill,
            topLeft = Offset(left, top),
            size = Size(w, h),
            cornerRadius = rr,
        )
        drawRoundRect(
            color = MouthLip.copy(alpha = 0.55f),
            topLeft = Offset(left, top),
            size = Size(w, h),
            cornerRadius = rr,
            style = Stroke(width = shell * 0.012f, cap = StrokeCap.Round),
        )
        // Soft inner highlight for life
        drawCircle(
            color = Color.White.copy(alpha = 0.12f),
            radius = h * 0.22f,
            center = Offset(center.x - w * 0.12f, top + h * 0.35f),
        )
    } else {
        // Closed mouth — single expressive stroke (smile / neutral / frown)
        val path = Path().apply {
            val y0 = center.y + talkWobble
            moveTo(center.x - halfW, y0)
            quadraticTo(
                center.x,
                y0 + smileLift,
                center.x + halfW,
                y0,
            )
        }
        drawPath(
            path = path,
            color = MouthLip.copy(alpha = 0.92f),
            style = Stroke(
                width = shell * 0.028f,
                cap = StrokeCap.Round,
            ),
        )
    }
}

private fun DrawScope.drawScanlineEye(
    center: Offset,
    width: Float,
    height: Float,
    color: Color,
    scanPhase: Float,
    roundness: Float,
    glow: Color,
) {
    val left = center.x - width
    val top = center.y - height
    val eyeW = width * 2f
    val eyeH = height * 2f
    val rx = width * (0.55f + roundness * 0.45f)
    val ry = height * (0.55f + roundness * 0.45f)
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
            brush = Brush.verticalGradient(
                colors = listOf(
                    color,
                    color.copy(alpha = 0.92f),
                    glow.copy(alpha = 0.35f),
                ),
            ),
            topLeft = Offset(left, top),
            size = Size(eyeW, eyeH),
            cornerRadius = radius,
        )
        // Soft shimmer bands — cute, not CRT ghost
        val lineGap = (eyeH / 7f).coerceAtLeast(2f)
        var y = top + lineGap * 0.5f
        var i = 0
        while (y < top + eyeH) {
            val shimmer = 0.03f + 0.05f * ((sin((scanPhase + i * 0.12f) * PI * 2).toFloat() + 1f) * 0.5f)
            drawLine(
                color = Color(0xFF1B1830).copy(alpha = shimmer),
                start = Offset(left, y),
                end = Offset(left + eyeW, y),
                strokeWidth = lineGap * 0.22f,
            )
            y += lineGap
            i++
        }
        // Catch light
        drawCircle(
            color = Color.White.copy(alpha = 0.55f),
            radius = minOf(eyeW, eyeH) * 0.14f,
            center = Offset(center.x - eyeW * 0.18f, center.y - eyeH * 0.22f),
        )
    }
}
