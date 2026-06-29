package com.test.design.component.core

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import com.test.design.component.theme.NissanSpacing

/** Shared defaults for AAOS driving-safe components. */
object OemComponentDefaults {
    val minTouchTarget = NissanSpacing.minTouchTarget
    const val minContrastRatio = 4.5f
    const val maxAnimationDurationMs = 200
}

fun Modifier.oemTouchTarget(): Modifier = defaultMinSize(
    minWidth = OemComponentDefaults.minTouchTarget,
    minHeight = OemComponentDefaults.minTouchTarget,
)
