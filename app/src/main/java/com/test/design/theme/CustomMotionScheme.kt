package com.test.design.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MotionScheme

/**
 * OEM-tuned motion: snappier spatial springs with critically damped effects.
 */
object CustomMotionScheme : MotionScheme {
    override fun <T> defaultSpatialSpec(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMedium,
    )

    override fun <T> fastSpatialSpec(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.92f,
        stiffness = Spring.StiffnessHigh,
    )

    override fun <T> slowSpatialSpec(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.68f,
        stiffness = Spring.StiffnessMediumLow,
    )

    override fun <T> defaultEffectsSpec(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    override fun <T> fastEffectsSpec(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    override fun <T> slowEffectsSpec(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow,
    )
}
