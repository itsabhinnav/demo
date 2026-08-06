package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ClimateCardActiveRadii
import com.test.design.theme.ClimateCardRestRadii
import com.test.design.theme.carTouchTarget

/**
 * Icon-only comfort strip — horizontal OEM climate controls with active/inactive states.
 */
@Composable
fun ClimateComfortControlsCard(
    seatHeatLevel: Int,
    maxSeatHeatLevel: Int,
    steeringHeatLevel: Int,
    maxSteeringHeatLevel: Int,
    seatVentLevel: Int,
    maxSeatVentLevel: Int,
    isFrontDefrostOn: Boolean,
    isRearDefrostOn: Boolean,
    isRecirculationOn: Boolean,
    isSyncEnabled: Boolean,
    isAcEnabled: Boolean,
    onCycleSeatHeat: () -> Unit,
    onCycleSteeringHeat: () -> Unit,
    onCycleSeatVent: () -> Unit,
    onToggleFrontDefrost: () -> Unit,
    onToggleRearDefrost: () -> Unit,
    onToggleRecirculation: () -> Unit,
    onToggleSync: () -> Unit,
    modifier: Modifier = Modifier,
    showSeatHeat: Boolean = true,
    showSteeringHeat: Boolean = true,
    showSeatVent: Boolean = true,
    showFrontDefrost: Boolean = true,
    showRearDefrost: Boolean = true,
    showRecirculation: Boolean = true,
    showSync: Boolean = true,
) {
    MorphingDetailSurfaceCard(
        morphExpanded = isAcEnabled,
        compactRadii = ClimateCardRestRadii,
        expandedRadii = ClimateCardActiveRadii,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showSeatHeat) {
                ComfortIconLevelButton(
                    icon = ClimateHvacIcons.SeatHeat,
                    contentDescription = "Seat heat",
                    level = seatHeatLevel,
                    maxLevel = maxSeatHeatLevel,
                    onClick = onCycleSeatHeat,
                )
            }
            if (showSteeringHeat) {
                ComfortIconLevelButton(
                    icon = ClimateHvacIcons.SteeringHeat,
                    contentDescription = "Wheel heat",
                    level = steeringHeatLevel,
                    maxLevel = maxSteeringHeatLevel,
                    onClick = onCycleSteeringHeat,
                )
            }
            if (showSeatVent) {
                ComfortIconLevelButton(
                    icon = ClimateHvacIcons.SeatVent,
                    contentDescription = "Seat vent",
                    level = seatVentLevel,
                    maxLevel = maxSeatVentLevel,
                    onClick = onCycleSeatVent,
                )
            }
            if (showFrontDefrost) {
                ComfortIconToggle(
                    icon = ClimateHvacIcons.FrontDefrost,
                    contentDescription = "Front defrost",
                    active = isFrontDefrostOn,
                    onClick = onToggleFrontDefrost,
                )
            }
            if (showRearDefrost) {
                ComfortIconToggle(
                    icon = ClimateHvacIcons.RearDefrost,
                    contentDescription = "Rear defrost",
                    active = isRearDefrostOn,
                    onClick = onToggleRearDefrost,
                )
            }
            if (showRecirculation) {
                ComfortIconToggle(
                    icon = ClimateHvacIcons.FreshAir,
                    contentDescription = "Fresh air",
                    active = !isRecirculationOn,
                    onClick = {
                        if (isRecirculationOn) onToggleRecirculation()
                    },
                )
                ComfortIconToggle(
                    icon = ClimateHvacIcons.Recirculation,
                    contentDescription = "Recirculation",
                    active = isRecirculationOn,
                    onClick = onToggleRecirculation,
                )
            }
            if (showSync) {
                ComfortIconToggle(
                    icon = ClimateHvacIcons.SyncZones,
                    contentDescription = "Sync zones",
                    active = isSyncEnabled,
                    onClick = onToggleSync,
                )
            }
        }
    }
}

@Composable
private fun ComfortIconLevelButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    level: Int,
    maxLevel: Int,
    onClick: () -> Unit,
) {
    val active = level > 0
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val container by animateColorAsState(
        targetValue = if (active) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = motionSpec,
        label = "comfort_level_bg_$contentDescription",
    )
    val content by animateColorAsState(
        targetValue = if (active) {
            MaterialTheme.colorScheme.onTertiaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = motionSpec,
        label = "comfort_level_fg_$contentDescription",
    )
    val border by animateColorAsState(
        targetValue = if (active) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
        animationSpec = motionSpec,
        label = "comfort_level_border_$contentDescription",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(CarDesignTokens.MinTouchTarget)
                .clip(CircleShape)
                .background(container)
                .border(1.5.dp, border, CircleShape)
                .carTouchTarget()
                .semantics {
                    role = Role.Button
                    this.contentDescription =
                        "$contentDescription, ${if (active) "level $level" else "off"}"
                }
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
            )
        }
        ComfortLevelBars(
            level = level,
            maxLevel = maxLevel,
            activeColor = content,
            inactiveColor = content.copy(alpha = 0.25f),
        )
    }
}

@Composable
private fun ComfortIconToggle(
    @DrawableRes icon: Int,
    contentDescription: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val container by animateColorAsState(
        targetValue = if (active) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = motionSpec,
        label = "comfort_toggle_bg_$contentDescription",
    )
    val content by animateColorAsState(
        targetValue = if (active) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = motionSpec,
        label = "comfort_toggle_fg_$contentDescription",
    )
    val border by animateColorAsState(
        targetValue = if (active) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
        animationSpec = motionSpec,
        label = "comfort_toggle_border_$contentDescription",
    )

    Box(
        modifier = Modifier
            .size(CarDesignTokens.MinTouchTarget)
            .clip(CircleShape)
            .background(container)
            .border(1.5.dp, border, CircleShape)
            .carTouchTarget()
            .semantics {
                role = Role.Switch
                this.contentDescription =
                    "$contentDescription ${if (active) "on" else "off"}"
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
        )
    }
}

@Composable
private fun ComfortLevelBars(
    level: Int,
    maxLevel: Int,
    activeColor: Color,
    inactiveColor: Color,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(maxLevel) { index ->
            val lit = index < level
            val alpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = motionSpec,
                label = "comfort_bar_$index",
            )
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background((if (lit) activeColor else inactiveColor).copy(alpha = alpha)),
            )
        }
    }
}
