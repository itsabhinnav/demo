package com.test.design.presentation.ivi.climate

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.test.design.presentation.common.ScreenBackground
import com.test.design.theme.CarBackgroundTokens
import com.test.design.presentation.ivi.climate.components.ClimateFanSpeedCard
import com.test.design.presentation.ivi.climate.components.ClimateTemperatureSection
import com.test.design.presentation.ivi.climate.components.ClimateZoneSelector
import com.test.design.presentation.ivi.climate.components.TemperatureAdjustButton
import com.test.design.presentation.ivi.climate.components.MorphingAirflowSegmentedButton
import com.test.design.presentation.ivi.climate.components.SeatHeatIndicator
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ClimateCardActiveRadii
import com.test.design.theme.ClimateCardRestRadii
import com.test.design.theme.climateColorScheme
import com.test.design.theme.rememberClimateDialShape
import com.test.design.theme.temperatureToFraction

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClimateControlScreen(
    uiState: ClimateUiState,
    activeTemperature: Int,
    onEvent: (ClimateEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val temperatureFraction = temperatureToFraction(
        celsius = activeTemperature,
        min = uiState.minTemperature,
        max = uiState.maxTemperature,
    )
    val dynamicScheme = climateColorScheme(temperatureFraction)
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val animatedPrimary by animateColorAsState(
        targetValue = dynamicScheme.primary,
        animationSpec = motionSpec,
        label = "climate_tint",
    )
    val dialShape = rememberClimateDialShape(acEnabled = uiState.isAcEnabled)

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        motionScheme = MaterialTheme.motionScheme,
    ) {
        Box(
            modifier = widgetContainerTransform(
                widget = DashboardWidget.Climate,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                WidgetScreenHeader(
                    widget = DashboardWidget.Climate,
                    onBack = onBack,
                    animatedVisibilityScope = animatedVisibilityScope,
                    trailingContent = {
                        Switch(
                            checked = uiState.isAcEnabled,
                            onCheckedChange = { onEvent(ClimateEvent.ToggleAc) },
                        )
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                ) {
                    Column(
                        modifier = Modifier.weight(0.45f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                    ) {
                        ClimateTemperatureSection(
                            temperature = activeTemperature,
                            isAcEnabled = uiState.isAcEnabled,
                            dialShape = dialShape,
                            onDecrease = { onEvent(ClimateEvent.DecreaseTemperature) },
                            onIncrease = { onEvent(ClimateEvent.IncreaseTemperature) },
                            modifier = widgetContentSharedElement(
                                widget = DashboardWidget.Climate,
                                animatedVisibilityScope = animatedVisibilityScope,
                                modifier = Modifier.fillMaxWidth(),
                            ),
                        )
                        ClimateZoneSelector(
                            driverTemp = uiState.temperatureCelsius,
                            passengerTemp = uiState.passengerTemperatureCelsius,
                            activeZone = uiState.activeZone,
                            onZoneSelected = { onEvent(ClimateEvent.SelectZone(it)) },
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.55f),
                        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                    ) {
                        ClimateFanSpeedCard(
                            fanSpeed = uiState.fanSpeed,
                            maxFanSpeed = uiState.maxFanSpeed,
                            isAcEnabled = uiState.isAcEnabled,
                            onSpeedSelected = { onEvent(ClimateEvent.SetFanSpeed(it)) },
                            modifier = widgetControlsSharedElement(
                                widget = DashboardWidget.Climate,
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                        )
                        MorphingDetailSurfaceCard(
                            morphExpanded = uiState.isAcEnabled,
                            compactRadii = ClimateCardRestRadii,
                            expandedRadii = ClimateCardActiveRadii,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Airflow", style = MaterialTheme.typography.titleMedium)
                            MorphingAirflowSegmentedButton(
                                selectedMode = uiState.airflowMode,
                                onModeSelected = { onEvent(ClimateEvent.SelectAirflow(it)) },
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }
                        MorphingDetailSurfaceCard(
                            morphExpanded = uiState.isAcEnabled,
                            compactRadii = ClimateCardRestRadii,
                            expandedRadii = ClimateCardActiveRadii,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text("Seat heat", style = MaterialTheme.typography.titleMedium)
                                    SeatHeatIndicator(
                                        level = uiState.seatHeatLevel,
                                        maxLevel = uiState.maxSeatHeatLevel,
                                        modifier = Modifier.padding(top = 8.dp),
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TemperatureAdjustButton(
                                        icon = Icons.Default.Remove,
                                        contentDescription = "Decrease seat heat",
                                        onClick = { onEvent(ClimateEvent.DecreaseSeatHeat) },
                                    )
                                    TemperatureAdjustButton(
                                        icon = Icons.Default.Add,
                                        contentDescription = "Increase seat heat",
                                        onClick = { onEvent(ClimateEvent.IncreaseSeatHeat) },
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
                            FilterChip(
                                selected = uiState.isSyncEnabled,
                                onClick = { onEvent(ClimateEvent.ToggleSync) },
                                label = { Text("Sync", style = MaterialTheme.typography.labelLarge) },
                            )
                            FilterChip(
                                selected = uiState.isRecirculationOn,
                                onClick = { onEvent(ClimateEvent.ToggleRecirculation) },
                                label = { Text("Recirc", style = MaterialTheme.typography.labelLarge) },
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

