package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.TemperatureUnit
import com.test.design.presentation.ivi.climate.toDisplayTemperature
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget
import com.test.design.theme.climateColorScheme
import com.test.design.theme.temperatureToFraction

@Composable
fun ClimateTemperatureSection(
    temperature: Int,
    isAcEnabled: Boolean,
    dialShape: Shape,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    contentModifier: Modifier = Modifier,
    minTemperature: Int = 16,
    maxTemperature: Int = 30,
    zoneLabel: String? = null,
    useZoneColorScheme: Boolean = false,
    temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
    onTemperatureSteps: ((Int) -> Unit)? = null,
) {
    val fraction = temperatureToFraction(temperature, minTemperature, maxTemperature)
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
            zoneLabel = zoneLabel,
            temperatureUnit = temperatureUnit,
            onTemperatureSteps = onTemperatureSteps,
            modifier = modifier,
        )
    }

    if (useZoneColorScheme && !compact) {
        val zoneScheme = climateColorScheme(fraction)
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
            dialContent()
        }
    } else {
        dialContent()
    }
}

@Composable
private fun ClimateTemperatureDialContent(
    temperature: Int,
    isAcEnabled: Boolean,
    dialShape: Shape,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    compact: Boolean,
    contentModifier: Modifier,
    minTemperature: Int,
    maxTemperature: Int,
    zoneLabel: String?,
    temperatureUnit: TemperatureUnit,
    onTemperatureSteps: ((Int) -> Unit)?,
    modifier: Modifier,
) {
    val dialSize = if (compact) 72.dp else 200.dp
    val buttonSize = if (compact) 36.dp else CarDesignTokens.MinTouchTarget
    val iconSize = if (compact) 18.dp else CarDesignTokens.PrimaryIcon
    val spacing = if (compact) 4.dp else CarDesignTokens.TouchTargetSpacing
    val coolIntensity = (
        (0.42f - temperatureToFraction(temperature, minTemperature, maxTemperature)) / 0.42f
        ).coerceIn(0f, 1f)
    val displayTemperature = temperature.toDisplayTemperature(temperatureUnit)
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
            val scheme = MaterialTheme.colorScheme
            Box(
                modifier = contentModifier
                    .size(dialSize)
                    .skeuomorphicDialShell(
                        shape = dialShape,
                        bezelLight = Color.White.copy(alpha = 0.55f),
                        bezelMid = scheme.outline,
                        bezelDark = scheme.surfaceContainerLowest,
                        faceHighlight = scheme.primaryContainer.copy(alpha = 0.95f),
                        face = scheme.primaryContainer.copy(alpha = 0.82f),
                        faceShadow = scheme.surfaceContainerLowest.copy(alpha = 0.9f),
                        tickColor = scheme.onPrimaryContainer,
                        elevation = if (compact) 6.dp else 18.dp,
                        showTicks = !compact,
                    )
                    .temperatureVerticalDrag(
                        enabled = !compact,
                        onTemperatureSteps = stepHandler,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (!compact) {
                    CoolSnowflakeOverlay(
                        coolIntensity = coolIntensity,
                        tint = scheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedTemperatureCounter(
                        temperature = displayTemperature,
                        compact = compact,
                        color = scheme.onPrimaryContainer,
                        unitSymbol = temperatureUnit.symbol,
                    )
                    if (!compact) {
                        Text(
                            text = if (isAcEnabled) "A/C On" else "A/C Off",
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onPrimaryContainer,
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
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(size)
            .skeuomorphicRaisedControl(
                shape = CircleShape,
                top = Color.White.copy(alpha = 0.28f).compositeOver(scheme.secondaryContainer),
                mid = scheme.secondaryContainer,
                bottom = scheme.surfaceContainerLowest,
                rim = Color.White,
                elevation = 8.dp,
            )
            .carTouchTarget()
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = scheme.onSecondaryContainer,
            modifier = Modifier.size(iconSize),
        )
    }
}

private fun abs(value: Int): Int = if (value < 0) -value else value
