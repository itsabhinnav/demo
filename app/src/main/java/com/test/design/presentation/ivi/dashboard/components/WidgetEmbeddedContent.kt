package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.ClimateEvent
import com.test.design.presentation.ivi.climate.ClimateUiState
import com.test.design.presentation.ivi.climate.components.ClimateFanSpeedCard
import com.test.design.presentation.ivi.climate.components.ClimateTemperatureSection
import com.test.design.theme.climateColorScheme
import com.test.design.theme.temperatureToFraction
import com.test.design.theme.vehicleColorScheme
import com.test.design.theme.batteryToFraction
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.media.MediaEvent
import com.test.design.presentation.ivi.media.MediaUiState
import com.test.design.presentation.ivi.media.components.AnimatedTrackInfo
import com.test.design.presentation.ivi.media.components.MediaAlbumArt
import com.test.design.presentation.ivi.media.components.MediaTransportControlsBar
import com.test.design.presentation.ivi.navigation.NavigationEvent
import com.test.design.presentation.ivi.navigation.NavigationUiState
import com.test.design.presentation.ivi.navigation.components.TurnInstructionCard
import com.test.design.presentation.ivi.vehicle.VehicleEvent
import com.test.design.presentation.ivi.vehicle.VehicleUiState
import com.test.design.presentation.ivi.vehicle.components.VehicleEnergyCockpit
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.rememberClimateDialShape
import com.test.design.theme.rememberMediaAlbumShape
import com.test.design.theme.rememberVehicleGaugeShape

