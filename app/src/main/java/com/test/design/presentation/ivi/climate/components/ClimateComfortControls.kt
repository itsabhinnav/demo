package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
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
) {
    MorphingDetailSurfaceCard(
        morphExpanded = isAcEnabled,
        compactRadii = ClimateCardRestRadii,
        expandedRadii = ClimateCardActiveRadii,
        emphasized = true,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ComfortIconLevelButton(
                icon = SeatHeatIcon,
                contentDescription = "Seat heat",
                level = seatHeatLevel,
                maxLevel = maxSeatHeatLevel,
                onClick = onCycleSeatHeat,
            )
            ComfortIconLevelButton(
                icon = SteeringWheelIcon,
                contentDescription = "Wheel heat",
                level = steeringHeatLevel,
                maxLevel = maxSteeringHeatLevel,
                onClick = onCycleSteeringHeat,
            )
            ComfortIconLevelButton(
                icon = SeatVentIcon,
                contentDescription = "Seat vent",
                level = seatVentLevel,
                maxLevel = maxSeatVentLevel,
                onClick = onCycleSeatVent,
            )
            ComfortIconToggle(
                icon = FrontDefrostIcon,
                contentDescription = "Front defrost",
                active = isFrontDefrostOn,
                onClick = onToggleFrontDefrost,
            )
            ComfortIconToggle(
                icon = RearDefrostIcon,
                contentDescription = "Rear defrost",
                active = isRearDefrostOn,
                onClick = onToggleRearDefrost,
            )
            ComfortIconToggle(
                icon = FreshAirIcon,
                contentDescription = "Fresh air",
                active = !isRecirculationOn,
                onClick = {
                    if (isRecirculationOn) onToggleRecirculation()
                },
            )
            ComfortIconToggle(
                icon = RecirculationIcon,
                contentDescription = "Recirculation",
                active = isRecirculationOn,
                onClick = onToggleRecirculation,
            )
            ComfortIconToggle(
                icon = Icons.Default.Sync,
                contentDescription = "Sync zones",
                active = isSyncEnabled,
                onClick = onToggleSync,
            )
        }
    }
}

@Composable
private fun ComfortIconLevelButton(
    icon: ImageVector,
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
                .skeuomorphicRaisedControl(
                    shape = CircleShape,
                    top = if (active) {
                        Color.White.copy(alpha = 0.22f).compositeOver(container)
                    } else {
                        Color.White.copy(alpha = 0.12f).compositeOver(container)
                    },
                    mid = container,
                    bottom = MaterialTheme.colorScheme.surfaceContainerLowest,
                rim = if (active) Color.White else border,
                elevation = if (active) 10.dp else 4.dp,
                pressed = false,
            )
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
                imageVector = icon,
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
    icon: ImageVector,
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
            .skeuomorphicRaisedControl(
                shape = CircleShape,
                top = if (active) {
                    Color.White.copy(alpha = 0.28f).compositeOver(container)
                } else {
                    Color.White.copy(alpha = 0.12f).compositeOver(container)
                },
                mid = container,
                bottom = MaterialTheme.colorScheme.surfaceContainerLowest,
                rim = if (active) Color.White else border,
                elevation = if (active) 8.dp else 4.dp,
                pressed = active,
            )
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
            imageVector = icon,
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
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(maxLevel) { index ->
            val lit = index < level
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 4.dp)
                    .skeuomorphicFanBar(
                        shape = RoundedCornerShape(2.dp),
                        active = lit,
                        primary = activeColor,
                        container = inactiveColor,
                    ),
            )
        }
    }
}
