package com.test.design.presentation.ivi.vehicle.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.test.design.core.motion.AppMotionScheme
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import kotlin.math.roundToInt

private enum class VehicleMotionToken(val label: String) {
    DefaultSpatial("Spatial"),
    FastSpatial("Fast"),
    SlowSpatial("Slow"),
    DefaultEffects("Effects"),
    FastEffects("Snappy"),
    SlowEffects("Gentle"),
}

@Composable
fun VehicleMotionStudio(
    selectedScheme: AppMotionScheme,
    activeToken: Int,
    previewTrigger: Int,
    onSchemeSelected: (AppMotionScheme) -> Unit,
    onTokenSelected: (Int) -> Unit,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ExpressiveShapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
        shadowElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CarDesignTokens.TouchTargetSpacing),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Motion studio", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Screen-local MaterialTheme.motionScheme",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Button(onClick = onReplay) {
                    Text("Replay", style = MaterialTheme.typography.labelLarge)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppMotionScheme.entries.forEach { scheme ->
                    FilterChip(
                        selected = selectedScheme == scheme,
                        onClick = { onSchemeSelected(scheme) },
                        label = { Text(scheme.label, style = MaterialTheme.typography.labelLarge) },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VehicleMotionToken.entries.forEachIndexed { index, token ->
                    MotionTokenCell(
                        token = token,
                        index = index,
                        isActive = activeToken == index,
                        previewTrigger = previewTrigger,
                        onClick = { onTokenSelected(index) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MotionTokenCell(
    token: VehicleMotionToken,
    index: Int,
    isActive: Boolean,
    previewTrigger: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    val spatialOffset by animateDpAsState(
        targetValue = if (previewTrigger % 2 == 0) 6.dp else 72.dp,
        animationSpec = when (token) {
            VehicleMotionToken.DefaultSpatial -> motionScheme.defaultSpatialSpec()
            VehicleMotionToken.FastSpatial -> motionScheme.fastSpatialSpec()
            VehicleMotionToken.SlowSpatial -> motionScheme.slowSpatialSpec()
            else -> motionScheme.defaultSpatialSpec()
        },
        label = "vehicle_token_spatial_$index",
    )
    val effectsScale by animateFloatAsState(
        targetValue = if (previewTrigger % 2 == 0) 0.55f else 1f,
        animationSpec = when (token) {
            VehicleMotionToken.DefaultEffects -> motionScheme.defaultEffectsSpec()
            VehicleMotionToken.FastEffects -> motionScheme.fastEffectsSpec()
            VehicleMotionToken.SlowEffects -> motionScheme.slowEffectsSpec()
            else -> motionScheme.defaultEffectsSpec()
        },
        label = "vehicle_token_effects_$index",
    )
    val containerColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "token_cell_bg_$index",
    )

    Column(
        modifier = modifier
            .clip(ExpressiveShapes.small)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            token.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(ExpressiveShapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f)),
        ) {
            if (token.ordinal <= VehicleMotionToken.SlowSpatial.ordinal) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(spatialOffset.roundToPx(), 14.dp.roundToPx()) }
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            } else {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                        .scale(effectsScale)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary),
                )
            }
        }
    }
}
