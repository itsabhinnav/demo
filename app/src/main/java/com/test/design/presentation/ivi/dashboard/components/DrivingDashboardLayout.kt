package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.design.presentation.ivi.climate.ClimateEvent
import com.test.design.presentation.ivi.climate.ClimateUiState
import com.test.design.presentation.ivi.climate.components.FrontDefrostIcon
import com.test.design.presentation.ivi.climate.components.SeatHeatIcon
import com.test.design.presentation.ivi.climate.formatTemperature
import com.test.design.presentation.ivi.dashboard.DashboardEvent
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.media.MediaEvent
import com.test.design.presentation.ivi.media.MediaUiState
import com.test.design.presentation.ivi.navigation.NavigationUiState
import com.test.design.presentation.ivi.navigation.components.DefaultMapCenter
import com.test.design.presentation.ivi.navigation.components.OsmMapBackground
import com.test.design.presentation.ivi.vehicle.VehicleUiState
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.climateAmbientColor
import com.test.design.theme.temperatureToFraction
import org.osmdroid.util.GeoPoint

/** Sidebar takes ~30% of the driving home (map keeps ~70%). */
private const val SidebarWidthFraction = 0.30f
private val OverlayInset = 16.dp
private val MapSearchShape = RoundedCornerShape(44.dp)

