package com.test.design.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Same AAOS sizes as [CarTypography] with tighter, production-grade rhythm.
 */
val OemBrandTypography = Typography(
    displayLarge = CarTypography.displayLarge.copy(fontWeight = FontWeight.Light),
    displayMedium = CarTypography.displayMedium.copy(fontWeight = FontWeight.Light),
    displaySmall = CarTypography.displaySmall.copy(fontWeight = FontWeight.Normal),
    headlineLarge = CarTypography.headlineLarge.copy(
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.25).sp,
    ),
    headlineMedium = CarTypography.headlineMedium.copy(
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.15).sp,
    ),
    headlineSmall = CarTypography.headlineSmall.copy(fontWeight = FontWeight.Medium),
    titleLarge = CarTypography.titleLarge.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = CarTypography.titleMedium.copy(fontWeight = FontWeight.Medium),
    titleSmall = CarTypography.titleSmall.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = CarTypography.bodyLarge.copy(fontWeight = FontWeight.Normal),
    bodyMedium = CarTypography.bodyMedium.copy(fontWeight = FontWeight.Normal),
    bodySmall = CarTypography.bodySmall.copy(fontWeight = FontWeight.Normal),
    labelLarge = CarTypography.labelLarge.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp,
    ),
    labelMedium = CarTypography.labelMedium.copy(fontWeight = FontWeight.Medium),
    labelSmall = CarTypography.labelSmall.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
    ),
)
