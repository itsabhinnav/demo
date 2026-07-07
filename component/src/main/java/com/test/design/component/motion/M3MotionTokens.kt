package com.test.design.component.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * M3 easing and duration tokens for screen transitions.
 *
 * Component motion should use [MotionScheme] springs via [MaterialTheme.motionScheme];
 * these curves apply to navigation and container transitions per M3 guidance.
 *
 * @see <a href="https://m3.material.io/styles/motion/easing-and-duration">M3 Easing & Duration</a>
 */
object M3MotionTokens {
    val Emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    val Standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val StandardDecelerate = CubicBezierEasing(0f, 0f, 0f, 1f)
    val StandardAccelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)

    const val EmphasizedDurationMs = 500
    const val EmphasizedDecelerateDurationMs = 400
    const val EmphasizedAccelerateDurationMs = 200
    const val StandardDurationMs = 300
    const val StandardDecelerateDurationMs = 250
    const val StandardAccelerateDurationMs = 200

    fun <T> emphasizedTween(durationMs: Int = EmphasizedDurationMs): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMs, easing = Emphasized)

    fun <T> emphasizedDecelerateTween(durationMs: Int = EmphasizedDecelerateDurationMs): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMs, easing = EmphasizedDecelerate)

    fun <T> emphasizedAccelerateTween(durationMs: Int = EmphasizedAccelerateDurationMs): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMs, easing = EmphasizedAccelerate)

    fun <T> standardTween(durationMs: Int = StandardDurationMs): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMs, easing = Standard)

    @Composable
    fun rememberSpatialSpring(expressive: Boolean): FiniteAnimationSpec<Float> =
        remember(expressive) {
            OemMotionScheme.spatialSpringSpec(expressive = expressive)
        }

    @Composable
    fun rememberEffectsSpring(expressive: Boolean): FiniteAnimationSpec<Float> =
        remember(expressive) {
            OemMotionScheme.springSpec(
                if (expressive) {
                    OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultEffects)
                } else {
                    OemMotionScheme.standardPhysics(OemMotionScheme.SpringToken.DefaultEffects)
                },
            )
        }
}
