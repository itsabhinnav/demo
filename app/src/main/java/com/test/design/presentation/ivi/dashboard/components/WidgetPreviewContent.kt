package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.ClimateUiState
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.media.MediaUiState
import com.test.design.presentation.ivi.navigation.NavigationUiState
import com.test.design.presentation.ivi.vehicle.VehicleUiState
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.WidgetCardShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.WidgetPreviewContent(
    widget: DashboardWidget,
    contentColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    mediaState: MediaUiState? = null,
    climateState: ClimateUiState? = null,
    climateTemperature: Int? = null,
    navigationState: NavigationUiState? = null,
    vehicleState: VehicleUiState? = null,
    modifier: Modifier = Modifier,
) {
    when (widget) {
        DashboardWidget.Media -> mediaState?.let {
            MediaWidgetPreview(
                state = it,
                contentColor = contentColor,
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        DashboardWidget.Climate -> climateState?.let {
            ClimateWidgetPreview(
                state = it,
                temperature = climateTemperature ?: it.temperatureCelsius,
                contentColor = contentColor,
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        DashboardWidget.Navigation -> navigationState?.let {
            NavigationWidgetPreview(
                state = it,
                contentColor = contentColor,
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        DashboardWidget.Vehicle -> vehicleState?.let {
            VehicleWidgetPreview(
                state = it,
                contentColor = contentColor,
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = modifier,
            )
        }
        else -> PlaceholderWidgetPreview(
            widget = widget,
            contentColor = contentColor,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MediaWidgetPreview(
    state: MediaUiState,
    contentColor: Color,
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .sharedElement(
                        rememberSharedContentState(key = "album_art"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .clip(WidgetCardShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.currentTrack.album.take(2).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Column(
                modifier = widgetContentSharedElement(
                    widget = widget,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.weight(1f),
                ),
            ) {
                Text(
                    text = state.currentTrack.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = state.currentTrack.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        MediaTransportControls(
            isPlaying = state.isPlaying,
            contentColor = contentColor,
            modifier = widgetControlsSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
    }
}

@Composable
private fun MediaTransportControls(
    isPlaying: Boolean,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = contentColor.copy(alpha = 0.8f),
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor,
            )
        }
        Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = contentColor.copy(alpha = 0.8f),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ClimateWidgetPreview(
    state: ClimateUiState,
    temperature: Int,
    contentColor: Color,
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = widgetContentSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$temperature°",
                style = MaterialTheme.typography.displaySmall,
                color = contentColor,
            )
        }
        Text(
            text = if (state.isAcEnabled) "${state.airflowMode.label} · A/C on" else "A/C off",
            style = MaterialTheme.typography.bodySmall,
            color = contentColor.copy(alpha = 0.75f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = widgetControlsSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.NavigationWidgetPreview(
    state: NavigationUiState,
    contentColor: Color,
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${state.destination} · ${state.etaMinutes} min",
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = widgetContentSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Surface(
            shape = ExpressiveShapes.small,
            color = contentColor.copy(alpha = 0.1f),
            modifier = widgetControlsSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.maneuverIcon,
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor,
                )
                Text(
                    text = state.currentInstruction,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.VehicleWidgetPreview(
    state: VehicleUiState,
    contentColor: Color,
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = widgetContentSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "${state.batteryPercent}%",
                style = MaterialTheme.typography.headlineMedium,
                color = contentColor,
            )
            Text(
                text = "${state.rangeMiles} mi",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor.copy(alpha = 0.75f),
            )
        }
        Text(
            text = when {
                state.isCharging -> "Charging · ${state.chargeRateKw.toInt()} kW"
                else -> "${state.driveMode.label} · ${state.regenLevel.label} regen"
            },
            style = MaterialTheme.typography.bodySmall,
            color = contentColor.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = widgetControlsSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.PlaceholderWidgetPreview(
    widget: DashboardWidget,
    contentColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = widget.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor.copy(alpha = 0.75f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = widgetContentSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Surface(
            shape = ExpressiveShapes.small,
            color = contentColor.copy(alpha = 0.1f),
            modifier = widgetControlsSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarDesignTokens.MinTouchTarget),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Open ${widget.title}",
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                )
            }
        }
    }
}
