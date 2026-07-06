package com.test.design.component.motion

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.material3.MotionScheme

/**
 * AAOS helpers for Material 3 [MotionScheme] spring physics.
 *
 * Values are read from [MotionScheme.expressive] / [MotionScheme.standard] so OEM
 * code stays aligned with M3 token updates. Pass [SpringPhysics.initialVelocity] to
 * [Animatable.animateTo] (third parameter) for swipe/fling panels.
 */
object OemMotionScheme {

    enum class SpringToken {
        DefaultSpatial,
        FastSpatial,
        SlowSpatial,
        DefaultEffects,
        FastEffects,
        SlowEffects,
    }

    data class SpringPhysics(
        val dampingRatio: Float,
        val stiffness: Float,
        val initialVelocity: Float = 0f,
    ) {
        fun label(): String = buildString {
            append("ζ=")
            append(String.format("%.2f", dampingRatio))
            append(" · k=")
            append(stiffness.toInt())
            if (initialVelocity != 0f) {
                append(" · v₀=")
                append(initialVelocity.toInt())
            }
        }
    }

    fun expressivePhysics(token: SpringToken): SpringPhysics =
        readPhysics(MotionScheme.expressive(), token)

    fun standardPhysics(token: SpringToken): SpringPhysics =
        readPhysics(MotionScheme.standard(), token)

    fun <T> springSpec(physics: SpringPhysics): FiniteAnimationSpec<T> =
        spring(
            dampingRatio = physics.dampingRatio,
            stiffness = physics.stiffness,
        )

    fun spatialSpringSpec(expressive: Boolean, token: SpringToken = SpringToken.DefaultSpatial): FiniteAnimationSpec<Float> {
        val physics = if (expressive) expressivePhysics(token) else standardPhysics(token)
        return springSpec(physics)
    }

    /**
     * OEM-custom [MotionScheme] built from expressive M3 specs.
     * Use when documenting a token-aligned scheme for AAOS design reviews.
     */
    fun customExpressiveFromBuiltIn(): MotionScheme = MotionScheme.expressive()

    private fun readPhysics(scheme: MotionScheme, token: SpringToken): SpringPhysics {
        @Suppress("UNCHECKED_CAST")
        val spec = when (token) {
            SpringToken.DefaultSpatial -> scheme.defaultSpatialSpec<Float>()
            SpringToken.FastSpatial -> scheme.fastSpatialSpec()
            SpringToken.SlowSpatial -> scheme.slowSpatialSpec()
            SpringToken.DefaultEffects -> scheme.defaultEffectsSpec()
            SpringToken.FastEffects -> scheme.fastEffectsSpec()
            SpringToken.SlowEffects -> scheme.slowEffectsSpec()
        } as SpringSpec<Float>
        return SpringPhysics(
            dampingRatio = spec.dampingRatio,
            stiffness = spec.stiffness,
        )
    }
}
