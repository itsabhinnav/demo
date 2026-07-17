package com.test.design.presentation.ivi.vehicle

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.test.design.presentation.common.ScreenBackground
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.common.SimulatedBadge
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.vehicle.components.AnimatedStatCounter
import com.test.design.presentation.ivi.vehicle.components.MorphingDriveModeSelector
import com.test.design.presentation.ivi.vehicle.components.VehicleDriveInsightsCard
import com.test.design.presentation.ivi.vehicle.components.VehicleEnergyCockpit
import com.test.design.presentation.ivi.vehicle.components.VehicleSystemsPanel
import com.test.design.theme.AdaptiveLayout
import com.test.design.theme.CarBackgroundTokens
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.VehicleCardActiveRadii
import com.test.design.theme.VehicleCardRestRadii
import com.test.design.theme.WindowLayoutInfo
import com.test.design.theme.batteryToFraction
import com.test.design.theme.rememberVehicleGaugeShape
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
        base = MaterialTheme.colorScheme,
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
        motionScheme = MaterialTheme.motionScheme,
    ) {
        CompositionLocalProvider(LocalContentColor provides dynamicScheme.onBackground) {
        val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
        val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
        val animatedPrimary by animateColorAsState(
            targetValue = dynamicScheme.primary,
            animationSpec = effectsSpec,
            label = "vehicle_tint",
        )
        val energyWeight by animateFloatAsState(layoutProfile.energyWeight, animationSpec = spatialSpec, label = "energy_weight")
        val centerWeight by animateFloatAsState(layoutProfile.centerWeight, animationSpec = spatialSpec, label = "center_weight")
        val sideWeight by animateFloatAsState(layoutProfile.sideWeight, animationSpec = spatialSpec, label = "side_weight")

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
                                animatedPrimary.copy(alpha = CarBackgroundTokens.DetailTintAlpha),
                                MaterialTheme.colorScheme.surfaceContainerLow.copy(
                                    alpha = CarBackgroundTokens.DetailOverlayAlpha,
                                ),
                            ),
                        ),
                    )
                    .padding(CarDesignTokens.ContentPadding),
            ) {
            AdaptiveLayout(modifier = Modifier.fillMaxSize()) { layout ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (!layout.useTriplePane) {
                                Modifier.verticalScroll(rememberScrollState())
                            } else {
                                Modifier
                            },
                        ),
                    verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                ) {
                    WidgetScreenHeader(
                        widget = DashboardWidget.Vehicle,
                        onBack = onBack,
                        animatedVisibilityScope = animatedVisibilityScope,
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                SimulatedBadge()
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
                            }
                        },
                    )

                    VehicleAdaptiveBody(
                        layout = layout,
                        layoutProfile = layoutProfile,
                        uiState = uiState,
                        onEvent = onEvent,
                        energyWeight = energyWeight,
                        centerWeight = centerWeight,
                        sideWeight = sideWeight,
                        gaugeShape = gaugeShape,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = if (layout.useTriplePane) {
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        } else {
                            Modifier.fillMaxWidth()
                        },
                    )
                }
            }
            }
        }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.VehicleAdaptiveBody(
    layout: WindowLayoutInfo,
    layoutProfile: VehicleDriveModeLayout,
    uiState: VehicleUiState,
    onEvent: (VehicleEvent) -> Unit,
    energyWeight: Float,
    centerWeight: Float,
    sideWeight: Float,
    gaugeShape: androidx.compose.ui.graphics.Shape,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val energy: @Composable (Modifier) -> Unit = { paneModifier ->
        VehicleEnergyCockpit(
            percent = uiState.batteryPercent,
            rangeMiles = uiState.rangeMiles,
            maxRangeMiles = MaxRangeMiles,
            isCharging = uiState.isCharging,
            chargeRateKw = uiState.chargeRateKw,
            gaugeShape = gaugeShape,
            onGaugeClick = { onEvent(VehicleEvent.CycleBatteryDemo) },
            contentModifier = widgetContentSharedElement(
                widget = DashboardWidget.Vehicle,
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            controlsModifier = widgetControlsSharedElement(
                widget = DashboardWidget.Vehicle,
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            modifier = paneModifier,
        )
    }
    val center: @Composable (Modifier) -> Unit = { paneModifier ->
        CenterDriveColumn(
            layoutProfile = layoutProfile,
            uiState = uiState,
            onEvent = onEvent,
            modifier = paneModifier,
        )
    }
    val side: @Composable (Modifier) -> Unit = { paneModifier ->
        SideInsightsColumn(
            layoutProfile = layoutProfile,
            uiState = uiState,
            modifier = paneModifier,
        )
    }

    when {
        layout.useTriplePane -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                energy(Modifier.weight(energyWeight).fillMaxHeight())
                center(Modifier.weight(centerWeight).fillMaxHeight())
                side(Modifier.weight(sideWeight).fillMaxHeight())
            }
        }
        layout.useSideBySide -> {
            Row(
                modifier = modifier.height(480.dp),
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                energy(Modifier.weight(energyWeight).fillMaxHeight())
                Column(
                    modifier = Modifier
                        .weight(centerWeight + sideWeight)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                ) {
                    center(Modifier.fillMaxWidth().weight(1f))
                    side(Modifier.fillMaxWidth().weight(1f))
                }
            }
        }
        else -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                energy(Modifier.fillMaxWidth())
                center(
                    Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                )
                side(Modifier.fillMaxWidth())
            }
        }
    }
}

