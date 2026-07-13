package com.test.design.presentation.ivi.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.IviExpressiveTheme
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.components.MapSideControls
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.navigation.components.DefaultMapCenter
import com.test.design.presentation.ivi.navigation.components.OsmMapBackground
import org.osmdroid.util.GeoPoint

private val MapOverlayInset = 16.dp

/**
 * Full-bleed map surface for AAOS Scalable UI map panels — no sidebar or widget chrome.
 */
@Composable
fun MapScreen(
    config: MapLaunchConfig,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val center = config.center ?: DefaultMapCenter
    var mapZoom by remember(config) { mutableDoubleStateOf(config.zoom) }

    IviExpressiveTheme {
        Box(modifier = modifier.fillMaxSize()) {
            OsmMapBackground(
                modifier = Modifier.fillMaxSize(),
                center = center,
                zoom = mapZoom,
                showRoute = config.showRoute,
                interactive = true,
            )

            if (config.showBack) {
                WidgetScreenHeader(
                    title = DashboardWidget.Navigation.title,
                    onBack = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(MapOverlayInset),
                )
            }

            MapSideControls(
                onZoomIn = { mapZoom = (mapZoom + 1.0).coerceAtMost(19.0) },
                onZoomOut = { mapZoom = (mapZoom - 1.0).coerceAtLeast(3.0) },
                onOpenSettings = {},
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = MapOverlayInset),
            )
        }
    }
}
