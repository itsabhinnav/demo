package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
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
        else -> PlaceholderWidgetEmbedded(
            widget = widget,
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            compact = true,
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
            modifier = modifier.fillMaxSize(),
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${state.destination} · ${state.etaMinutes} min",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
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
            modifier = modifier.fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.PlaceholderWidgetEmbedded(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = widget.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            modifier = widgetContentSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Surface(
            shape = ExpressiveShapes.small,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = widgetControlsSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarDesignTokens.MinTouchTarget),
            ),
        ) {
            androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Open ${widget.title}",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
