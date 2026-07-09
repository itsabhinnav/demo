package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.ClimateEvent
import com.test.design.presentation.ivi.climate.ClimateUiState
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.dashboard.widgetIconSharedElement
import com.test.design.presentation.ivi.dashboard.widgetTitleSharedElement
import com.test.design.presentation.ivi.media.MediaEvent
import com.test.design.presentation.ivi.media.MediaUiState
import com.test.design.presentation.ivi.navigation.NavigationEvent
import com.test.design.presentation.ivi.navigation.NavigationUiState
import com.test.design.presentation.ivi.vehicle.VehicleEvent
import com.test.design.presentation.ivi.vehicle.VehicleUiState
import com.test.design.theme.CarBackgroundTokens
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.WidgetCardShape
import com.test.design.theme.carTouchTarget
import com.test.design.theme.rememberClimateCardShape
import com.test.design.theme.rememberMediaCardShape
import com.test.design.theme.rememberVehicleCardShape
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DashboardWidgetCard(
    widget: DashboardWidget,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    morphExpanded: Boolean = false,
    mediaState: MediaUiState? = null,
    onMediaEvent: ((MediaEvent) -> Unit)? = null,
    climateState: ClimateUiState? = null,
    climateTemperature: Int? = null,
    onClimateEvent: ((ClimateEvent) -> Unit)? = null,
    navigationState: NavigationUiState? = null,
    onNavigationEvent: ((NavigationEvent) -> Unit)? = null,
    vehicleState: VehicleUiState? = null,
    onVehicleEvent: ((VehicleEvent) -> Unit)? = null,
) {
    val containerColor = when (widget) {
        DashboardWidget.Media -> MaterialTheme.colorScheme.secondaryContainer
        DashboardWidget.Climate -> MaterialTheme.colorScheme.tertiaryContainer
        DashboardWidget.Navigation -> MaterialTheme.colorScheme.primaryContainer
        DashboardWidget.Vehicle -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> MaterialTheme.colorScheme.surfaceContainer
    }.copy(alpha = CarBackgroundTokens.GlassSurfaceAlpha)
    val contentColor = when (widget) {
        DashboardWidget.Media -> MaterialTheme.colorScheme.onSecondaryContainer
        DashboardWidget.Climate -> MaterialTheme.colorScheme.onTertiaryContainer
        DashboardWidget.Navigation -> MaterialTheme.colorScheme.onPrimaryContainer
        DashboardWidget.Vehicle -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface
    }

    val cardShape: Shape = when (widget) {
        DashboardWidget.Climate -> rememberClimateCardShape(active = morphExpanded)
        DashboardWidget.Media -> rememberMediaCardShape(playing = morphExpanded)
        DashboardWidget.Vehicle -> rememberVehicleCardShape(active = morphExpanded)
        else -> WidgetCardShape
    }

    Box(
        modifier = widgetContainerTransform(
            widget = widget,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
                .clip(cardShape)
                .background(containerColor)
                .clickable(enabled = !isExpanded, onClick = onClick)
                .padding(CarDesignTokens.SectionPadding),
        ),
    ) {
        if (!isExpanded) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                WidgetEmbeddedContent(
                    widget = widget,
                    animatedVisibilityScope = animatedVisibilityScope,
                    mediaState = mediaState,
                    onMediaEvent = onMediaEvent,
                    climateState = climateState,
                    climateTemperature = climateTemperature,
                    onClimateEvent = onClimateEvent,
                    navigationState = navigationState,
                    onNavigationEvent = onNavigationEvent,
                    vehicleState = vehicleState,
                    onVehicleEvent = onVehicleEvent,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = widget.icon,
                contentDescription = null,
                modifier = widgetIconSharedElement(
                    widget = widget,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .size(CarDesignTokens.PrimaryIcon)
                        .carTouchTarget(),
                ),
                tint = contentColor,
            )
            Text(
                text = widget.title,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = widgetTitleSharedElement(
                    widget = widget,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.weight(1f),
                ),
            )
        }
    }
}
