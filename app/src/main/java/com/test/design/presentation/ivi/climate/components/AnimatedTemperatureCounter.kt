package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset

@Composable
fun AnimatedTemperatureCounter(
    temperature: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()

    AnimatedContent(
        targetState = temperature,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            slideInVertically(animationSpec = motionSpec) { height -> direction * height } togetherWith
                slideOutVertically(animationSpec = motionSpec) { height -> -direction * height }
        },
        label = "temperature_counter",
    ) { targetTemp ->
        Text(
            text = "$targetTemp°",
            style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}
