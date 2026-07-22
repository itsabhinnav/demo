package com.test.design.presentation.assistant

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

val EporoShell = Color(0xFFF7F7F8)
val EporoShellShade = Color(0xFFE7E7E7)
val EporoVisor = Color(0xFF060606)
val EporoGlow = Color(0xFF9A7DFF)
val EporoGlowSoft = Color(0xFFB8A6FF)

private val EporoPoseSpring = spring<Float>(
    dampingRatio = 0.82f,
    stiffness = Spring.StiffnessLow,
)

/** True horizontal ellipse clip for the black visor / Dynamic Island. */
private val EporoEllipseShape = GenericShape { size, _ ->
    addOval(Rect(Offset.Zero, size))
}


/**
 * EPORO — white oval shell + horizontal elliptical black visor (Dynamic Island).
 *
 * Eyes keep the hollow ring look but morph width/height/gap/gaze like
 * [ImmersiveEyesFace]. The island can host any [islandContent] (waveform,
 * status, icons, …); default is the morphing ring eyes.
 */
@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
fun EporoAssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    shellColor: Color = EporoShell,
    visorColor: Color = EporoVisor,
    glowColor: Color = EporoGlow,
    brandGlow: Color = EporoGlowSoft,
    gazeX: Float? = null,
    gazeY: Float? = null,
    mouthAmplitude: Float? = null,
    gesture: FaceGesture = FaceGesture.None,
    islandContent: (@Composable BoxScope.() -> Unit)? = null,
) {
    val eyePose = mood.toEporoEyePose()
    val islandPose = mood.toEporoIslandPose()
    val shellMorph = remember {
        ExpressiveShellMorphState(
            morph = Morph(
                start = ExpressiveShellKind.Oval.toRoundedPolygon(),
                end = ExpressiveShellKind.Oval.toRoundedPolygon(),
            ),
            progress = 1f,
        )
    }

    val eyeOpen = remember { Animatable(eyePose.eyeOpen) }
    val eyeWidth = remember { Animatable(eyePose.eyeWidth) }
    val eyeHeight = remember { Animatable(eyePose.eyeHeight) }
    val eyeGap = remember { Animatable(eyePose.eyeGap) }
    val lookX = remember { Animatable(eyePose.lookX) }
    val lookY = remember { Animatable(eyePose.lookY) }
    val tilt = remember { Animatable(eyePose.tilt) }
    val ringPulse = remember { Animatable(eyePose.ringPulse) }
    val islandW = remember { Animatable(islandPose.widthFrac) }
    val islandH = remember { Animatable(islandPose.heightFrac) }
    val blink = remember { Animatable(1f) }

    val externalGaze = gazeX != null || gazeY != null
    val activeGlow = remember(glowColor, brandGlow) {
        lerpEporoGlow(glowColor, brandGlow, 0.28f)
    }

    LaunchedEffect(mood) {
        val e = mood.toEporoEyePose()
        val i = mood.toEporoIslandPose()
        launch { eyeOpen.animateTo(e.eyeOpen, EporoPoseSpring) }
        launch { eyeWidth.animateTo(e.eyeWidth, EporoPoseSpring) }
        launch { eyeHeight.animateTo(e.eyeHeight, EporoPoseSpring) }
        launch { eyeGap.animateTo(e.eyeGap, EporoPoseSpring) }
        launch { lookY.animateTo(gazeY ?: e.lookY, EporoPoseSpring) }
        launch { tilt.animateTo(e.tilt, EporoPoseSpring) }
        launch { ringPulse.animateTo(e.ringPulse, EporoPoseSpring) }
        launch { islandW.animateTo(i.widthFrac, EporoPoseSpring) }
        launch { islandH.animateTo(i.heightFrac, EporoPoseSpring) }
        if (!externalGaze &&
            mood != AssistantMood.Reading &&
            mood != AssistantMood.Searching &&
            mood != AssistantMood.Bored
        ) {
            launch { lookX.animateTo(e.lookX, EporoPoseSpring) }
        }
    }

    LaunchedEffect(gazeX, gazeY) {
        if (gazeX != null) lookX.animateTo(gazeX, EporoPoseSpring)
        if (gazeY != null) lookY.animateTo(gazeY, EporoPoseSpring)
    }

    LaunchedEffect(gesture) {
        val e = mood.toEporoEyePose()
        when (gesture) {
            FaceGesture.None -> Unit
            FaceGesture.Nod -> {
                repeat(2) {
                    tilt.animateTo(e.tilt + 10f, tween(120))
                    tilt.animateTo(e.tilt - 4f, tween(120))
                }
                tilt.animateTo(e.tilt, EporoPoseSpring)
            }
            FaceGesture.Shake -> {
                repeat(2) {
                    lookX.animateTo(0.55f, tween(100))
                    lookX.animateTo(-0.55f, tween(100))
                }
                lookX.animateTo(gazeX ?: e.lookX, EporoPoseSpring)
            }
        }
    }

    LaunchedEffect(mood) {
        val speed = mood.toEporoEyePose().blinkSpeed.coerceIn(0.25f, 1.6f)
        while (isActive) {
            val base = when (mood) {
                AssistantMood.Drowsy, AssistantMood.Tired -> Random.nextLong(900, 1_800)
                AssistantMood.Bored -> Random.nextLong(2_800, 4_800)
                AssistantMood.Excited, AssistantMood.Listening -> Random.nextLong(1_800, 3_200)
                else -> Random.nextLong(2_200, 4_000)
            }
            kotlinx.coroutines.delay((base / speed).toLong().coerceAtLeast(400L))
            if (Random.nextFloat() > 0.78f) continue
            val closeTo = when (mood) {
                AssistantMood.Drowsy -> 0.06f
                AssistantMood.Tired -> 0.08f
                else -> 0.12f
            }
            blink.animateTo(closeTo, tween((90 / speed).toInt().coerceAtLeast(40)))
            kotlinx.coroutines.delay((50 / speed).toLong().coerceAtLeast(20L))
            blink.animateTo(1f, tween((140 / speed).toInt().coerceAtLeast(60)))
            if (mood == AssistantMood.Tired || mood == AssistantMood.Drowsy) {
                kotlinx.coroutines.delay(120)
                blink.animateTo(closeTo * 1.4f, tween(80))
                kotlinx.coroutines.delay(40)
                blink.animateTo(1f, tween(160))
            } else if (Random.nextFloat() < 0.22f) {
                kotlinx.coroutines.delay(90)
                blink.animateTo(0.12f, tween(60))
                blink.animateTo(1f, tween(100))
            }
        }
    }

    // Built-in gaze wander when no cabin override — mirrors ImmersiveEyesFace.
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
                    kotlinx.coroutines.delay(100)
                    lookX.animateTo(-0.32f, tween(90))
                    kotlinx.coroutines.delay(70)
                }
                AssistantMood.Searching -> {
                    lookX.animateTo(0.45f, tween(160))
                    lookX.animateTo(-0.4f, tween(200))
                    lookX.animateTo(0.08f, tween(140))
                    kotlinx.coroutines.delay(50)
                }
                AssistantMood.Bored -> {
                    lookX.animateTo(0.5f, tween(1_600, easing = FastOutSlowInEasing))
                    kotlinx.coroutines.delay(600)
                    lookX.animateTo(-0.35f, tween(1_800, easing = FastOutSlowInEasing))
                    kotlinx.coroutines.delay(800)
                }
                else -> kotlinx.coroutines.delay(500)
            }
        }
    }

    val breath = rememberInfiniteTransition(label = "eporo_breath")
    val glowPhase by breath.animateFloat(
        initialValue = 0.88f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (mood) {
                    AssistantMood.Listening, AssistantMood.Speaking -> 900
                    AssistantMood.Searching, AssistantMood.Thinking -> 1_400
                    else -> 2_200
                },
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "eporo_glow",
    )
    val shellBreath by breath.animateFloat(
        initialValue = 0.988f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "eporo_shell_breath",
    )
    val idleBob by breath.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "eporo_idle_bob",
    )

    Box(
        modifier = modifier
            .aspectRatio(1.15f)
            .graphicsLayer {
                val s = shellBreath
                scaleX = s
                scaleY = s
                translationY = idleBob * 3.2f
            },
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                val cx = size.width * 0.5f
                rotate(degrees = tilt.value, pivot = Offset(cx, size.height * 0.48f)) {
                    drawEporoContactShadow()
                    drawEporoHead(shellMorph = shellMorph, shellColor = shellColor)
                    drawEporoShellHighlights()
                }
            },
        )

        // Dynamic Island — sits slightly high on the shell (robot visor).
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .graphicsLayer {
                    rotationZ = tilt.value
                    translationY = -size.height * 0.04f
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(islandW.value.coerceIn(0.58f, 0.92f))
                    .fillMaxHeight(islandH.value.coerceIn(0.22f, 0.48f))
                    .clip(EporoEllipseShape)
                    .background(visorColor),
                contentAlignment = Alignment.Center,
            ) {
                // Glass sheen + bottom shade inside the island.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.16f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.32f),
                                ),
                            ),
                        ),
                )
                // Soft brand rim wash along the island edge.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    activeGlow.copy(
                                        alpha = 0.10f + 0.12f * ringPulse.value * glowPhase,
                                    ),
                                ),
                            ),
                        ),
                )
                // Top specular pill on the black glass.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 5.dp)
                        .fillMaxWidth(0.22f)
                        .fillMaxHeight(0.09f)
                        .clip(EporoEllipseShape)
                        .background(Color.White.copy(alpha = 0.42f)),
                )
                if (islandContent != null) {
                    islandContent()
                } else {
                    EporoIslandEyes(
                        mood = mood,
                        glowColor = activeGlow,
                        eyeOpen = eyeOpen.value * blink.value,
                        eyeWidth = eyeWidth.value,
                        eyeHeight = eyeHeight.value,
                        eyeGap = eyeGap.value,
                        lookX = lookX.value,
                        lookY = lookY.value,
                        glowPhase = glowPhase,
                        ringPulse = ringPulse.value,
                        mouthAmplitude = mouthAmplitude,
                        islandTallEnough = islandH.value >= 0.30f,
                    )
                }
            }
        }
    }
}

