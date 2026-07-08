package com.test.design.presentation.ivi.vehicle

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = MaterialTheme.typography,
        shapes = ExpressiveShapes,
        motionScheme = uiState.screenMotionScheme.toMotionScheme(),
    ) {
        val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
        val animatedBackground by animateColorAsState(
            targetValue = dynamicScheme.background,
            animationSpec = motionSpec,
            label = "vehicle_bg",
        )

        Box(
            modifier = widgetContainerTransform(
                widget = DashboardWidget.Vehicle,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier.fillMaxSize(),
            )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            animatedBackground,
                            MaterialTheme.colorScheme.surfaceContainerLow,
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
                            .weight(0.36f)
                            .fillMaxHeight(),
                    )

                    Column(
                        modifier = Modifier
                            .weight(0.34f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                    ) {
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
                        MorphingDriveModeSelector(
                            selected = uiState.driveMode,
                            onSelected = { onEvent(VehicleEvent.SelectDriveMode(it)) },
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.30f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                    ) {
                        VehicleStatsPanel(
                            efficiencyMpkWh = uiState.efficiencyMpkWh,
                            odometerMiles = uiState.odometerMiles,
                            tripEnergyKwh = uiState.tripEnergyKwh,
                            driveMode = uiState.driveMode,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        VehicleMotionStudio(
                            selectedScheme = uiState.screenMotionScheme,
                            activeToken = uiState.activeMotionToken,
                            previewTrigger = uiState.motionPreviewTrigger,
                            onSchemeSelected = { onEvent(VehicleEvent.SelectScreenMotionScheme(it)) },
                            onTokenSelected = { onEvent(VehicleEvent.SelectMotionToken(it)) },
                            onReplay = { onEvent(VehicleEvent.ReplayMotionPreview) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        )
                    }
                }
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
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
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
