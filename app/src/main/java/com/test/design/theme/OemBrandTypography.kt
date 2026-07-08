package com.test.design.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Same AAOS minimum sizes as [CarTypography] but with heavier weights and
 * wider tracking so OEM-branded screens feel distinct from stock Material.
 */
val OemBrandTypography = Typography(
    displayLarge = CarTypography.displayLarge.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
    ),
    displayMedium = CarTypography.displayMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
    ),
    displaySmall = CarTypography.displaySmall.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
    ),
    headlineLarge = CarTypography.headlineLarge.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
    ),
    headlineMedium = CarTypography.headlineMedium.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.6.sp,
    ),
    headlineSmall = CarTypography.headlineSmall.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
    ),
    titleLarge = CarTypography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
    ),
    titleMedium = CarTypography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.4.sp,
    ),
    titleSmall = CarTypography.titleSmall.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.3.sp,
    ),
    bodyLarge = CarTypography.bodyLarge.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.25.sp,
    ),
    bodyMedium = CarTypography.bodyMedium.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
    ),
    bodySmall = CarTypography.bodySmall.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp,
    ),
    labelLarge = CarTypography.labelLarge.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
    ),
    labelMedium = CarTypography.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = CarTypography.labelSmall.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.4.sp,
    ),
)
