package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.test.design.assistant.api.AssistantContextGlyph
import kotlinx.coroutines.delay
import kotlin.math.sin

private val ThinkCloudFill = Color(0xFFE8ECFF)
private val ThinkCloudStroke = Color(0xFFF5F7FF)
private val ThinkCloudDot = Color(0xFFB39DDB)

/**
 * Weather sink face: Fusion Eyes with weather glyphs rendered *inside* the visor
 * as a realtime HUD (eyes ↔ icon crossfade), plus a thinking cloud at top-right.
 */
@Composable
fun WeatherSinkFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    contextGlyph: AssistantContextGlyph? = null,
    gazeX: Float? = null,
    gazeY: Float? = null,
    mouthAmplitude: Float? = null,
    brandGlow: Color = Color(0xFF8AB4F8),
    highContrast: Boolean = false,
    gesture: FaceGesture = FaceGesture.None,
) {
    var showIcon by remember { mutableStateOf(false) }
    LaunchedEffect(contextGlyph) {
        showIcon = false
        if (contextGlyph == null) return@LaunchedEffect
        // Settle on eyes first, then leisurely alternate so the swap reads clearly.
        delay(1_800)
        while (true) {
            showIcon = true
            delay(2_800)
            showIcon = false
            delay(2_800)
        }
    }

    val iconAlpha = remember { Animatable(0f) }
    LaunchedEffect(showIcon, contextGlyph) {
        if (contextGlyph != null && showIcon) {
            iconAlpha.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
        } else {
            iconAlpha.animateTo(0f, tween(480, easing = FastOutSlowInEasing))
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        FusionEyesAssistantFace(
            mood = mood,
            modifier = Modifier.fillMaxSize(),
            gazeX = gazeX,
            gazeY = gazeY,
            mouthAmplitude = mouthAmplitude,
            brandGlow = brandGlow,
            highContrast = highContrast,
            gesture = gesture,
            visorDisplayGlyph = contextGlyph?.imageVector(),
            visorDisplayAlpha = if (contextGlyph != null) iconAlpha.value else 0f,
            visorDisplayTint = contextGlyph?.tint(),
        )

        WeatherSinkThinkingCloud(
            visible = mood == AssistantMood.Thinking,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 6.dp, y = (-4).dp)
                .fillMaxSize(0.42f),
        )
    }
}

@Composable
private fun WeatherSinkThinkingCloud(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val life = rememberInfiniteTransition(label = "weather_think_life")
    val phase by life.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2_800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "weather_think_phase",
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(280)) +
            scaleIn(
                initialScale = 0.55f,
                animationSpec = spring(
                    dampingRatio = 0.68f,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ) +
            slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = spring(
                    dampingRatio = 0.78f,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ),
        exit = fadeOut(tween(200)) +
            scaleOut(targetScale = 0.7f, animationSpec = tween(200)) +
            slideOutHorizontally(
                targetOffsetX = { it / 4 },
                animationSpec = tween(220, easing = FastOutSlowInEasing),
            ),
        modifier = modifier,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val side = minOf(size.width, size.height)
            val bob = sin(phase.toDouble()).toFloat() * side * 0.04f
            val anchor = Offset(size.width * 0.58f, size.height * 0.42f + bob)
            drawWeatherThoughtCloud(
                anchor = anchor,
                cloudSize = side * 0.72f,
                life = phase,
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWeatherThoughtCloud(
    anchor: Offset,
    cloudSize: Float,
    life: Float,
) {
    val fill = ThinkCloudFill.copy(alpha = 0.94f)
    val stroke = ThinkCloudStroke.copy(alpha = 0.95f)
    val s = cloudSize

    val c1 = Offset(anchor.x, anchor.y)
    val c2 = Offset(anchor.x - s * 0.28f, anchor.y + s * 0.06f)
    val c3 = Offset(anchor.x + s * 0.3f, anchor.y + s * 0.04f)
    val c4 = Offset(anchor.x + s * 0.05f, anchor.y - s * 0.22f)
    drawCircle(fill, s * 0.32f, c1)
    drawCircle(fill, s * 0.26f, c2)
    drawCircle(fill, s * 0.28f, c3)
    drawCircle(fill, s * 0.24f, c4)
    drawCircle(stroke, s * 0.32f, c1, style = Stroke(1.5f))
    drawCircle(stroke, s * 0.26f, c2, style = Stroke(1.5f))
    drawCircle(stroke, s * 0.28f, c3, style = Stroke(1.5f))
    drawCircle(stroke, s * 0.24f, c4, style = Stroke(1.5f))

    drawCircle(fill, s * 0.08f, Offset(anchor.x - s * 0.42f, anchor.y + s * 0.32f))
    drawCircle(fill, s * 0.05f, Offset(anchor.x - s * 0.55f, anchor.y + s * 0.48f))

    val dotY = anchor.y + s * 0.02f
    for (i in 0..2) {
        val bounce = sin(life * 2f + i * 0.9f).toFloat() * s * 0.06f
        drawCircle(
            color = ThinkCloudDot.copy(alpha = 0.9f),
            radius = s * 0.055f,
            center = Offset(anchor.x - s * 0.16f + i * s * 0.16f, dotY + bounce),
        )
    }
}
