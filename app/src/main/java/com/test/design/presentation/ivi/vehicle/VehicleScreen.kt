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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.vehicle.components.AnimatedBatteryGauge
import com.test.design.presentation.ivi.vehicle.components.DriveModeSelector
import com.test.design.presentation.ivi.vehicle.components.TirePressureGrid
import com.test.design.presentation.ivi.vehicle.components.VehicleMotionLabPanel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.VehicleCardActiveRadii
import com.test.design.theme.VehicleCardRestRadii
import com.test.design.theme.batteryToFraction
import com.test.design.theme.rememberVehicleGaugeShape
import com.test.design.theme.toMotionScheme
import com.test.design.theme.vehicleColorScheme

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
    )
    val screenMotionScheme = uiState.screenMotionScheme.toMotionScheme()
    val gaugeShape = rememberVehicleGaugeShape(sportMode = uiState.driveMode == DriveMode.Sport)

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = MaterialTheme.typography,
        shapes = ExpressiveShapes,
        motionScheme = screenMotionScheme,
    ) {
        val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<androidx.compose.ui.graphics.Color>()
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
                .background(animatedBackground)
                .padding(CarDesignTokens.ContentPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                WidgetScreenHeader(
                    widget = DashboardWidget.Vehicle,
                    onBack = onBack,
                    animatedVisibilityScope = animatedVisibilityScope,
                    trailingContent = {
                        FilterChip(
                            selected = uiState.motionLabExpanded,
                            onClick = { onEvent(VehicleEvent.ToggleMotionLab) },
                            label = {
                                Text(
                                    text = if (uiState.motionLabExpanded) {
                                        uiState.screenMotionScheme.label
                                    } else {
                                        "Motion"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            },
                        )
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedBatteryGauge(
                        percent = uiState.batteryPercent,
                        rangeMiles = uiState.rangeMiles,
                        gaugeShape = gaugeShape,
                        onClick = { onEvent(VehicleEvent.CycleBatteryDemo) },
                        modifier = Modifier.weight(0.38f),
                    )

                    Column(
                        modifier = Modifier.weight(0.62f),
                        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                    ) {
                        MorphingDetailSurfaceCard(
                            morphExpanded = uiState.driveMode == DriveMode.Sport,
                            compactRadii = VehicleCardRestRadii,
                            expandedRadii = VehicleCardActiveRadii,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Drive mode", style = MaterialTheme.typography.titleMedium)
                            DriveModeSelector(
                                selected = uiState.driveMode,
                                onSelected = { onEvent(VehicleEvent.SelectDriveMode(it)) },
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }

                        MorphingDetailSurfaceCard(
                            morphExpanded = uiState.batteryPercent < 35,
                            compactRadii = VehicleCardRestRadii,
                            expandedRadii = VehicleCardActiveRadii,
                            emphasized = uiState.batteryPercent < 35,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Trip efficiency", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${uiState.efficiencyMpkWh} mi/kWh",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = "Odometer ${uiState.odometerMiles} mi · ${uiState.tripEnergyKwh} kWh used",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        VehicleMotionLabPanel(
                            selectedScheme = uiState.screenMotionScheme,
                            expanded = uiState.motionLabExpanded,
                            previewTrigger = uiState.motionPreviewTrigger,
                            onSchemeSelected = { onEvent(VehicleEvent.SelectScreenMotionScheme(it)) },
                            onReplayPreview = { onEvent(VehicleEvent.ReplayMotionPreview) },
                        )
                    }
                }

                MorphingDetailSurfaceCard(
                    morphExpanded = uiState.tirePressures.any { !it.isOptimal },
                    compactRadii = VehicleCardRestRadii,
                    expandedRadii = VehicleCardActiveRadii,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Tire pressure", style = MaterialTheme.typography.titleMedium)
                    TirePressureGrid(
                        tires = uiState.tirePressures,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }
    }
}