private val DriveSelectorFallbackHeight = 132.dp

@Composable
private fun CenterDriveColumn(
    layoutProfile: VehicleDriveModeLayout,
    uiState: VehicleUiState,
    onEvent: (VehicleEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectorHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val measuredSelectorHeight = with(density) { selectorHeightPx.toDp() }
    val selectorSlotHeight = if (measuredSelectorHeight > 0.dp) {
        measuredSelectorHeight
    } else {
        DriveSelectorFallbackHeight
    }
    val motionSpec = MaterialTheme.motionScheme.slowSpatialSpec<androidx.compose.ui.unit.Dp>()
    val sectionGap = CarDesignTokens.TouchTargetSpacing

    BoxWithConstraints(
        modifier = modifier.fillMaxHeight(),
    ) {
        val targetTopPadding = if (layoutProfile.driveSelectorFirst) selectorSlotHeight + sectionGap else 0.dp
        val targetBottomPadding = if (layoutProfile.driveSelectorFirst) 0.dp else selectorSlotHeight + sectionGap
        val selectorYOffset by animateDpAsState(
            targetValue = if (layoutProfile.driveSelectorFirst) {
                0.dp
            } else {
                (maxHeight - selectorSlotHeight).coerceAtLeast(0.dp)
            },
            animationSpec = motionSpec,
            label = "drive_selector_offset",
        )
        val systemsTopPadding by animateDpAsState(
            targetValue = targetTopPadding,
            animationSpec = motionSpec,
            label = "systems_top_padding",
        )
        val systemsBottomPadding by animateDpAsState(
            targetValue = targetBottomPadding,
            animationSpec = motionSpec,
            label = "systems_bottom_padding",
        )
        val safeTopPadding = systemsTopPadding.coerceAtLeast(0.dp)
        val safeBottomPadding = systemsBottomPadding.coerceAtLeast(0.dp)

        VehicleSystemsPanel(
            systems = uiState.systems,
            regenLevel = uiState.regenLevel,
            selectedSystemId = uiState.selectedSystemId,
            isCharging = uiState.isCharging,
            onRegenClick = { onEvent(VehicleEvent.CycleRegenLevel) },
            onSystemClick = { onEvent(VehicleEvent.SelectSystem(it)) },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = safeTopPadding, bottom = safeBottomPadding),
        )

        MorphingDriveModeSelector(
            selected = uiState.driveMode,
            onSelected = { onEvent(VehicleEvent.SelectDriveMode(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = selectorYOffset)
                .onSizeChanged { selectorHeightPx = it.height }
                .zIndex(1f),
        )
    }
}

@Composable
private fun SideInsightsColumn(
    layoutProfile: VehicleDriveModeLayout,
    uiState: VehicleUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        VehicleStatsPanel(
            efficiencyMpkWh = uiState.efficiencyMpkWh,
            odometerMiles = uiState.odometerMiles,
            tripEnergyKwh = uiState.tripEnergyKwh,
            driveMode = uiState.driveMode,
            modifier = Modifier.fillMaxWidth(),
        )
        if (layoutProfile.showDriveInsights) {
            VehicleDriveInsightsCard(
                driveMode = uiState.driveMode,
                regenLevel = uiState.regenLevel,
                efficiencyMpkWh = uiState.efficiencyMpkWh,
                rangeMiles = uiState.rangeMiles,
                modifier = Modifier.fillMaxWidth(),
            )
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
        Text(
            "Trip",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "$efficiencyMpkWh",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "mi/kWh",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "Odometer",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AnimatedStatCounter(
                    value = odometerMiles,
                    suffix = " mi",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    "Used",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "$tripEnergyKwh kWh",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
