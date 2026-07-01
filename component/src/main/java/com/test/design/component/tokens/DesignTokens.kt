package com.test.design.component.tokens

import com.test.design.component.theme.OemSpacing

object DesignTokens {
    val spacing = OemSpacing
    const val minContrastRatio = 4.5f
    const val minTouchTargetDp = 76
    const val drivingTouchTargetDp = 84
    const val restrictedTouchTargetDp = 88
    const val minBodyTextSp = 20
    const val minCaptionTextSp = 16
    const val maxDrivingAnimationMs = 250
    const val restrictedAnimationMs = 0
    const val leftColumnWeight = 0.7f
    const val rightColumnWeight = 0.3f
    const val blueZoneHeightFraction = 0.11f

    val componentCategories = listOf(
        "Actions", "Selection", "Input", "Display", "Feedback", "Navigation",
    )
}
