package com.test.design.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.test.design.component.core.DrivingUxState
import com.test.design.component.motion.OemMotion

/** Navigation transitions mapped to AAOS motion patterns. */
object NavMotion {

    fun detailEnter(state: DrivingUxState = DrivingUxState.Parked): EnterTransition {
        val duration = OemMotion.durationMs(state, opening = true, OemMotion.DetailOpenDurationMs)
        if (duration == 0) return EnterTransition.None
        val spec = tween<Float>(durationMillis = duration, easing = OemMotion.StandardEasing)
        return scaleIn(initialScale = 0.94f, animationSpec = spec) + fadeIn(animationSpec = spec)
    }

    fun detailExit(state: DrivingUxState = DrivingUxState.Parked): ExitTransition {
        val duration = OemMotion.durationMs(state, opening = false, OemMotion.DetailCloseDurationMs)
        if (duration == 0) return ExitTransition.None
        val spec = tween<Float>(durationMillis = duration, easing = OemMotion.StandardEasing)
        return scaleOut(targetScale = 0.94f, animationSpec = spec) + fadeOut(animationSpec = spec)
    }

    fun sameLevelEnter(state: DrivingUxState = DrivingUxState.Parked): EnterTransition {
        val duration = OemMotion.durationMs(state, opening = true, OemMotion.SameLevelDurationMs)
        if (duration == 0) return EnterTransition.None
        val slideSpec = tween<IntOffset>(durationMillis = duration, easing = OemMotion.StandardEasing)
        val fadeSpec = tween<Float>(durationMillis = duration, easing = OemMotion.StandardEasing)
        return slideInHorizontally(animationSpec = slideSpec) { fullWidth -> fullWidth / 4 } +
            fadeIn(animationSpec = fadeSpec)
    }

    fun sameLevelExit(state: DrivingUxState = DrivingUxState.Parked): ExitTransition {
        val duration = OemMotion.durationMs(state, opening = false, OemMotion.CloseDurationMs)
        if (duration == 0) return ExitTransition.None
        val slideSpec = tween<IntOffset>(durationMillis = duration, easing = OemMotion.StandardEasing)
        val fadeSpec = tween<Float>(durationMillis = duration, easing = OemMotion.StandardEasing)
        return slideOutHorizontally(animationSpec = slideSpec) { fullWidth -> -fullWidth / 4 } +
            fadeOut(animationSpec = fadeSpec)
    }
}
