package com.test.design.component.core

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.test.design.component.theme.OemSpacing

/** Shared defaults for AAOS driving-safe components. */
object OemComponentDefaults {
    val minTouchTarget = OemSpacing.minTouchTarget
    const val minContrastRatio = 4.5f
    const val maxAnimationDurationMs = 200
}

@Composable
@ReadOnlyComposable
fun currentTouchTarget(): Dp =
    RestrictedComponentPolicy.touchTarget(LocalDrivingUxState.current)

fun Modifier.oemTouchTarget(): Modifier = defaultMinSize(
    minWidth = OemComponentDefaults.minTouchTarget,
    minHeight = OemComponentDefaults.minTouchTarget,
)

@Composable
fun Modifier.oemDrivingTouchTarget(): Modifier {
    val target = currentTouchTarget()
    return defaultMinSize(minWidth = target, minHeight = target)
}
