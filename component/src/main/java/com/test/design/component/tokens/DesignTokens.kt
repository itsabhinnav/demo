package com.test.design.component.tokens

import com.test.design.component.theme.NissanSpacing

object DesignTokens {
    val spacing = NissanSpacing
    const val minContrastRatio = 4.5f
    const val minTouchTargetDp = 48
    const val maxDrivingAnimationMs = 200
    const val leftColumnWeight = 0.7f
    const val rightColumnWeight = 0.3f
    const val blueZoneHeightFraction = 0.11f

    val componentCategories = listOf(
        "Actions", "Selection", "Input", "Display", "Feedback", "Navigation",
    )
}
