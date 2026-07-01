package com.test.design.component.motion

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.test.design.component.core.DrivingUxState
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState

/**
 * AAOS motion tokens aligned with [Design for Driving motion guidance](https://developers.google.com/cars/design/automotive-os/design-system/motion):
 * informative transitions, Material standard easing (fast out, slow in), and shorter close durations.
 */
object OemMotion {
    val StandardEasing = FastOutSlowInEasing

    const val OpenDurationMs = 250
    const val CloseDurationMs = 200
    const val DetailOpenDurationMs = 280
    const val DetailCloseDurationMs = 200
    const val SameLevelDurationMs = 250
    const val PressDurationMs = 100
    const val DisruptiveEnterMs = 300
    const val DisruptiveExitMs = 200

    fun durationMs(state: DrivingUxState, opening: Boolean, requestedMs: Int): Int {
        val cap = RestrictedComponentPolicy.maxAnimationDurationMs(state)
        if (cap == 0) return 0
        return minOf(requestedMs, cap)
    }

    fun <T> spec(durationMs: Int): FiniteAnimationSpec<T> =
        if (durationMs <= 0) snap() else tween(durationMillis = durationMs, easing = StandardEasing)

    @Composable
    fun pressSpec(): FiniteAnimationSpec<Float> {
        val duration = durationMs(
            state = currentDrivingUxState(),
            opening = true,
            requestedMs = PressDurationMs,
        )
        return spec(duration)
    }

    @Composable
    fun transitionSpec(requestedMs: Int, opening: Boolean = true): FiniteAnimationSpec<Float> {
        val duration = durationMs(
            state = currentDrivingUxState(),
            opening = opening,
            requestedMs = requestedMs,
        )
        return spec(duration)
    }
}