/** Default Dynamic Island content — morphing hollow ring eyes. */
@Composable
private fun EporoIslandEyes(
    mood: AssistantMood,
    glowColor: Color,
    eyeOpen: Float,
    eyeWidth: Float,
    eyeHeight: Float,
    eyeGap: Float,
    lookX: Float,
    lookY: Float,
    glowPhase: Float,
    ringPulse: Float,
    mouthAmplitude: Float?,
    islandTallEnough: Boolean,
) {
    val mouthOpen = remember { Animatable(0f) }
    LaunchedEffect(mouthAmplitude, mood) {
        if (mouthAmplitude != null) {
            mouthOpen.snapTo(mouthAmplitude.coerceIn(0f, 1f))
            return@LaunchedEffect
        }
        if (mood != AssistantMood.Speaking && mood != AssistantMood.Excited) {
            mouthOpen.animateTo(0f, EporoPoseSpring)
            return@LaunchedEffect
        }
        while (isActive) {
            mouthOpen.animateTo(Random.nextFloat() * 0.4f + 0.3f, tween(Random.nextInt(70, 130)))
            mouthOpen.animateTo(Random.nextFloat() * 0.12f + 0.04f, tween(Random.nextInt(55, 100)))
        }
    }

    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f
            val cy = h * (0.44f + lookY * 0.05f)
            val pulse = (0.88f + 0.12f * glowPhase * ringPulse.coerceIn(0.3f, 1.1f))
                .coerceIn(0.82f, 1.08f)
            val base = minOf(w, h)
            val halfW = base * 0.135f * eyeWidth * eyeOpen * pulse
            val halfH = base * 0.135f * eyeHeight * eyeOpen * pulse * blinkSafe(eyeOpen)
            val gap = base * 0.24f * eyeGap
            val gaze = lookX * halfW * 0.9f
            val bloom = 0.32f + 0.28f * ringPulse.coerceIn(0f, 1.2f)

            drawEporoRingEye(
                center = Offset(cx - gap + gaze, cy),
                halfW = halfW,
                halfH = halfH,
                glow = glowColor,
                bloomStrength = bloom,
            )
            drawEporoRingEye(
                center = Offset(cx + gap + gaze, cy),
                halfW = halfW,
                halfH = halfH,
                glow = glowColor,
                bloomStrength = bloom,
            )

            val showMouth = islandTallEnough && (
                mouthAmplitude != null ||
                    mood == AssistantMood.Speaking ||
                    mood == AssistantMood.Happy ||
                    mood == AssistantMood.Excited ||
                    mood == AssistantMood.Sad
                )
            if (showMouth) {
                val curve = when (mood) {
                    AssistantMood.Happy, AssistantMood.Excited -> 0.85f
                    AssistantMood.Sad -> -0.65f
                    else -> 0.35f
                }
                drawEporoIslandMouth(
                    center = Offset(cx, cy + base * 0.30f),
                    width = base * 0.20f,
                    curve = curve,
                    open = mouthOpen.value,
                    color = glowColor.copy(alpha = 0.88f),
                )
            }
        },
    )
}

