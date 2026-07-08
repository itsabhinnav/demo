package com.test.design.presentation.ivi.climate

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.test.design.core.motion.AppMotionScheme
import com.test.design.presentation.common.ScreenBackground
import com.test.design.presentation.ivi.climate.components.AnimatedTemperatureCounter
import com.test.design.presentation.ivi.climate.components.ClimateAmbientPanel
import com.test.design.presentation.ivi.climate.components.ClimateFluidDial
import com.test.design.presentation.ivi.climate.components.ClimateZoneSelector
import com.test.design.presentation.ivi.climate.components.FanSpeedBars
import com.test.design.presentation.ivi.climate.components.MorphingAirflowSegmentedButton
import com.test.design.presentation.ivi.climate.components.SeatHeatIndicator
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.theme.CarBackgroundTokens
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ClimateCardActiveRadii
import com.test.design.theme.ClimateCardRestRadii
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget
import com.test.design.theme.climateColorScheme
import com.test.design.theme.glassSurfaceColor
import com.test.design.theme.rememberClimateDialShape
import com.test.design.theme.temperatureToFraction
import com.test.design.theme.toMotionScheme

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
    val temperatureBand = activeTemperature.toClimateTemperatureBand(
        min = uiState.minTemperature,
        max = uiState.maxTemperature,
    )
    val layoutProfile = temperatureBand.layoutProfile()
    val dynamicScheme = climateColorScheme(temperatureFraction)
    val dialShape = rememberClimateDialShape(acEnabled = uiState.isAcEnabled)

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = MaterialTheme.typography,
        shapes = ExpressiveShapes,
        motionScheme = AppMotionScheme.Expressive.toMotionScheme(),
    ) {
        val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
        val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
        val animatedPrimary by animateColorAsState(
            targetValue = dynamicScheme.primary,
            animationSpec = effectsSpec,
            label = "climate_tint",
        )
        val dialWeight by animateFloatAsState(layoutProfile.dialWeight, animationSpec = spatialSpec, label = "dial_weight")
        val controlsWeight by animateFloatAsState(layoutProfile.controlsWeight, animationSpec = spatialSpec, label = "controls_weight")

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

                    ClimateTemperatureBanner(
                        band = temperatureBand,
                        layoutLabel = layoutProfile.layoutLabel,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                    ) {
                        Column(
                            modifier = Modifier.weight(dialWeight),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                TemperatureAdjustButton(
                                    icon = Icons.Default.Remove,
                                    contentDescription = "Decrease temperature",
                                    onClick = { onEvent(ClimateEvent.DecreaseTemperature) },
                                )
                                ClimateFluidDial(
                                    temperature = activeTemperature,
                                    temperatureFraction = temperatureFraction,
                                    isAcEnabled = uiState.isAcEnabled,
                                    dialShape = dialShape,
                                    layoutProfile = layoutProfile,
                                    modifier = widgetContentSharedElement(
                                        widget = DashboardWidget.Climate,
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        modifier = Modifier.size(240.dp),
                                    ),
                                ) {
                                    AnimatedTemperatureCounter(temperature = activeTemperature)
                                }
                                TemperatureAdjustButton(
                                    icon = Icons.Default.Add,
                                    contentDescription = "Increase temperature",
                                    onClick = { onEvent(ClimateEvent.IncreaseTemperature) },
                                )
                            }
                            ClimateZoneSelector(
                                driverTemp = uiState.temperatureCelsius,
                                passengerTemp = uiState.passengerTemperatureCelsius,
                                activeZone = uiState.activeZone,
                                onZoneSelected = { onEvent(ClimateEvent.SelectZone(it)) },
                            )
                            AnimatedVisibility(
                                visible = layoutProfile.showAmbientPanel,
                                enter = expandVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()) +
                                    fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
                                exit = shrinkVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()) +
                                    fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                ClimateAmbientPanel(
                                    temperature = activeTemperature,
                                    temperatureFraction = temperatureFraction,
                                    band = temperatureBand,
                                    isAcEnabled = uiState.isAcEnabled,
                                    fanSpeed = uiState.fanSpeed,
                                    maxFanSpeed = uiState.maxFanSpeed,
                                    morphExpanded = uiState.isAcEnabled,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(controlsWeight)
                                .animateContentSize(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()),
                            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                        ) {
                            MorphingDetailSurfaceCard(
                                morphExpanded = uiState.isAcEnabled,
                                compactRadii = ClimateCardRestRadii,
                                expandedRadii = ClimateCardActiveRadii,
                                modifier = widgetControlsSharedElement(
                                    widget = DashboardWidget.Climate,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    modifier = Modifier.fillMaxWidth(),
                                ),
                            ) {
                                Text("Fan speed", style = MaterialTheme.typography.titleMedium)
                                FanSpeedBars(
                                    fanSpeed = uiState.fanSpeed,
                                    maxFanSpeed = uiState.maxFanSpeed,
                                    onSpeedSelected = { onEvent(ClimateEvent.SetFanSpeed(it)) },
                                    modifier = Modifier.padding(top = 12.dp),
                                )
                            }
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

@Composable
private fun ClimateTemperatureBanner(
    band: ClimateTemperatureBand,
    layoutLabel: String,
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    AnimatedContent(
        targetState = band,
        transitionSpec = {
            fadeIn(animationSpec = effectsSpec) togetherWith fadeOut(animationSpec = effectsSpec)
        },
        label = "climate_temperature_banner",
    ) { mode ->
        Surface(
            shape = ExpressiveShapes.medium,
            color = glassSurfaceColor(),
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
private fun TemperatureAdjustButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(CarDesignTokens.MinTouchTarget)
            .clip(CircleShape)
            .carTouchTarget(),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
        )
    }
}
