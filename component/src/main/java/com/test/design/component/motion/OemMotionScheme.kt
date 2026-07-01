package com.test.design.component.motion

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable

/**
 * Material 3 motion scheme mirrored from StandardMotionTokens and ExpressiveMotionTokens.
 * Use until androidx.compose.material3 MotionScheme is public on the stable channel.
 */
@Immutable
interface OemMotionScheme {
    fun <T> defaultSpatialSpec(): FiniteAnimationSpec<T>
    fun <T> fastSpatialSpec(): FiniteAnimationSpec<T>
    fun <T> slowSpatialSpec(): FiniteAnimationSpec<T>
    fun <T> defaultEffectsSpec(): FiniteAnimationSpec<T>
    fun <T> fastEffectsSpec(): FiniteAnimationSpec<T>
    fun <T> slowEffectsSpec(): FiniteAnimationSpec<T>

    companion object {
        fun standard(): OemMotionScheme = StandardOemMotionScheme
        fun expressive(): OemMotionScheme = ExpressiveOemMotionScheme
    }
}

private object StandardOemMotionScheme : OemMotionScheme {
    override fun <T> defaultSpatialSpec() = spring<T>(
        dampingRatio = 0.9f,
        stiffness = 700f,
    )

    override fun <T> fastSpatialSpec() = spring<T>(
        dampingRatio = 0.9f,
        stiffness = 1400f,
    )

    override fun <T> slowSpatialSpec() = spring<T>(
        dampingRatio = 0.9f,
        stiffness = 300f,
    )

    override fun <T> defaultEffectsSpec() = spring<T>(
        dampingRatio = 1.0f,
        stiffness = 1600f,
    )

    override fun <T> fastEffectsSpec() = spring<T>(
        dampingRatio = 1.0f,
        stiffness = 3800f,
    )

    override fun <T> slowEffectsSpec() = spring<T>(
        dampingRatio = 1.0f,
        stiffness = 800f,
    )
}

private object ExpressiveOemMotionScheme : OemMotionScheme {
    override fun <T> defaultSpatialSpec() = spring<T>(
        dampingRatio = 0.8f,
        stiffness = 380f,
    )

    override fun <T> fastSpatialSpec() = spring<T>(
        dampingRatio = 0.6f,
        stiffness = 800f,
    )

    override fun <T> slowSpatialSpec() = spring<T>(
        dampingRatio = 0.8f,
        stiffness = 200f,
    )

    override fun <T> defaultEffectsSpec() = spring<T>(
        dampingRatio = 1.0f,
        stiffness = 1600f,
    )

    override fun <T> fastEffectsSpec() = spring<T>(
        dampingRatio = 1.0f,
        stiffness = 3800f,
    )

    override fun <T> slowEffectsSpec() = spring<T>(
        dampingRatio = 1.0f,
        stiffness = 800f,
    )
}