/**
 * Full-bleed map with floating sidebar, search, map controls, and HVAC bar.
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
    onClimateEvent: (ClimateEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onOpenWidgetDashboard: (() -> Unit)? = null,
    mapCenter: GeoPoint? = null,
    initialMapZoom: Double = 14.5,
    showMapRoute: Boolean = false,
    onOpenMain: (() -> Unit)? = null,
) {
    var mapZoom by remember(initialMapZoom) { mutableDoubleStateOf(initialMapZoom) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sidebarWidth = maxWidth * SidebarWidthFraction
        val mapContentStart = sidebarWidth + OverlayInset

        OsmMapBackground(
            modifier = Modifier.fillMaxSize(),
            center = mapCenter ?: DefaultMapCenter,
            showRoute = showMapRoute,
            interactive = true,
            zoom = mapZoom,
        )

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
                .align(Alignment.CenterStart)
                .padding(OverlayInset)
                .width(sidebarWidth - OverlayInset)
                .fillMaxHeight(),
        )

        MapSearchBar(
            onClick = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Navigation)) },
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(
                    start = mapContentStart,
                    end = OverlayInset + 64.dp,
                    top = OverlayInset,
                ),
        )

        MapSideControls(
            onZoomIn = { mapZoom = (mapZoom + 1.0).coerceAtMost(19.0) },
            onZoomOut = { mapZoom = (mapZoom - 1.0).coerceAtLeast(3.0) },
            onOpenSettings = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Settings)) },
            onOpenHome = onOpenMain,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = OverlayInset),
        )

        MapHvacBar(
            climateState = climateState,
            climateTemperature = climateTemperature,
            onClimateEvent = onClimateEvent,
            onExpandClimate = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Climate)) },
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = mapContentStart,
                    end = OverlayInset,
                    bottom = OverlayInset,
                ),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MapSearchBar(
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Navigation,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
            clipShape = MapSearchShape,
        ),
        shape = MapSearchShape,
        color = Color(0xF01C1C1E),
        shadowElevation = 10.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
            )
            Text(
                text = "Search maps",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 18.dp),
            )
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(28.dp),
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
    onClimateEvent: (ClimateEvent) -> Unit,
    onExpandClimate: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val tempFraction = temperatureToFraction(
        celsius = climateTemperature,
        min = climateState.minTemperature,
        max = climateState.maxTemperature,
    )
    val ambient by animateColorAsState(
        targetValue = climateAmbientColor(tempFraction),
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "hvac_ambient",
    )
    val seatHeatTint = Color(0xFFFF8A65)

    Surface(
        onClick = onExpandClimate,
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Climate,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        ),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xF01C1C1E),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            ambient.copy(alpha = 0.18f),
                            Color.Transparent,
                            ambient.copy(alpha = 0.10f),
                        ),
                    ),
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HvacLevelControl(
                icon = SeatHeatIcon,
                contentDescription = "Seat heat",
                level = climateState.seatHeatLevel,
                maxLevel = climateState.maxSeatHeatLevel,
                activeColor = seatHeatTint,
                onClick = { onClimateEvent(ClimateEvent.CycleSeatHeat) },
            )
            HvacToggleControl(
                icon = FrontDefrostIcon,
                contentDescription = "Front defrost",
                active = climateState.isFrontDefrostOn,
                activeColor = Color.White,
                onClick = { onClimateEvent(ClimateEvent.ToggleFrontDefrost) },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onExpandClimate)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = formatTemperature(climateTemperature, climateState.temperatureUnit),
                    color = ambient,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (climateState.isAcEnabled) "A/C · Fan ${climateState.fanSpeed}" else "Fan ${climateState.fanSpeed}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            HvacToggleControl(
                icon = Icons.Default.AcUnit,
                contentDescription = "A/C",
                active = climateState.isAcEnabled,
                activeColor = Color(0xFF4EA1FF),
                onClick = { onClimateEvent(ClimateEvent.ToggleAc) },
            )
            CompactFanBars(
                fanSpeed = climateState.fanSpeed,
                maxFanSpeed = climateState.maxFanSpeed,
                activeColor = ambient,
                onSpeedSelected = { onClimateEvent(ClimateEvent.SetFanSpeed(it)) },
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            IconButton(
                onClick = onExpandClimate,
                modifier = Modifier.size(CarDesignTokens.MinTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandLess,
                    contentDescription = "Open climate",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                )
            }
        }
    }
}

@Composable
private fun HvacToggleControl(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val tint by animateColorAsState(
        targetValue = if (active) activeColor else Color.White.copy(alpha = 0.4f),
        animationSpec = effectsSpec,
        label = "hvac_toggle_tint_$contentDescription",
    )
    val container by animateColorAsState(
        targetValue = if (active) activeColor.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = effectsSpec,
        label = "hvac_toggle_bg_$contentDescription",
    )

    Box(
        modifier = Modifier
            .size(CarDesignTokens.MinTouchTarget)
            .clip(CircleShape)
            .background(container)
            .semantics {
                role = Role.Switch
                this.contentDescription =
                    "$contentDescription ${if (active) "on" else "off"}"
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
        )
    }
}

@Composable
private fun HvacLevelControl(
    icon: ImageVector,
    contentDescription: String,
    level: Int,
    maxLevel: Int,
    activeColor: Color,
    onClick: () -> Unit,
) {
    val active = level > 0
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val tint by animateColorAsState(
        targetValue = if (active) activeColor else Color.White.copy(alpha = 0.4f),
        animationSpec = effectsSpec,
        label = "hvac_level_tint_$contentDescription",
    )
    val container by animateColorAsState(
        targetValue = if (active) activeColor.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = effectsSpec,
        label = "hvac_level_bg_$contentDescription",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .sizeIn(minWidth = CarDesignTokens.MinTouchTarget, minHeight = CarDesignTokens.MinTouchTarget)
            .clip(RoundedCornerShape(20.dp))
            .background(container)
            .semantics {
                role = Role.Button
                this.contentDescription =
                    "$contentDescription, ${if (active) "level $level" else "off"}"
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(maxLevel) { index ->
                val lit = index < level
                Box(
                    modifier = Modifier
                        .size(width = 14.dp, height = 5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(
                            if (lit) tint else Color.White.copy(alpha = 0.2f),
                        ),
                )
            }
        }
    }
}

@Composable
private fun CompactFanBars(
    fanSpeed: Int,
    maxFanSpeed: Int,
    activeColor: Color,
    onSpeedSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    Row(
        modifier = modifier
            .height(CarDesignTokens.MinTouchTarget)
            .semantics {
                contentDescription = "Fan speed $fanSpeed of $maxFanSpeed"
            },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        repeat(maxFanSpeed) { index ->
            val level = index + 1
            val active = level <= fanSpeed
            val fraction by animateFloatAsState(
                targetValue = if (active) 1f else 0.35f,
                animationSpec = spatialSpec,
                label = "hvac_fan_$level",
            )
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height((18 + level * 10).dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(activeColor.copy(alpha = 0.25f + fraction * 0.7f))
                    .clickable { onSpeedSelected(level) },
            )
        }
    }
}
