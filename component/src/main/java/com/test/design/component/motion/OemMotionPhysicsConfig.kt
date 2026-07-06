package com.test.design.component.motion

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import kotlin.math.abs

enum class MotionSchemePreset {
    Standard,
    Expressive,
    Custom,
}

/**
 * OEM-tunable motion physics for AAOS surfaces: list fling, progress fills, and M3 spring specs.
 * Values map to [MotionScheme] spatial/effects springs and duration-based progress motion.
 */
data class OemMotionPhysicsConfig(
    val preset: MotionSchemePreset = MotionSchemePreset.Expressive,
    val spatialDampingRatio: Float = Spring.DampingRatioMediumBouncy,
    val spatialStiffness: Float = Spring.StiffnessMediumLow,
    val effectsDampingRatio: Float = Spring.DampingRatioNoBouncy,
    val effectsStiffness: Float = Spring.StiffnessMedium,
    val progressDurationMs: Int = 400,
    val progressUseSpring: Boolean = false,
    val progressStiffness: Float = Spring.StiffnessLow,
    val flingFrictionMultiplier: Float = 1f,
) {
    companion object {
        val Default = OemMotionPhysicsConfig()

        fun fromPreset(preset: MotionSchemePreset): OemMotionPhysicsConfig = when (preset) {
            MotionSchemePreset.Standard -> Default.copy(
                preset = preset,
                spatialDampingRatio = Spring.DampingRatioNoBouncy,
                spatialStiffness = Spring.StiffnessMedium,
                effectsDampingRatio = Spring.DampingRatioNoBouncy,
                effectsStiffness = Spring.StiffnessMedium,
                progressDurationMs = 300,
                progressUseSpring = false,
                flingFrictionMultiplier = 1.2f,
            )
            MotionSchemePreset.Expressive -> Default.copy(
                preset = preset,
                spatialDampingRatio = Spring.DampingRatioMediumBouncy,
                spatialStiffness = Spring.StiffnessMediumLow,
                effectsDampingRatio = Spring.DampingRatioLowBouncy,
                effectsStiffness = Spring.StiffnessMediumLow,
                progressDurationMs = 400,
                progressUseSpring = true,
                flingFrictionMultiplier = 0.85f,
            )
            MotionSchemePreset.Custom -> Default.copy(preset = preset)
        }
    }
}

fun OemMotionPhysicsConfig.toMotionScheme(): MotionScheme = when (preset) {
    MotionSchemePreset.Standard -> MotionScheme.standard()
    MotionSchemePreset.Expressive -> MotionScheme.expressive()
    MotionSchemePreset.Custom -> createCustomMotionScheme(
        spatialDampingRatio = spatialDampingRatio,
        spatialStiffness = spatialStiffness,
        effectsDampingRatio = effectsDampingRatio,
        effectsStiffness = effectsStiffness,
    )
}

@Composable
fun OemMotionPhysicsConfig.progressSpec(
    animationsEnabled: Boolean = RestrictedComponentPolicy
        .maxAnimationDurationMs(currentDrivingUxState()) > 0,
): AnimationSpec<Float> {
    if (!animationsEnabled) return snap()
    return if (progressUseSpring) {
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = progressStiffness,
        )
    } else {
        val cappedMs = OemMotion.durationMs(
            state = currentDrivingUxState(),
            opening = true,
            requestedMs = progressDurationMs,
        )
        OemMotion.spec(cappedMs)
    }
}

@Composable
fun rememberOemFlingBehavior(
    config: OemMotionPhysicsConfig,
    animationsEnabled: Boolean = RestrictedComponentPolicy
        .maxAnimationDurationMs(currentDrivingUxState()) > 0,
): FlingBehavior {
    val density = LocalDensity.current
    val decay = remember(density, config.flingFrictionMultiplier, animationsEnabled) {
        if (!animationsEnabled) {
            exponentialDecay<Float>(frictionMultiplier = 10_000f)
        } else {
            exponentialDecay(
                frictionMultiplier = 2.2f * config.flingFrictionMultiplier.coerceIn(0.4f, 2.5f),
                absVelocityThreshold = with(density) { 0.5.dp.toPx() },
            )
        }
    }
    return remember(decay) { OemFlingBehavior(decay) }
}

private class OemFlingBehavior(
    private val decay: DecayAnimationSpec<Float>,
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        if (abs(initialVelocity) <= 1f) return initialVelocity
        var velocityLeft = initialVelocity
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = initialVelocity,
        ).animateDecay(decay) {
            val delta = value - lastValue
            lastValue = value
            val consumed = scrollBy(delta)
            if (abs(delta - consumed) > 0.5f) {
                cancelAnimation()
            }
            velocityLeft = this.velocity
        }
        return velocityLeft
    }
}

private fun createCustomMotionScheme(
    spatialDampingRatio: Float,
    spatialStiffness: Float,
    effectsDampingRatio: Float,
    effectsStiffness: Float,
): MotionScheme = object : MotionScheme {
    private val spatial = spring<Any>(
        dampingRatio = spatialDampingRatio.coerceIn(0.1f, 1f),
        stiffness = spatialStiffness.coerceIn(50f, 4_000f),
    )
    private val effects = spring<Any>(
        dampingRatio = effectsDampingRatio.coerceIn(0.1f, 1f),
        stiffness = effectsStiffness.coerceIn(50f, 4_000f),
    )
    private val fastSpatial = spring<Any>(
        dampingRatio = spatialDampingRatio.coerceIn(0.1f, 1f),
        stiffness = (spatialStiffness * 1.6f).coerceIn(50f, 4_000f),
    )
    private val slowSpatial = spring<Any>(
        dampingRatio = spatialDampingRatio.coerceIn(0.1f, 1f),
        stiffness = (spatialStiffness * 0.55f).coerceIn(50f, 4_000f),
    )
    private val fastEffects = spring<Any>(
        dampingRatio = effectsDampingRatio.coerceIn(0.1f, 1f),
        stiffness = (effectsStiffness * 1.6f).coerceIn(50f, 4_000f),
    )
    private val slowEffects = spring<Any>(
        dampingRatio = effectsDampingRatio.coerceIn(0.1f, 1f),
        stiffness = (effectsStiffness * 0.55f).coerceIn(50f, 4_000f),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T> defaultSpatialSpec(): FiniteAnimationSpec<T> = spatial as FiniteAnimationSpec<T>

    @Suppress("UNCHECKED_CAST")
    override fun <T> fastSpatialSpec(): FiniteAnimationSpec<T> = fastSpatial as FiniteAnimationSpec<T>

    @Suppress("UNCHECKED_CAST")
    override fun <T> slowSpatialSpec(): FiniteAnimationSpec<T> = slowSpatial as FiniteAnimationSpec<T>

    @Suppress("UNCHECKED_CAST")
    override fun <T> defaultEffectsSpec(): FiniteAnimationSpec<T> = effects as FiniteAnimationSpec<T>

    @Suppress("UNCHECKED_CAST")
    override fun <T> fastEffectsSpec(): FiniteAnimationSpec<T> = fastEffects as FiniteAnimationSpec<T>

    @Suppress("UNCHECKED_CAST")
    override fun <T> slowEffectsSpec(): FiniteAnimationSpec<T> = slowEffects as FiniteAnimationSpec<T>
}
