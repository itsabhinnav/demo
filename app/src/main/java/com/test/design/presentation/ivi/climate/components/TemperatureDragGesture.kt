package com.test.design.presentation.ivi.climate.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

/**
 * Vertical drag on a temperature control: drag up increases, drag down decreases.
 * Accumulates pixels into discrete 1° steps.
 */
fun Modifier.temperatureVerticalDrag(
    enabled: Boolean = true,
    pixelsPerStep: Float = 40f,
    onTemperatureSteps: (Int) -> Unit,
): Modifier {
    if (!enabled) return this
    return composed {
        var residual by remember { mutableFloatStateOf(0f) }
        val latestSteps by rememberUpdatedState(onTemperatureSteps)
        pointerInput(pixelsPerStep) {
            detectVerticalDragGestures(
                onDragEnd = { residual = 0f },
                onDragCancel = { residual = 0f },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    // Negative Y (finger up) → warmer.
                    residual += -dragAmount
                    if (abs(residual) >= pixelsPerStep) {
                        val steps = (residual / pixelsPerStep).toInt()
                        residual -= steps * pixelsPerStep
                        if (steps != 0) latestSteps(steps)
                    }
                },
            )
        }
    }
}
