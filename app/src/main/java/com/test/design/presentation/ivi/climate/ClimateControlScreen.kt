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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.test.design.presentation.common.ScreenBackground
import com.test.design.presentation.ivi.climate.components.ClimateComfortControlsCard
import com.test.design.presentation.ivi.climate.components.ClimateFanSpeedCard
import com.test.design.presentation.ivi.climate.components.ClimateHvacIcons
import com.test.design.presentation.ivi.climate.components.ClimateTemperatureSection
import com.test.design.presentation.ivi.climate.components.CoolSnowflakeOverlay
import com.test.design.presentation.ivi.climate.components.MorphingAirflowSegmentedButton
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.common.SimulatedBadge
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.theme.AdaptiveLayout
import com.test.design.theme.CarBackgroundTokens
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ClimateCardActiveRadii
import com.test.design.theme.ClimateCardRestRadii
import com.test.design.theme.WindowLayoutInfo
import com.test.design.theme.climateAmbientColor
import com.test.design.theme.climateColorScheme
import com.test.design.theme.carTouchTarget
import com.test.design.theme.rememberClimateDialShape
import com.test.design.theme.temperatureToFraction
import com.test.design.theme.zoneCoolIntensity

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClimateControlScreen(
    uiState: ClimateUiState,
    activeTemperature: Float,
    onEvent: (ClimateEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val driverFraction = temperatureToFraction(
        uiState.temperatureCelsius,
        uiState.minTemperature,
        uiState.maxTemperature,
    )
    val passengerFraction = temperatureToFraction(
        uiState.passengerTemperatureCelsius,
        uiState.minTemperature,
        uiState.maxTemperature,
    )
    val blendFraction = (driverFraction + passengerFraction) / 2f
    val driverCoolIntensity = zoneCoolIntensity(driverFraction, passengerFraction)
    val passengerCoolIntensity = zoneCoolIntensity(passengerFraction, driverFraction)
    val dynamicScheme = climateColorScheme(blendFraction, MaterialTheme.colorScheme)
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val driverGlow by animateColorAsState(
        climateAmbientColor(driverFraction),
        animationSpec = motionSpec,
        label = "driver_glow",
    )
    val passengerGlow by animateColorAsState(
        climateAmbientColor(passengerFraction),
        animationSpec = motionSpec,
        label = "passenger_glow",
    )
    val dialShape = rememberClimateDialShape(acEnabled = uiState.isAcEnabled)

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        motionScheme = MaterialTheme.motionScheme,
    ) {
        CompositionLocalProvider(LocalContentColor provides dynamicScheme.onBackground) {
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
                            Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0f to driverGlow.copy(alpha = CarBackgroundTokens.DetailTintAlpha),
                                    0.5f to MaterialTheme.colorScheme.surfaceContainerLow.copy(
                                        alpha = CarBackgroundTokens.DetailOverlayAlpha,
                                    ),
                                    1f to passengerGlow.copy(alpha = CarBackgroundTokens.DetailTintAlpha),
                                ),
                            ),
                        ),
                )
                // Per-zone snow — only the cool side gets flakes.
                Row(modifier = Modifier.fillMaxSize()) {
                    CoolSnowflakeOverlay(
                        coolIntensity = driverCoolIntensity,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        flakeCount = 18,
                        sizeScale = 1.3f,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    CoolSnowflakeOverlay(
                        coolIntensity = passengerCoolIntensity,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        flakeCount = 18,
                        sizeScale = 1.3f,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(CarDesignTokens.ContentPadding),
                ) {
                    AdaptiveLayout(modifier = Modifier.fillMaxSize()) { layout ->
                        val scrollPortrait = !layout.useSideBySide
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (scrollPortrait) {
                                        Modifier.verticalScroll(rememberScrollState())
                                    } else {
                                        Modifier
                                    },
                                ),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            WidgetScreenHeader(
                                widget = DashboardWidget.Climate,
                                onBack = onBack,
                                animatedVisibilityScope = animatedVisibilityScope,
                                trailingContent = {
                                    val caps = uiState.capabilities
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        SimulatedBadge(
                                            label = if (uiState.isLive) "Live" else "Simulated",
                                        )
                                        FilterChip(
                                            selected = true,
                                            onClick = { onEvent(ClimateEvent.ToggleTemperatureUnit) },
                                            label = {
                                                Text(
                                                    text = uiState.temperatureUnit.shortLabel,
                                                    style = MaterialTheme.typography.labelLarge,
                                                )
                                            },
                                            modifier = Modifier.carTouchTarget(),
                                        )
                                        if (caps.hasAc) {
                                            Icon(
                                                painter = painterResource(ClimateHvacIcons.Ac),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(CarDesignTokens.TertiaryIcon),
                                            )
                                            Text(
                                                text = "A/C",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Switch(
                                                checked = uiState.isAcEnabled,
                                                onCheckedChange = { onEvent(ClimateEvent.ToggleAc) },
                                            )
                                        }
                                    }
                                },
                            )

                            if (uiState.capabilities.hasDriverTemp ||
                                uiState.capabilities.hasPassengerTemp
                            ) {
                                DualZoneTemperatureBand(
                                    layout = layout,
                                    uiState = uiState,
                                    dialShape = dialShape,
                                    onEvent = onEvent,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    modifier = Modifier
                                        .then(
                                            if (scrollPortrait) {
                                                Modifier.fillMaxWidth()
                                            } else {
                                                Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                            },
                                        ),
                                )
                            }

                            if (uiState.capabilities.hasFanSpeed ||
                                uiState.capabilities.hasAirflowControls
                            ) {
                                ClimateAirBand(
                                    layout = layout,
                                    uiState = uiState,
                                    onEvent = onEvent,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                            }

                            if (uiState.capabilities.hasComfortControls) {
                                val caps = uiState.capabilities
                                ClimateComfortControlsCard(
                                    seatHeatLevel = uiState.seatHeatLevel,
                                    maxSeatHeatLevel = uiState.maxSeatHeatLevel,
                                    steeringHeatLevel = uiState.steeringHeatLevel,
                                    maxSteeringHeatLevel = uiState.maxSteeringHeatLevel,
                                    seatVentLevel = uiState.seatVentLevel,
                                    maxSeatVentLevel = uiState.maxSeatVentLevel,
                                    isFrontDefrostOn = uiState.isFrontDefrostOn,
                                    isRearDefrostOn = uiState.isRearDefrostOn,
                                    isRecirculationOn = uiState.isRecirculationOn,
                                    isSyncEnabled = uiState.isSyncEnabled,
                                    isAcEnabled = uiState.isAcEnabled,
                                    onCycleSeatHeat = { onEvent(ClimateEvent.CycleSeatHeat) },
                                    onCycleSteeringHeat = { onEvent(ClimateEvent.CycleSteeringHeat) },
                                    onCycleSeatVent = { onEvent(ClimateEvent.CycleSeatVent) },
                                    onToggleFrontDefrost = { onEvent(ClimateEvent.ToggleFrontDefrost) },
                                    onToggleRearDefrost = { onEvent(ClimateEvent.ToggleRearDefrost) },
                                    onToggleRecirculation = { onEvent(ClimateEvent.ToggleRecirculation) },
                                    onToggleSync = { onEvent(ClimateEvent.ToggleSync) },
                                    showSeatHeat = caps.hasSeatHeat,
                                    showSteeringHeat = caps.hasSteeringHeat,
                                    showSeatVent = caps.hasSeatVent,
                                    showFrontDefrost = caps.hasFrontDefrost,
                                    showRearDefrost = caps.hasRearDefrost,
                                    showRecirculation = caps.hasRecirculation,
                                    showSync = caps.hasSync,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.DualZoneTemperatureBand(
    layout: WindowLayoutInfo,
    uiState: ClimateUiState,
    dialShape: androidx.compose.ui.graphics.Shape,
    onEvent: (ClimateEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val showDriver = uiState.capabilities.hasDriverTemp
    val showPassenger = uiState.capabilities.hasPassengerTemp
    val driverDial = @Composable { dialModifier: Modifier ->
        ClimateTemperatureSection(
            temperature = uiState.temperatureCelsius,
            isAcEnabled = uiState.isAcEnabled,
            dialShape = dialShape,
            onDecrease = {
                onEvent(ClimateEvent.AdjustZoneTemperature(ClimateZone.Driver, -1))
            },
            onIncrease = {
                onEvent(ClimateEvent.AdjustZoneTemperature(ClimateZone.Driver, +1))
            },
            onTemperatureSteps = { steps ->
                onEvent(ClimateEvent.AdjustZoneTemperature(ClimateZone.Driver, steps))
            },
            minTemperature = uiState.minTemperature,
            maxTemperature = uiState.maxTemperature,
            temperatureStepCelsius = uiState.temperatureStepCelsius,
            temperatureStepFahrenheit = uiState.temperatureStepFahrenheit,
            minTemperatureFahrenheit = uiState.minTemperatureFahrenheit,
            zoneLabel = ClimateZone.Driver.label,
            useZoneColorScheme = true,
            temperatureUnit = uiState.temperatureUnit,
            otherZoneTemperature = uiState.passengerTemperatureCelsius,
            modifier = dialModifier,
        )
    }
    val passengerDial = @Composable { dialModifier: Modifier ->
        ClimateTemperatureSection(
            temperature = uiState.passengerTemperatureCelsius,
            isAcEnabled = uiState.isAcEnabled,
            dialShape = dialShape,
            onDecrease = {
                onEvent(ClimateEvent.AdjustZoneTemperature(ClimateZone.Passenger, -1))
            },
            onIncrease = {
                onEvent(ClimateEvent.AdjustZoneTemperature(ClimateZone.Passenger, +1))
            },
            onTemperatureSteps = { steps ->
                onEvent(ClimateEvent.AdjustZoneTemperature(ClimateZone.Passenger, steps))
            },
            minTemperature = uiState.minTemperature,
            maxTemperature = uiState.maxTemperature,
            temperatureStepCelsius = uiState.temperatureStepCelsius,
            temperatureStepFahrenheit = uiState.temperatureStepFahrenheit,
            minTemperatureFahrenheit = uiState.minTemperatureFahrenheit,
            zoneLabel = ClimateZone.Passenger.label,
            useZoneColorScheme = true,
            temperatureUnit = uiState.temperatureUnit,
            otherZoneTemperature = uiState.temperatureCelsius,
            modifier = dialModifier,
        )
    }

    if (layout.useSideBySide && showDriver && showPassenger) {
        Row(
            modifier = modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            driverDial(
                widgetContentSharedElement(
                    widget = DashboardWidget.Climate,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ),
            )
            passengerDial(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            if (showDriver) {
                driverDial(
                    widgetContentSharedElement(
                        widget = DashboardWidget.Climate,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.fillMaxWidth(),
                    ),
                )
            }
            if (showPassenger) {
                passengerDial(Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ClimateAirBand(
    layout: WindowLayoutInfo,
    uiState: ClimateUiState,
    onEvent: (ClimateEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val showFan = uiState.capabilities.hasFanSpeed
    val showAirflow = uiState.capabilities.hasAirflowControls
    val airflowModes = uiState.capabilities.availableAirflowModes
    val fanCard = @Composable { fanModifier: Modifier ->
        ClimateFanSpeedCard(
            fanSpeed = uiState.fanSpeed,
            maxFanSpeed = uiState.maxFanSpeed,
            isAcEnabled = uiState.isAcEnabled,
            onSpeedSelected = { onEvent(ClimateEvent.SetFanSpeed(it)) },
            modifier = widgetControlsSharedElement(
                widget = DashboardWidget.Climate,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = fanModifier,
            ),
        )
    }
    val airflowCard = @Composable { airflowModifier: Modifier ->
        MorphingDetailSurfaceCard(
            morphExpanded = uiState.isAcEnabled,
            compactRadii = ClimateCardRestRadii,
            expandedRadii = ClimateCardActiveRadii,
            modifier = airflowModifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    painter = painterResource(ClimateHvacIcons.Hvac),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(CarDesignTokens.TertiaryIcon),
                )
                Text("Airflow", style = MaterialTheme.typography.titleMedium)
            }
            MorphingAirflowSegmentedButton(
                selectedMode = uiState.airflowMode,
                onModeSelected = { onEvent(ClimateEvent.SelectAirflow(it)) },
                modes = airflowModes,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }

    if (layout.useSideBySide && showFan && showAirflow) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            verticalAlignment = Alignment.Top,
        ) {
            fanCard(Modifier.weight(0.4f))
            airflowCard(Modifier.weight(0.6f))
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            if (showFan) fanCard(Modifier.fillMaxWidth())
            if (showAirflow) airflowCard(Modifier.fillMaxWidth())
        }
    }
}
