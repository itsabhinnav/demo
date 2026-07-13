package com.test.design.presentation.ivi.climate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Physical HVAC panel cues — raised bezels, recessed faces, tick marks, and specular gloss.
 */
object ClimateSkeuomorphism {
    fun raisedButtonBrush(
        top: Color,
        mid: Color,
        bottom: Color,
    ): Brush = Brush.verticalGradient(
        colors = listOf(top, mid, bottom),
    )

    fun recessedWellBrush(
        rim: Color,
        well: Color,
        depth: Color,
    ): Brush = Brush.verticalGradient(
        colors = listOf(depth, well, rim),
    )

    fun fanBarBrush(
        active: Boolean,
        primary: Color,
        container: Color,
    ): Brush = if (active) {
        Brush.verticalGradient(
            colors = listOf(
                primary.copy(alpha = 0.95f),
                primary,
                primary.copy(alpha = 0.55f),
            ),
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                container.copy(alpha = 0.85f),
                container.copy(alpha = 0.45f),
                container.copy(alpha = 0.7f),
            ),
        )
    }
}

fun Modifier.skeuomorphicDialShell(
    shape: Shape,
    bezelLight: Color,
    bezelMid: Color,
    bezelDark: Color,
    faceHighlight: Color,
    face: Color,
    faceShadow: Color,
    tickColor: Color,
    elevation: Dp = 16.dp,
    showTicks: Boolean = true,
): Modifier = this
    .shadow(elevation = elevation, shape = shape, clip = false)
    .border(
        width = 3.dp,
        brush = Brush.linearGradient(
            colors = listOf(bezelLight, bezelMid, bezelDark, bezelMid, bezelLight),
        ),
        shape = shape,
    )
    .background(color = face, shape = shape)
    .clip(shape)
    .drawWithContent {
        val radius = min(size.width, size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Recessed face with top-left light catch.
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to faceHighlight,
                    0.42f to face,
                    1.0f to faceShadow,
                ),
                center = Offset(size.width * 0.32f, size.height * 0.28f),
                radius = radius * 1.15f,
            ),
            radius = radius,
            center = center,
        )

        if (showTicks) {
            val tickCount = 24
            for (i in 0 until tickCount) {
                val angleDeg = i * (360f / tickCount) - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val major = i % 3 == 0
                val outer = radius * 0.92f
                val inner = radius * if (major) 0.78f else 0.84f
                val alpha = if (major) 0.7f else 0.35f
                drawLine(
                    color = tickColor.copy(alpha = alpha),
                    start = Offset(
                        center.x + (cos(angleRad) * inner).toFloat(),
                        center.y + (sin(angleRad) * inner).toFloat(),
                    ),
                    end = Offset(
                        center.x + (cos(angleRad) * outer).toFloat(),
                        center.y + (sin(angleRad) * outer).toFloat(),
                    ),
                    strokeWidth = if (major) 3f else 1.5f,
                )
            }
            // Specular crescent on the upper-left rim.
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.28f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.28f, size.height * 0.22f),
                    radius = radius * 0.85f,
                ),
                startAngle = 200f,
                sweepAngle = 110f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.96f, center.y - radius * 0.96f),
                size = Size(radius * 1.92f, radius * 1.92f),
                style = Stroke(width = radius * 0.08f),
            )
        }
        drawContent()
    }

fun Modifier.skeuomorphicRaisedControl(
    shape: Shape,
    top: Color,
    mid: Color,
    bottom: Color,
    rim: Color,
    elevation: Dp = 6.dp,
    pressed: Boolean = false,
): Modifier = this
    .shadow(
        elevation = if (pressed) 1.dp else elevation,
        shape = shape,
        clip = false,
    )
    .background(
        brush = ClimateSkeuomorphism.raisedButtonBrush(
            top = if (pressed) mid else top,
            mid = mid,
            bottom = if (pressed) top else bottom,
        ),
        shape = shape,
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                rim.copy(alpha = if (pressed) 0.25f else 0.65f),
                rim.copy(alpha = 0.15f),
            ),
        ),
        shape = shape,
    )

fun Modifier.skeuomorphicRecessedTrack(
    shape: Shape,
    rim: Color,
    well: Color,
    depth: Color,
): Modifier = this
    .background(
        brush = ClimateSkeuomorphism.recessedWellBrush(rim, well, depth),
        shape = shape,
    )
    .border(
        width = 1.5.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.45f),
                rim.copy(alpha = 0.35f),
            ),
        ),
        shape = shape,
    )
    .drawBehind {
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent),
            ),
            topLeft = Offset(4f, 3f),
            size = Size(size.width - 8f, size.height * 0.35f),
            cornerRadius = CornerRadius(24f, 24f),
        )
    }

fun Modifier.skeuomorphicRaisedPill(
    shape: Shape,
    top: Color,
    mid: Color,
    bottom: Color,
): Modifier = this
    .shadow(elevation = 4.dp, shape = shape, clip = false)
    .background(
        brush = ClimateSkeuomorphism.raisedButtonBrush(top, mid, bottom),
        shape = shape,
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.2f)),
        ),
        shape = shape,
    )

fun Modifier.skeuomorphicFanBar(
    shape: Shape,
    active: Boolean,
    primary: Color,
    container: Color,
): Modifier = this
    .shadow(elevation = if (active) 6.dp else 2.dp, shape = shape, clip = false)
    .background(
        brush = ClimateSkeuomorphism.fanBarBrush(active, primary, container),
        shape = shape,
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (active) 0.4f else 0.18f),
                Color.Black.copy(alpha = 0.35f),
            ),
        ),
        shape = shape,
    )
    .drawBehind {
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent),
            ),
            topLeft = Offset(size.width * 0.15f, 2f),
            size = Size(size.width * 0.7f, size.height * 0.4f),
            cornerRadius = CornerRadius(6f, 6f),
        )
    }