private fun blinkSafe(eyeOpen: Float): Float = eyeOpen.coerceIn(0.15f, 1.2f)

private fun lerpEporoGlow(a: Color, b: Color, t: Float): Color {
    val u = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * u,
        green = a.green + (b.green - a.green) * u,
        blue = a.blue + (b.blue - a.blue) * u,
        alpha = a.alpha + (b.alpha - a.alpha) * u,
    )
}

private fun DrawScope.drawEporoContactShadow() {
    val w = size.width
    val h = size.height
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.22f),
                Color.Transparent,
            ),
            center = Offset(w * 0.5f, h * 0.92f),
            radius = w * 0.38f,
        ),
        topLeft = Offset(w * 0.18f, h * 0.82f),
        size = Size(w * 0.64f, h * 0.16f),
    )
}

private fun DrawScope.drawEporoHead(
    shellMorph: ExpressiveShellMorphState,
    shellColor: Color,
) {
    val w = size.width
    val h = size.height
    val bounds = Rect(
        left = w * 0.02f,
        top = h * 0.02f,
        right = w * 0.98f,
        bottom = h * 0.96f,
    )
    drawExpressiveFaceShell(
        morphState = shellMorph,
        bounds = bounds,
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                shellColor,
                EporoShellShade,
            ),
            center = Offset(w * 0.5f, h * 0.16f),
            radius = w * 0.95f,
        ),
    )
}

