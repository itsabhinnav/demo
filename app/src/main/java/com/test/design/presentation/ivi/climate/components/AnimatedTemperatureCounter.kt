package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset

@Composable
fun AnimatedTemperatureCounter(
    temperature: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
    unitSymbol: String = "°",
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()

    AnimatedContent(
        targetState = temperature to unitSymbol,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState.first > initialState.first) 1 else -1
            slideInVertically(animationSpec = motionSpec) { height -> direction * height } togetherWith
                slideOutVertically(animationSpec = motionSpec) { height -> -direction * height }
        },
        label = "temperature_counter",
    ) { (targetTemp, symbol) ->
        Text(
            text = "$targetTemp$symbol",
            style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displayLarge,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}
