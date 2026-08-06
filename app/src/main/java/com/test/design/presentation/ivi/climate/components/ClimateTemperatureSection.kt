package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.TemperatureUnit
import com.test.design.presentation.ivi.climate.formatTemperature
import com.test.design.presentation.ivi.climate.toDisplayTemperature
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget
import com.test.design.theme.climateColorScheme
import com.test.design.theme.temperatureToFraction
import com.test.design.theme.zoneCoolIntensity

@Composable
fun ClimateTemperatureSection(
    temperature: Float,
    isAcEnabled: Boolean,
    dialShape: Shape,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    contentModifier: Modifier = Modifier,
    minTemperature: Float = 16f,
    maxTemperature: Float = 30f,
    temperatureStepCelsius: Float = 0.5f,
    temperatureStepFahrenheit: Float = 1f,
    minTemperatureFahrenheit: Float? = null,
    zoneLabel: String? = null,
    useZoneColorScheme: Boolean = false,
    temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
    onTemperatureSteps: ((Int) -> Unit)? = null,
    /** When set, snow only appears if this zone is cooler than (or equal to) the other. */
    otherZoneTemperature: Float? = null,
) {
    val fraction = temperatureToFraction(temperature, minTemperature, maxTemperature)
    val otherFraction = otherZoneTemperature?.let {
        temperatureToFraction(it, minTemperature, maxTemperature)
    } ?: fraction
    val coolIntensity = zoneCoolIntensity(fraction, otherFraction)
    val dialContent: @Composable () -> Unit = {
        ClimateTemperatureDialContent(
            temperature = temperature,
            isAcEnabled = isAcEnabled,
            dialShape = dialShape,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            compact = compact,
            contentModifier = contentModifier,
            minTemperature = minTemperature,
            maxTemperature = maxTemperature,
            temperatureStepCelsius = temperatureStepCelsius,
            temperatureStepFahrenheit = temperatureStepFahrenheit,
            minTemperatureFahrenheit = minTemperatureFahrenheit,
            zoneLabel = zoneLabel,
            temperatureUnit = temperatureUnit,
            onTemperatureSteps = onTemperatureSteps,
            coolIntensity = coolIntensity,
            modifier = modifier,
        )
    }

    if (useZoneColorScheme && !compact) {
        val zoneScheme = climateColorScheme(fraction, MaterialTheme.colorScheme)
        val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
        val animatedContainer by animateColorAsState(
            targetValue = zoneScheme.primaryContainer,
            animationSpec = motionSpec,
            label = "zone_dial_container",
        )
        MaterialTheme(
            colorScheme = zoneScheme.copy(
                primaryContainer = animatedContainer,
            ),
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            motionScheme = MaterialTheme.motionScheme,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides zoneScheme.onBackground,
            ) {
                dialContent()
            }
        }
    } else {
        dialContent()
    }
}

@Composable
private fun ClimateTemperatureDialContent(
    temperature: Float,
    isAcEnabled: Boolean,
    dialShape: Shape,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    compact: Boolean,
    contentModifier: Modifier,
    minTemperature: Float,
    maxTemperature: Float,
    temperatureStepCelsius: Float,
    temperatureStepFahrenheit: Float,
    minTemperatureFahrenheit: Float?,
    zoneLabel: String?,
    temperatureUnit: TemperatureUnit,
    onTemperatureSteps: ((Int) -> Unit)?,
    coolIntensity: Float,
    modifier: Modifier,
) {
    val dialSize = if (compact) 72.dp else 200.dp
    val buttonSize = if (compact) 36.dp else CarDesignTokens.MinTouchTarget
    val iconSize = if (compact) 18.dp else CarDesignTokens.PrimaryIcon
    val spacing = if (compact) 4.dp else CarDesignTokens.TouchTargetSpacing
    val displayLabel = formatTemperature(
        celsius = temperature,
        unit = temperatureUnit,
        celsiusStep = temperatureStepCelsius,
        minCelsius = minTemperature,
        minFahrenheit = minTemperatureFahrenheit,
        fahrenheitStep = temperatureStepFahrenheit,
    )
    val displaySortKey = temperature.toDisplayTemperature(
        unit = temperatureUnit,
        minCelsius = minTemperature,
        celsiusStep = temperatureStepCelsius,
        minFahrenheit = minTemperatureFahrenheit,
        fahrenheitStep = temperatureStepFahrenheit,
    )
    val stepHandler = onTemperatureSteps ?: { steps ->
        repeat(abs(steps)) {
            if (steps > 0) onIncrease() else onDecrease()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            if (compact) 0.dp else 8.dp,
            Alignment.CenterVertically,
        ),
    ) {
        if (!compact && zoneLabel != null) {
            Text(
                text = zoneLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TemperatureAdjustButton(
                icon = Icons.Default.Remove,
                contentDescription = "Decrease temperature",
                onClick = onDecrease,
                size = buttonSize,
                iconSize = iconSize,
            )
            Box(
                modifier = contentModifier
                    .size(dialSize)
                    .clip(dialShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f))
                    .temperatureVerticalDrag(
                        enabled = !compact,
                        onTemperatureSteps = stepHandler,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (!compact) {
                    CoolSnowflakeOverlay(
                        coolIntensity = coolIntensity,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedTemperatureCounter(
                        temperatureLabel = displayLabel,
                        compact = compact,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        sortKey = displaySortKey,
                    )
                    if (!compact) {
                        Text(
                            text = if (isAcEnabled) "A/C On" else "A/C Off",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
            TemperatureAdjustButton(
                icon = Icons.Default.Add,
                contentDescription = "Increase temperature",
                onClick = onIncrease,
                size = buttonSize,
                iconSize = iconSize,
            )
        }
    }
}

@Composable
fun TemperatureAdjustButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp = CarDesignTokens.MinTouchTarget,
    iconSize: Dp = CarDesignTokens.PrimaryIcon,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .carTouchTarget(),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
        )
    }
}

private fun abs(value: Int): Int = if (value < 0) -value else value
