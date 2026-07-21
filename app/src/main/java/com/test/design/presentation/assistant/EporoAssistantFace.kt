package com.test.design.presentation.assistant

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.graphics.shapes.Morph
import kotlinx.coroutines.launch

val EporoShell = Color(0xFFF7F7F8)
val EporoShellShade = Color(0xFFE7E7E7)
val EporoVisor = Color(0xFF060606)
val EporoGlow = Color(0xFF9A7DFF)
val EporoGlowSoft = Color(0xFFB8A6FF)

/**
 * EPORO / robot head — Compose Canvas architecture:
 * HeadShell (Material Gem) → Visor → Eyes → BottomLightBar → GlossHighlights.
 * Same outer footprint as [DroidAssistantFace].
 */
@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
fun EporoAssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    shellColor: Color = EporoShell,
    visorColor: Color = EporoVisor,
    glowColor: Color = EporoGlow,
) {
    val pose = mood.toEporoPose()
    // Material Gem silhouette for the pale white outer head shell.
    val shellMorph = remember {
        ExpressiveShellMorphState(
            morph = Morph(
                start = ExpressiveShellKind.Gem.toRoundedPolygon(),
                end = ExpressiveShellKind.Gem.toRoundedPolygon(),
            ),
            progress = 1f,
        )
    }
    val eyeOpen = remember { Animatable(pose.eyeOpen) }
    val eyeGap = remember { Animatable(pose.eyeGap) }
    val lookX = remember { Animatable(pose.lookX) }
    val lookY = remember { Animatable(pose.lookY) }
    val tilt = remember { Animatable(pose.tilt) }

    LaunchedEffect(mood) {
        val p = mood.toEporoPose()
        launch { eyeOpen.animateTo(p.eyeOpen, spring(dampingRatio = 0.72f)) }
        launch { eyeGap.animateTo(p.eyeGap, spring(dampingRatio = 0.72f)) }
        launch { lookX.animateTo(p.lookX, spring(dampingRatio = 0.78f)) }
        launch { lookY.animateTo(p.lookY, spring(dampingRatio = 0.78f)) }
        launch { tilt.animateTo(p.tilt, spring(dampingRatio = 0.8f)) }
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
    val scan by breath.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "eporo_scan",
    )

    // Reference is wider than tall (~1.15).
    Canvas(modifier = modifier.aspectRatio(1.15f)) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f

        rotate(degrees = tilt.value, pivot = Offset(cx, h * 0.48f)) {
            drawHead(shellMorph = shellMorph, shellColor = shellColor)
            drawVisor(visorColor)
            drawHighlights()

            val gap = 0.24f * eyeGap.value
            val eyeY = h * (0.50f + lookY.value * 0.03f)
            val gaze = lookX.value + if (
                mood == AssistantMood.Searching || mood == AssistantMood.Reading
            ) {
                scan * 0.04f
            } else {
                0f
            }
            val pulse = (0.9f + 0.1f * glowPhase).coerceIn(0.85f, 1f)
            val eyeR = (minOf(w, h) * 0.095f) * eyeOpen.value * pulse

            drawEye(
                center = Offset(w * (0.50f - gap) + gaze * eyeR, eyeY),
                radius = eyeR,
                glow = glowColor,
            )
            drawEye(
                center = Offset(w * (0.50f + gap) + gaze * eyeR, eyeY),
                radius = eyeR,
                glow = glowColor,
            )

            drawBottomLight(glowPhase)
            drawVisorSpecks()
        }
    }
}

private fun DrawScope.drawHead(
    shellMorph: ExpressiveShellMorphState,
    shellColor: Color,
) {
    val w = size.width
    val h = size.height
    val bounds = Rect(
        left = 0f,
        top = 0f,
        right = w,
        bottom = h * 0.95f,
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
            center = Offset(w * 0.5f, h * 0.15f),
            radius = w,
        ),
    )
}

/** Organic Bézier visor — convex brow, blunt chin tab.
 * Inset only a thin margin from the Gem shell so the white border stays tight.
 */
private fun DrawScope.drawVisor(visorColor: Color) {
    val w = size.width
    val h = size.height
    val p = Path().apply {
        // ~4–6% inset from Gem shell edges (was ~18% / deeper) for a slim white bezel.
        moveTo(w * 0.08f, h * 0.28f)
        cubicTo(
            w * 0.18f, h * 0.14f,
            w * 0.82f, h * 0.14f,
            w * 0.92f, h * 0.28f,
        )
        cubicTo(
            w * 0.97f, h * 0.40f,
            w * 0.94f, h * 0.66f,
            w * 0.82f, h * 0.76f,
        )
        cubicTo(
            w * 0.70f, h * 0.82f,
            w * 0.62f, h * 0.90f,
            w * 0.50f, h * 0.90f,
        )
        cubicTo(
            w * 0.38f, h * 0.90f,
            w * 0.30f, h * 0.82f,
            w * 0.18f, h * 0.76f,
        )
        cubicTo(
            w * 0.06f, h * 0.66f,
            w * 0.03f, h * 0.40f,
            w * 0.08f, h * 0.28f,
        )
        close()
    }
    drawPath(path = p, color = visorColor)
    // Soft glass sheen.
    drawPath(
        path = p,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.14f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.35f),
            ),
            startY = h * 0.14f,
            endY = h * 0.90f,
        ),
    )
    // Elongated top specular.
    drawRoundRect(
        color = Color.White.copy(alpha = 0.55f),
        topLeft = Offset(w * 0.42f, h * 0.22f),
        size = Size(w * 0.16f, h * 0.028f),
        cornerRadius = CornerRadius(h * 0.02f, h * 0.02f),
    )
}