/** Soft ceramic gloss on the white oval shell. */
private fun DrawScope.drawEporoShellHighlights() {
    val w = size.width
    val h = size.height
    drawOval(
        color = Color.White.copy(alpha = 0.62f),
        topLeft = Offset(w * 0.38f, h * 0.06f),
        size = Size(w * 0.24f, h * 0.07f),
    )
    drawOval(
        color = Color.White.copy(alpha = 0.30f),
        topLeft = Offset(w * 0.12f, h * 0.18f),
        size = Size(w * 0.10f, h * 0.055f),
    )
    drawOval(
        color = Color.White.copy(alpha = 0.20f),
        topLeft = Offset(w * 0.74f, h * 0.16f),
        size = Size(w * 0.09f, h * 0.05f),
    )
}

/** Hollow pill ring — same family as Immersive/Nomi capsule eyes, with Eporo glow. */
private fun DrawScope.drawEporoRingEye(
    center: Offset,
    halfW: Float,
    halfH: Float,
    glow: Color,
    bloomStrength: Float = 0.55f,
) {
    val w = halfW.coerceAtLeast(1.5f)
    val h = halfH.coerceAtLeast(w * 0.45f)
    val bloomR = maxOf(w, h) * (1.7f + 0.45f * bloomStrength.coerceIn(0.2f, 1.2f))
    val bloomAlpha = (0.28f + 0.22f * bloomStrength).coerceIn(0.18f, 0.55f)

    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            color = glow.copy(alpha = bloomAlpha).toArgb()
            maskFilter = BlurMaskFilter(bloomR * 0.42f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawOval(
            center.x - bloomR,
            center.y - bloomR,
            center.x + bloomR,
            center.y + bloomR,
            paint,
        )
    }
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(glow.copy(alpha = bloomAlpha * 0.7f), Color.Transparent),
            center = center,
            radius = bloomR,
        ),
        topLeft = Offset(center.x - bloomR, center.y - bloomR),
        size = Size(bloomR * 2f, bloomR * 2f),
    )
    val strokeW = minOf(w, h) * 0.36f
    drawRoundRect(
        color = glow.copy(alpha = 0.98f),
        topLeft = Offset(center.x - w, center.y - h),
        size = Size(w * 2f, h * 2f),
        cornerRadius = CornerRadius(w, w),
        style = Stroke(width = strokeW, cap = StrokeCap.Round),
    )
    drawRoundRect(
        color = Color.Black,
        topLeft = Offset(center.x - w * 0.52f, center.y - h * 0.52f),
        size = Size(w * 1.04f, h * 1.04f),
        cornerRadius = CornerRadius(w * 0.52f, w * 0.52f),
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.88f),
        radius = minOf(w, h) * 0.11f,
        center = Offset(center.x, center.y + h * 0.70f),
    )
}

