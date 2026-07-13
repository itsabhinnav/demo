package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.test.design.presentation.ivi.climate.ClimateZone
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@Composable
fun ClimateZoneSelector(
    driverTemp: Int,
    passengerTemp: Int,
    activeZone: ClimateZone,
    onZoneSelected: (ClimateZone) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        ClimateZoneCard(
            label = ClimateZone.Driver.label,
            temperature = driverTemp,
            selected = activeZone == ClimateZone.Driver,
            onClick = { onZoneSelected(ClimateZone.Driver) },
            modifier = Modifier.weight(1f),
        )
        ClimateZoneCard(
            label = ClimateZone.Passenger.label,
            temperature = passengerTemp,
            selected = activeZone == ClimateZone.Passenger,
            onClick = { onZoneSelected(ClimateZone.Passenger) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun ClimateZoneCard(
    label: String,
    temperature: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Color>()
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = motionSpec,
        label = "zone_card_color",
    )
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .carTouchTarget()
            .clickable(onClick = onClick)
            .padding(CarDesignTokens.TouchTargetSpacing),
        verticalArrangement = if (centered) {
            Arrangement.Center
        } else {
            Arrangement.spacedBy(4.dp)
        },
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        )
        Text(
            text = "$temperature°",
            style = if (centered) {
                MaterialTheme.typography.displaySmall
            } else {
                MaterialTheme.typography.headlineMedium
            },
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        )
    }
}

@Composable
fun FanSpeedBars(
    fanSpeed: Int,
    maxFanSpeed: Int,
    onSpeedSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val motionSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
    val barSpacing = if (compact) 4.dp else 8.dp
    val baseHeight = if (compact) 12 else 24
    val heightStep = if (compact) 8 else 14
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        verticalAlignment = Alignment.Bottom,
    ) {
        repeat(maxFanSpeed) { index ->
            val level = index + 1
            val active = level <= fanSpeed
            val targetHeight = (baseHeight + level * heightStep).dp
            val animatedFraction by animateFloatAsState(
                targetValue = if (active) 1f else 0.35f,
                animationSpec = motionSpec,
                label = "fan_bar_$level",
            )
            val barShape = RoundedCornerShape(8.dp)
            val scheme = MaterialTheme.colorScheme
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(targetHeight)
                    .skeuomorphicFanBar(
                        shape = barShape,
                        active = active,
                        primary = scheme.primary.copy(alpha = 0.35f + animatedFraction * 0.65f),
                        container = scheme.surfaceContainerHigh,
                    )
                    .carTouchTarget()
                    .clickable { onSpeedSelected(level) },
            )
        }
    }
}

@Composable
fun SeatHeatIndicator(
    level: Int,
    maxLevel: Int,
    modifier: Modifier = Modifier,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(maxLevel) { index ->
            val active = index < level
            val scale by animateFloatAsState(
                targetValue = if (active) 1f else 0.5f,
                animationSpec = motionSpec,
                label = "seat_heat_$index",
            )
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height((28 * scale).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant,
                    ),
            )
        }
    }
}