private fun DrawScope.drawEye(
    center: Offset,
    radius: Float,
    glow: Color,
) {
    // Soft bloom via BlurMaskFilter.
    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            color = glow.copy(alpha = 0.45f).toArgb()
            maskFilter = BlurMaskFilter(radius * 0.9f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawCircle(center.x, center.y, radius * 1.55f, paint)
    }
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glow.copy(alpha = 0.35f), Color.Transparent),
            center = center,
            radius = radius * 1.7f,
        ),
        radius = radius * 1.7f,
        center = center,
    )
    drawCircle(
        color = glow.copy(alpha = 0.98f),
        radius = radius,
        center = center,
        style = Stroke(width = radius * 0.32f, cap = StrokeCap.Round),
    )
    drawCircle(
        color = Color.Black,
        radius = radius * 0.58f,
        center = center,
    )
    // Specular on bottom of ring.
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = radius * 0.1f,
        center = Offset(center.x, center.y + radius * 0.78f),
    )
}

private fun DrawScope.drawBottomLight(phase: Float) {
    val w = size.width
    val h = size.height
    val y = h * 0.90f
    val barH = h * 0.018f
    // Bloom under LED.
    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            color = EporoGlowSoft.copy(alpha = 0.35f * phase).toArgb()
            maskFilter = BlurMaskFilter(barH * 4f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawRoundRect(
            w * 0.06f,
            y - barH,
            w * 0.94f,
            y + barH * 2f,
            barH,
            barH,
            paint,
        )
    }
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.25f),
                Color(0xFFF4EAFF).copy(alpha = 0.55f + 0.35f * phase),
                Color.White.copy(alpha = 0.25f),
            ),
        ),
        topLeft = Offset(w * 0.05f, y),
        size = Size(w * 0.90f, barH),
        cornerRadius = CornerRadius(barH, barH),
    )
}

private fun DrawScope.drawHighlights() {
    val w = size.width
    val h = size.height
    drawOval(
        color = Color.White.copy(alpha = 0.7f),
        topLeft = Offset(w * 0.40f, h * 0.07f),
        size = Size(w * 0.20f, h * 0.08f),
    )
    drawOval(
        color = Color.White.copy(alpha = 0.35f),
        topLeft = Offset(w * 0.12f, h * 0.18f),
        size = Size(w * 0.10f, h * 0.06f),
    )
    drawOval(
        color = Color.White.copy(alpha = 0.22f),
        topLeft = Offset(w * 0.72f, h * 0.16f),
        size = Size(w * 0.09f, h * 0.05f),
    )
}

private fun DrawScope.drawVisorSpecks() {
    val w = size.width
    val h = size.height
    val base = Offset(w * 0.68f, h * 0.62f)
    drawCircle(Color.White.copy(alpha = 0.65f), radius = w * 0.006f, center = base)
    drawCircle(
        Color.White.copy(alpha = 0.5f),
        radius = w * 0.005f,
        center = base + Offset(w * 0.015f, h * 0.025f),
    )
    drawCircle(
        Color.White.copy(alpha = 0.4f),
        radius = w * 0.004f,
        center = base + Offset(w * 0.028f, h * 0.048f),
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

internal fun AssistantMood.toEporoPose(): EporoPose = when (this) {
    AssistantMood.Idle -> EporoPose(eyeOpen = 1f, ringPulse = 0.65f)
    AssistantMood.Listening -> EporoPose(
        eyeOpen = 1.05f,
        eyeGap = 1.06f,
        ringPulse = 1f,
        lookY = -0.03f,
    )
    AssistantMood.Thinking -> EporoPose(
        eyeOpen = 0.94f,
        lookX = 0.18f,
        lookY = -0.08f,
        tilt = 2f,
    )
    AssistantMood.Reading -> EporoPose(
        eyeOpen = 0.96f,
        lookX = 0.14f,
        lookY = 0.1f,
    )
    AssistantMood.Searching -> EporoPose(
        eyeOpen = 1.04f,
        eyeGap = 1.08f,
        ringPulse = 0.9f,
    )
    AssistantMood.Speaking -> EporoPose(
        eyeOpen = 1.03f,
        ringPulse = 1f,
        lookY = 0.02f,
    )
    AssistantMood.Happy, AssistantMood.Excited -> EporoPose(
        eyeOpen = 1.08f,
        eyeGap = 1.04f,
        ringPulse = 0.95f,
        tilt = -2f,
    )
    AssistantMood.Sad -> EporoPose(
        eyeOpen = 0.8f,
        lookY = 0.14f,
        ringPulse = 0.45f,
        tilt = 3f,
    )
    AssistantMood.Bored -> EporoPose(
        eyeOpen = 0.72f,
        lookX = 0.28f,
        ringPulse = 0.4f,
    )
    AssistantMood.Drowsy, AssistantMood.Tired -> EporoPose(
        eyeOpen = 0.45f,
        lookY = 0.08f,
        ringPulse = 0.35f,
        tilt = 4f,
    )
}