private fun DrawScope.drawEporoIslandMouth(
    center: Offset,
    width: Float,
    curve: Float,
    open: Float,
    color: Color,
) {
    val path = androidx.compose.ui.graphics.Path().apply {
        val lift = -curve * width * 0.35f
        val drop = open * width * 0.28f
        moveTo(center.x - width, center.y)
        quadraticTo(
            center.x,
            center.y + lift + drop,
            center.x + width,
            center.y,
        )
        if (open > 0.08f) {
            quadraticTo(
                center.x,
                center.y + lift + drop * 2.1f,
                center.x - width,
                center.y,
            )
            close()
        }
    }
    if (open > 0.08f) {
        drawPath(path, color = color.copy(alpha = 0.35f))
    }
    drawPath(
        path,
        color = color,
        style = Stroke(width = width * 0.18f, cap = StrokeCap.Round),
    )
}

/** Eye geometry — mirrors Immersive mood morphs, kept on ring glyphs. */
internal data class EporoEyePose(
    val eyeOpen: Float = 1f,
    val eyeWidth: Float = 1.12f,
    val eyeHeight: Float = 0.92f,
    val eyeGap: Float = 1.05f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val tilt: Float = 0f,
    val blinkSpeed: Float = 0.9f,
    val ringPulse: Float = 0.75f,
)

/** Horizontal elliptical visor (Dynamic Island) size by mood. */
internal data class EporoIslandPose(
    val widthFrac: Float = 0.78f,
    val heightFrac: Float = 0.34f,
)

