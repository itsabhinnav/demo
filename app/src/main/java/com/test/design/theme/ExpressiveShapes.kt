package com.test.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Playful, fluid M3 Expressive shapes with varying corner radii for automotive surfaces.
 */
val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(28.dp),
    large = RoundedCornerShape(36.dp),
    extraLarge = RoundedCornerShape(48.dp),
)

val WidgetCardShape = RoundedCornerShape(topStart = 40.dp, topEnd = 16.dp, bottomEnd = 40.dp, bottomStart = 16.dp)
val MediaAlbumShape = RoundedCornerShape(topStart = 48.dp, topEnd = 24.dp, bottomEnd = 48.dp, bottomStart = 24.dp)
val ClimateDialShape = RoundedCornerShape(56.dp)
