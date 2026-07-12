package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.design.presentation.ivi.climate.ClimateEvent
import com.test.design.presentation.ivi.climate.ClimateUiState
import com.test.design.presentation.ivi.climate.formatTemperature
import com.test.design.presentation.ivi.dashboard.DashboardEvent
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.media.MediaEvent
import com.test.design.presentation.ivi.media.MediaUiState
import com.test.design.presentation.ivi.navigation.NavigationUiState
import com.test.design.presentation.ivi.navigation.components.OsmMapBackground
import com.test.design.presentation.ivi.vehicle.VehicleUiState

private val SidebarWidth = 460.dp

/**
 * Fixed-width left rail + map. Sidebar uses absolute width so app tiles never
 * compete with [Modifier.weight] and clip.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DrivingDashboardLayout(
    vehicleState: VehicleUiState,
    @Suppress("UNUSED_PARAMETER") navigationState: NavigationUiState,
    mediaState: MediaUiState,
    climateState: ClimateUiState,
    climateTemperature: Int,
    onEvent: (DashboardEvent) -> Unit,
    onMediaEvent: (MediaEvent) -> Unit,
    @Suppress("UNUSED_PARAMETER") onClimateEvent: (ClimateEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onOpenWidgetDashboard: (() -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxSize()) {
        DrivingSidebar(
            vehicleState = vehicleState,
            mediaState = mediaState,
            onVehicleClick = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Vehicle)) },
            onMediaEvent = onMediaEvent,
            onExpandMedia = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Media)) },
            onOpenApp = { onEvent(DashboardEvent.WidgetTapped(it)) },
            onOpenWidgetDashboard = onOpenWidgetDashboard,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .width(SidebarWidth)
                .fillMaxHeight(),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            OsmMapBackground(
                modifier = Modifier.fillMaxSize(),
                showRoute = false,
                interactive = true,
            )

            MapSearchBar(
                onClick = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Navigation)) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            )

            MapHvacBar(
                climateState = climateState,
                climateTemperature = climateTemperature,
                onExpandClimate = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Climate)) },
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun MapSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = Color(0xF01C1C1E),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = "Search maps",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MapHvacBar(
    climateState: ClimateUiState,
    climateTemperature: Int,
    onExpandClimate: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onExpandClimate,
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Climate,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        ),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xF01C1C1E),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "CLIMATE",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AcUnit,
                    contentDescription = "A/C",
                    tint = if (climateState.isAcEnabled) {
                        Color(0xFF4EA1FF)
                    } else {
                        Color.White.copy(alpha = 0.45f)
                    },
                    modifier = Modifier.size(26.dp),
                )
                Text(
                    text = formatTemperature(climateTemperature, climateState.temperatureUnit),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (climateState.isAcEnabled) "A/C On" else "A/C Off",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 14.sp,
                )
            }
            Text(
                text = "Open",
                color = Color(0xFF4EA1FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