private const val VehicleMaxRangeMiles = 300

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.WidgetEmbeddedContent(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    contentColor: Color,
    mediaState: MediaUiState? = null,
    onMediaEvent: ((MediaEvent) -> Unit)? = null,
    climateState: ClimateUiState? = null,
    climateTemperature: Int? = null,
    onClimateEvent: ((ClimateEvent) -> Unit)? = null,
    navigationState: NavigationUiState? = null,
    onNavigationEvent: ((NavigationEvent) -> Unit)? = null,
    vehicleState: VehicleUiState? = null,
    onVehicleEvent: ((VehicleEvent) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    when (widget) {
        DashboardWidget.Media -> {
            val state = mediaState ?: return
            val onEvent = onMediaEvent ?: return
            val albumShape = rememberMediaAlbumShape(playing = state.isPlaying)
            MediaWidgetEmbedded(
                state = state,
                albumShape = albumShape,
                onEvent = onEvent,
                contentColor = contentColor,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        DashboardWidget.Climate -> {
            val state = climateState ?: return
            val temperature = climateTemperature ?: state.temperatureCelsius
            val onEvent = onClimateEvent ?: return
            val dialShape = rememberClimateDialShape(acEnabled = state.isAcEnabled)
            ClimateWidgetEmbedded(
                state = state,
                temperature = temperature,
                dialShape = dialShape,
                onEvent = onEvent,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        DashboardWidget.Navigation -> {
            val state = navigationState ?: return
            val onEvent = onNavigationEvent ?: return
            NavigationWidgetEmbedded(
                state = state,
                onEvent = onEvent,
                contentColor = contentColor,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        DashboardWidget.Vehicle -> {
            val state = vehicleState ?: return
            val onEvent = onVehicleEvent ?: return
            val gaugeShape = rememberVehicleGaugeShape(
                sportMode = state.driveMode == com.test.design.presentation.ivi.vehicle.DriveMode.Sport ||
                    state.isCharging,
            )
            VehicleWidgetEmbedded(
                state = state,
                gaugeShape = gaugeShape,
                onEvent = onEvent,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        DashboardWidget.Settings -> SettingsWidgetEmbedded(
            contentColor = contentColor,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        )
        DashboardWidget.MaterialComponents -> MaterialWidgetEmbedded(
            contentColor = contentColor,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        )
        DashboardWidget.CustomizedMaterial -> CustomizedWidgetEmbedded(
            contentColor = contentColor,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MediaWidgetEmbedded(
    state: MediaUiState,
    albumShape: Shape,
    onEvent: (MediaEvent) -> Unit,
    contentColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MediaAlbumArt(
                album = state.currentTrack.album,
                albumShape = albumShape,
                compact = true,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "album_art"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            )
            AnimatedTrackInfo(
                track = state.currentTrack,
                compact = true,
                titleColor = contentColor,
                subtitleColor = contentColor.copy(alpha = 0.75f),
                modifier = widgetContentSharedElement(
                    widget = DashboardWidget.Media,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.weight(1f),
                ),
            )
        }
        MediaTransportControlsBar(
            isPlaying = state.isPlaying,
            onToggleQueue = { onEvent(MediaEvent.ToggleQueue) },
            onPrevious = { onEvent(MediaEvent.PreviousTrack) },
            onTogglePlayback = { onEvent(MediaEvent.TogglePlayback) },
            onNext = { onEvent(MediaEvent.NextTrack) },
            showQueue = false,
            modifier = widgetControlsSharedElement(
                widget = DashboardWidget.Media,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ClimateWidgetEmbedded(
    state: ClimateUiState,
    temperature: Int,
    dialShape: Shape,
    onEvent: (ClimateEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val temperatureFraction = temperatureToFraction(
        celsius = temperature,
        min = state.minTemperature,
        max = state.maxTemperature,
    )
    MaterialTheme(colorScheme = climateColorScheme(temperatureFraction)) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ClimateTemperatureSection(
                temperature = temperature,
                isAcEnabled = state.isAcEnabled,
                dialShape = dialShape,
                onDecrease = { onEvent(ClimateEvent.DecreaseTemperature) },
                onIncrease = { onEvent(ClimateEvent.IncreaseTemperature) },
                compact = true,
                temperatureUnit = state.temperatureUnit,
                modifier = widgetContentSharedElement(
                    widget = DashboardWidget.Climate,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.fillMaxWidth(),
                ),
            )
            ClimateFanSpeedCard(
                fanSpeed = state.fanSpeed,
                maxFanSpeed = state.maxFanSpeed,
                isAcEnabled = state.isAcEnabled,
                onSpeedSelected = { onEvent(ClimateEvent.SetFanSpeed(it)) },
                compact = true,
                modifier = widgetControlsSharedElement(
                    widget = DashboardWidget.Climate,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.fillMaxWidth(),
                ),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.NavigationWidgetEmbedded(
    state: NavigationUiState,
    onEvent: (NavigationEvent) -> Unit,
    contentColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${state.destination} · ${state.etaMinutes} min",
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            maxLines = 1,
            modifier = widgetContentSharedElement(
                widget = DashboardWidget.Navigation,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        TurnInstructionCard(
            instruction = state.currentInstruction,
            maneuverIcon = state.maneuverIcon,
            distanceRemaining = state.distanceRemaining,
            etaMinutes = state.etaMinutes,
            onClick = { onEvent(NavigationEvent.NextManeuver) },
            modifier = widgetControlsSharedElement(
                widget = DashboardWidget.Navigation,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.VehicleWidgetEmbedded(
    state: VehicleUiState,
    gaugeShape: Shape,
    onEvent: (VehicleEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val dynamicScheme = vehicleColorScheme(
        driveMode = state.driveMode,
        batteryFraction = batteryToFraction(state.batteryPercent),
        isCharging = state.isCharging,
    )
    MaterialTheme(colorScheme = dynamicScheme) {
        VehicleEnergyCockpit(
            percent = state.batteryPercent,
            rangeMiles = state.rangeMiles,
            maxRangeMiles = VehicleMaxRangeMiles,
            isCharging = state.isCharging,
            chargeRateKw = state.chargeRateKw,
            gaugeShape = gaugeShape,
            onGaugeClick = { onEvent(VehicleEvent.CycleBatteryDemo) },
            compact = true,
            contentModifier = widgetContentSharedElement(
                widget = DashboardWidget.Vehicle,
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            controlsModifier = widgetControlsSharedElement(
                widget = DashboardWidget.Vehicle,
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            modifier = modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SettingsWidgetEmbedded(
    contentColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val drivingState = com.test.design.core.LocalDrivingUxState.current
    val onDrivingStateChange = com.test.design.core.driving.LocalDrivingUxUpdater.current
    val motionScheme = com.test.design.core.motion.LocalEffectiveMotionScheme.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Driving · ${drivingState.name}",
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            modifier = widgetContentSharedElement(
                widget = DashboardWidget.Settings,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            com.test.design.core.DrivingUxState.entries.take(3).forEach { state ->
                androidx.compose.material3.FilterChip(
                    selected = drivingState == state,
                    onClick = { onDrivingStateChange(state) },
                    label = {
                        Text(state.name, style = MaterialTheme.typography.labelLarge)
                    },
                )
            }
        }
        Surface(
            shape = ExpressiveShapes.small,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
            modifier = widgetControlsSharedElement(
                widget = DashboardWidget.Settings,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        ) {
            Text(
                text = "Motion · ${motionScheme.label}",
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MaterialWidgetEmbedded(
    contentColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Buttons · Chips · Sliders · Cards",
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            modifier = widgetContentSharedElement(
                widget = DashboardWidget.MaterialComponents,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            androidx.compose.material3.AssistChip(
                onClick = {},
                label = { Text("Chip") },
            )
            androidx.compose.material3.FilterChip(
                selected = true,
                onClick = {},
                label = { Text("Filter") },
            )
            androidx.compose.material3.SuggestionChip(
                onClick = {},
                label = { Text("Suggest") },
            )
        }
        Surface(
            shape = ExpressiveShapes.small,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
            modifier = widgetControlsSharedElement(
                widget = DashboardWidget.MaterialComponents,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarDesignTokens.MinTouchTarget),
            ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Open gallery",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CustomizedWidgetEmbedded(
    contentColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Horizon brand tokens",
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            modifier = widgetContentSharedElement(
                widget = DashboardWidget.CustomizedMaterial,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.primaryContainer,
            ).forEach { swatch ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = ExpressiveShapes.extraSmall,
                    color = swatch,
                ) {}
            }
        }
        Surface(
            shape = ExpressiveShapes.small,
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f),
            modifier = widgetControlsSharedElement(
                widget = DashboardWidget.CustomizedMaterial,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarDesignTokens.MinTouchTarget),
            ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Open brand system",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
}
