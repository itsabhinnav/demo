package com.test.design.component.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object OemVisuals {
    val buttonShape = RoundedCornerShape(0.dp)
    val cardShape = RoundedCornerShape(0.dp)
    val chipShape = RoundedCornerShape(0.dp)
    val tabContainerShape = RoundedCornerShape(0.dp)
    val iconContainerShape = RoundedCornerShape(0.dp)
    val fabShape = RoundedCornerShape(0.dp)
}

fun Modifier.oemSurfaceBorder(
    shape: Shape,
    color: androidx.compose.ui.graphics.Color = OemBorder,
    width: Dp = 1.dp,
): Modifier = border(width = width, color = color, shape = shape)

fun Modifier.oemCardSurface(shape: Shape = OemVisuals.cardShape): Modifier =
    clip(shape)
        .background(OemSurfaceElevated)
        .oemSurfaceBorder(shape)
