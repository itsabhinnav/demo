package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.launch

/** Velocity (px/s) above which a fling snaps the widget panel fully open or closed. */
internal const val WidgetsRevealFlingVelocity = 900f

/**
 * Snap target for the widgets reveal fraction after a drag or fling ends.
 * 1 = widgets fully shown, 0 = hidden (map full width).
 */
internal fun widgetsRevealSnapTarget(
    currentFraction: Float,
    velocityPxPerSec: Float,
    velocityThreshold: Float = WidgetsRevealFlingVelocity,
): Float = when {
    velocityPxPerSec < -velocityThreshold -> 0f
    velocityPxPerSec > velocityThreshold -> 1f
    else -> if (currentFraction >= 0.5f) 1f else 0f
}

/**
 * Horizontal drag that scrubs [reveal] between 0 (hidden) and 1 (shown).
 * Swipe left hides widgets; swipe right shows them.
 */
fun Modifier.widgetsRevealDrag(
    reveal: Animatable<Float, AnimationVector1D>,
    panelWidthPx: Float,
    enabled: Boolean = true,
): Modifier {
    if (!enabled || panelWidthPx <= 0f) return this
    return composed {
        val scope = rememberCoroutineScope()
        val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
        val dragState = rememberDraggableState { delta ->
            scope.launch {
                val next = (reveal.value + delta / panelWidthPx).coerceIn(0f, 1f)
                reveal.snapTo(next)
            }
        }
        Modifier.draggable(
            state = dragState,
            orientation = Orientation.Horizontal,
            onDragStopped = { velocity ->
                scope.launch {
                    val target = widgetsRevealSnapTarget(
                        currentFraction = reveal.value,
                        velocityPxPerSec = velocity,
                    )
                    reveal.animateTo(target, spatialSpec)
                }
            },
        )
    }
}

@Composable
fun rememberWidgetsReveal(
    initial: Float = 1f,
): Animatable<Float, AnimationVector1D> = remember { Animatable(initial) }
