package com.test.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Asymmetric, pill-heavy shapes for a distinctly non-stock Material look.
 */
val OemBrandShapes = Shapes(
    extraSmall = RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomEnd = 4.dp, bottomStart = 12.dp),
    small = RoundedCornerShape(topStart = 8.dp, topEnd = 20.dp, bottomEnd = 8.dp, bottomStart = 20.dp),
    medium = RoundedCornerShape(topStart = 16.dp, topEnd = 32.dp, bottomEnd = 16.dp, bottomStart = 32.dp),
    large = RoundedCornerShape(topStart = 28.dp, topEnd = 8.dp, bottomEnd = 28.dp, bottomStart = 8.dp),
    extraLarge = RoundedCornerShape(50),
)
