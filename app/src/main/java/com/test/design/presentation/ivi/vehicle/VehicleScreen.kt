package com.test.design.presentation.ivi.vehicle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.test.design.presentation.common.ScreenBackground
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.vehicle.components.AnimatedStatCounter
import com.test.design.presentation.ivi.vehicle.components.MorphingDriveModeSelector
import com.test.design.presentation.ivi.vehicle.components.VehicleEnergyCockpit
import com.test.design.presentation.ivi.vehicle.components.VehicleMotionStudio
import com.test.design.presentation.ivi.vehicle.components.VehicleSystemsPanel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.VehicleCardActiveRadii
import com.test.design.theme.VehicleCardRestRadii
import com.test.design.theme.batteryToFraction
import com.test.design.theme.rememberVehicleGaugeShape
import com.test.design.theme.toMotionScheme
import com.test.design.theme.vehicleColorScheme

private const val MaxRangeMiles = 300

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.VehicleScreen(
    uiState: VehicleUiState,
    onEvent: (VehicleEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val batteryFraction = batteryToFraction(uiState.batteryPercent)
    val dynamicScheme = vehicleColorScheme(
        driveMode = uiState.driveMode,
        batteryFraction = batteryFraction,
        isCharging = uiState.isCharging,
    )
    val gaugeShape = rememberVehicleGaugeShape(
        sportMode = uiState.driveMode == DriveMode.Sport || uiState.isCharging,
    )
    val layoutProfile = uiState.driveMode.layoutProfile()

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = MaterialTheme.typography,
        shapes = ExpressiveShapes,
        motionScheme = uiState.screenMotionScheme.toMotionScheme(),
    ) {
        val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
        val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
        val animatedBackground by animateColorAsState(
            targetValue = dynamicScheme.background,
            animationSpec = effectsSpec,
            label = "vehicle_bg",
        )
        val energyWeight by animateFloatAsState(layoutProfile.energyWeight, animationSpec = spatialSpec, label = "energy_weight")
        val centerWeight by animateFloatAsState(layoutProfile.centerWeight, animationSpec = spatialSpec, label = "center_weight")
        val sideWeight by animateFloatAsState(layoutProfile.sideWeight, animationSpec = spatialSpec, label = "side_weight")
        val motionWeight by animateFloatAsState(layoutProfile.motionStudioWeight, animationSpec = spatialSpec, label = "motion_weight")
        val statsWeight by animateFloatAsState(layoutProfile.statsWeight, animationSpec = spatialSpec, label = "stats_weight")

        Box(
            modifier = widgetContainerTransform(
                widget = DashboardWidget.Vehicle,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier.fillMaxSize(),
            ),
        ) {
            ScreenBackground()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                animatedBackground.copy(alpha = 0.72f),
                                MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.72f),
                            ),
                        ),
                    )
                    .padding(CarDesignTokens.ContentPadding),
            ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                WidgetScreenHeader(
                    widget = DashboardWidget.Vehicle,
                    onBack = onBack,
                    animatedVisibilityScope = animatedVisibilityScope,
                    trailingContent = {
                        FilterChip(
                            selected = uiState.isCharging,
                            onClick = { onEvent(VehicleEvent.ToggleCharging) },
                            label = {
                                Text(
                                    text = if (uiState.isCharging) "Charging" else "Charge",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            },
                        )
                    },
                )

                DriveModeLayoutBanner(
                    driveMode = uiState.driveMode,
                    layoutLabel = layoutProfile.layoutLabel,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                ) {
                    VehicleEnergyCockpit(
                        percent = uiState.batteryPercent,
                        rangeMiles = uiState.rangeMiles,
                        maxRangeMiles = MaxRangeMiles,
                        isCharging = uiState.isCharging,
                        chargeRateKw = uiState.chargeRateKw,
                        gaugeShape = gaugeShape,
                        onGaugeClick = { onEvent(VehicleEvent.CycleBatteryDemo) },
                        modifier = Modifier
                            .weight(energyWeight)
                            .fillMaxHeight(),
                    )

                    CenterDriveColumn(
                        layoutProfile = layoutProfile,
                        uiState = uiState,
                        onEvent = onEvent,
                        modifier = Modifier
                            .weight(centerWeight)
                            .fillMaxHeight(),
                    )

                    SideInsightsColumn(
                        layoutProfile = layoutProfile,
                        uiState = uiState,
                        onEvent = onEvent,
                        motionWeight = motionWeight,
                        statsWeight = statsWeight,
                        modifier = Modifier
                            .weight(sideWeight)
                            .fillMaxHeight(),
                    )
                }
            }
            }
        }
    }
}

