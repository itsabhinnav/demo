package com.test.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Consistent, automotive-grade corner radii — uniform and restrained.
 */
val OemBrandShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(16.dp),
)

/** Squared action control used instead of circular FABs in OEM layouts. */
val OemActionShape = RoundedCornerShape(14.dp)

/** Icon container behind list and toolbar actions. */
val OemIconContainerShape = RoundedCornerShape(12.dp)