internal fun AssistantMood.toEporoEyePose(): EporoEyePose = when (this) {
    AssistantMood.Idle -> EporoEyePose()
    AssistantMood.Listening -> EporoEyePose(
        eyeOpen = 1.08f,
        eyeWidth = 1.12f,
        eyeHeight = 0.98f,
        eyeGap = 1.08f,
        lookY = -0.05f,
        ringPulse = 1f,
        blinkSpeed = 1.05f,
    )
    AssistantMood.Speaking -> EporoEyePose(
        eyeOpen = 1.05f,
        eyeWidth = 1.1f,
        eyeHeight = 0.95f,
        tilt = 1f,
        ringPulse = 1f,
    )
    AssistantMood.Thinking -> EporoEyePose(
        eyeOpen = 0.98f,
        eyeHeight = 0.88f,
        lookX = 0.22f,
        lookY = -0.08f,
        tilt = 4f,
        blinkSpeed = 0.85f,
    )
    AssistantMood.Reading -> EporoEyePose(
        eyeOpen = 1.0f,
        lookX = 0.28f,
        lookY = 0.06f,
    )
    AssistantMood.Searching -> EporoEyePose(
        eyeOpen = 1.06f,
        eyeHeight = 0.95f,
        eyeGap = 1.12f,
        tilt = 1.5f,
        blinkSpeed = 1.15f,
        ringPulse = 0.9f,
    )
    AssistantMood.Happy -> EporoEyePose(
        eyeOpen = 0.98f,
        eyeWidth = 1.28f,
        eyeHeight = 0.72f,
        eyeGap = 1.06f,
        tilt = -2f,
        ringPulse = 0.95f,
    )
    AssistantMood.Excited -> EporoEyePose(
        eyeOpen = 1.12f,
        eyeWidth = 1.18f,
        eyeHeight = 1.02f,
        eyeGap = 1.1f,
        tilt = -3f,
        blinkSpeed = 1.25f,
        ringPulse = 1f,
    )
    AssistantMood.Sad -> EporoEyePose(
        eyeOpen = 0.85f,
        eyeWidth = 1.15f,
        eyeHeight = 0.82f,
        lookY = 0.14f,
        tilt = 3.5f,
        blinkSpeed = 0.7f,
        ringPulse = 0.45f,
    )
    AssistantMood.Bored -> EporoEyePose(
        eyeOpen = 0.72f,
        eyeWidth = 1.22f,
        eyeHeight = 0.62f,
        lookX = 0.32f,
        lookY = 0.06f,
        tilt = 2.5f,
        blinkSpeed = 0.55f,
        ringPulse = 0.4f,
    )
    AssistantMood.Drowsy -> EporoEyePose(
        eyeOpen = 0.48f,
        eyeWidth = 1.25f,
        eyeHeight = 0.42f,
        lookY = 0.1f,
        tilt = 2f,
        blinkSpeed = 0.4f,
        ringPulse = 0.35f,
    )
    AssistantMood.Tired -> EporoEyePose(
        eyeOpen = 0.55f,
        eyeWidth = 1.2f,
        eyeHeight = 0.5f,
        lookY = 0.12f,
        tilt = 2.5f,
        blinkSpeed = 0.38f,
        ringPulse = 0.35f,
    )
}

internal fun AssistantMood.toEporoIslandPose(): EporoIslandPose = when (this) {
    AssistantMood.Idle -> EporoIslandPose(widthFrac = 0.74f, heightFrac = 0.31f)
    AssistantMood.Listening -> EporoIslandPose(widthFrac = 0.84f, heightFrac = 0.37f)
    AssistantMood.Speaking, AssistantMood.Excited -> EporoIslandPose(
        widthFrac = 0.90f,
        heightFrac = 0.42f,
    )
    AssistantMood.Thinking, AssistantMood.Searching -> EporoIslandPose(
        widthFrac = 0.80f,
        heightFrac = 0.34f,
    )
    AssistantMood.Reading -> EporoIslandPose(widthFrac = 0.86f, heightFrac = 0.29f)
    AssistantMood.Happy -> EporoIslandPose(widthFrac = 0.82f, heightFrac = 0.39f)
    AssistantMood.Sad, AssistantMood.Bored -> EporoIslandPose(
        widthFrac = 0.68f,
        heightFrac = 0.27f,
    )
    AssistantMood.Drowsy, AssistantMood.Tired -> EporoIslandPose(
        widthFrac = 0.62f,
        heightFrac = 0.23f,
    )
}

/** @deprecated Use [toEporoEyePose]; kept for existing tests. */
internal fun AssistantMood.toEporoPose(): EporoPose {
    val e = toEporoEyePose()
    return EporoPose(
        eyeOpen = e.eyeOpen,
        eyeGap = e.eyeGap,
        lookX = e.lookX,
        lookY = e.lookY,
        ringPulse = e.ringPulse,
        tilt = e.tilt,
    )
}

internal data class EporoPose(
    val eyeOpen: Float = 1f,
    val eyeGap: Float = 1f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val ringPulse: Float = 0.75f,
    val tilt: Float = 0f,
)