@Composable
private fun DriveModeLayoutBanner(
    driveMode: DriveMode,
    layoutLabel: String,
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    AnimatedContent(
        targetState = driveMode,
        transitionSpec = {
            fadeIn(animationSpec = effectsSpec) togetherWith fadeOut(animationSpec = effectsSpec)
        },
        label = "drive_mode_layout_banner",
    ) { mode ->
        Surface(
            shape = ExpressiveShapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(mode.label, style = MaterialTheme.typography.titleSmall)
                    Text(
                        mode.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    layoutLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun CenterDriveColumn(
    layoutProfile: VehicleDriveModeLayout,
    uiState: VehicleUiState,
    onEvent: (VehicleEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<androidx.compose.ui.unit.IntSize>()
    Column(
        modifier = modifier.animateContentSize(animationSpec = spatialSpec),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        val driveSelector = @Composable {
            MorphingDriveModeSelector(
                selected = uiState.driveMode,
                onSelected = { onEvent(VehicleEvent.SelectDriveMode(it)) },
            )
        }
        val systemsPanel = @Composable {
            VehicleSystemsPanel(
                systems = uiState.systems,
                regenLevel = uiState.regenLevel,
                selectedSystemId = uiState.selectedSystemId,
                isCharging = uiState.isCharging,
                onRegenClick = { onEvent(VehicleEvent.CycleRegenLevel) },
                onSystemClick = { onEvent(VehicleEvent.SelectSystem(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }

        if (layoutProfile.driveSelectorFirst) {
            driveSelector()
            systemsPanel()
        } else {
            systemsPanel()
            driveSelector()
        }
    }
}

@Composable
private fun SideInsightsColumn(
    layoutProfile: VehicleDriveModeLayout,
    uiState: VehicleUiState,
    onEvent: (VehicleEvent) -> Unit,
    motionWeight: Float,
    statsWeight: Float,
    modifier: Modifier = Modifier,
) {
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<androidx.compose.ui.unit.IntSize>()
    Column(
        modifier = modifier.animateContentSize(animationSpec = spatialSpec),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        if (layoutProfile.showMotionStudio && uiState.driveMode == DriveMode.Sport) {
            VehicleMotionStudio(
                selectedScheme = uiState.screenMotionScheme,
                activeToken = uiState.activeMotionToken,
                previewTrigger = uiState.motionPreviewTrigger,
                onSchemeSelected = { onEvent(VehicleEvent.SelectScreenMotionScheme(it)) },
                onTokenSelected = { onEvent(VehicleEvent.SelectMotionToken(it)) },
                onReplay = { onEvent(VehicleEvent.ReplayMotionPreview) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(motionWeight.coerceAtLeast(0.01f)),
            )
            VehicleStatsPanel(
                efficiencyMpkWh = uiState.efficiencyMpkWh,
                odometerMiles = uiState.odometerMiles,
                tripEnergyKwh = uiState.tripEnergyKwh,
                driveMode = uiState.driveMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(statsWeight.coerceAtLeast(0.01f)),
            )
        } else {
            VehicleStatsPanel(
                efficiencyMpkWh = uiState.efficiencyMpkWh,
                odometerMiles = uiState.odometerMiles,
                tripEnergyKwh = uiState.tripEnergyKwh,
                driveMode = uiState.driveMode,
                modifier = Modifier.fillMaxWidth(),
            )
            AnimatedVisibility(
                visible = layoutProfile.showMotionStudio,
                enter = expandVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()) +
                    fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
                exit = shrinkVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()) +
                    fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                VehicleMotionStudio(
                    selectedScheme = uiState.screenMotionScheme,
                    activeToken = uiState.activeMotionToken,
                    previewTrigger = uiState.motionPreviewTrigger,
                    onSchemeSelected = { onEvent(VehicleEvent.SelectScreenMotionScheme(it)) },
                    onTokenSelected = { onEvent(VehicleEvent.SelectMotionToken(it)) },
                    onReplay = { onEvent(VehicleEvent.ReplayMotionPreview) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun VehicleStatsPanel(
    efficiencyMpkWh: Float,
    odometerMiles: Int,
    tripEnergyKwh: Float,
    driveMode: DriveMode,
    modifier: Modifier = Modifier,
) {
    MorphingDetailSurfaceCard(
        morphExpanded = driveMode == DriveMode.Sport,
        compactRadii = VehicleCardRestRadii,
        expandedRadii = VehicleCardActiveRadii,
        modifier = modifier,
    ) {
        Text("Trip", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "$efficiencyMpkWh mi/kWh",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Odometer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AnimatedStatCounter(
                    value = odometerMiles,
                    suffix = " mi",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Trip use", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "$tripEnergyKwh kWh",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
