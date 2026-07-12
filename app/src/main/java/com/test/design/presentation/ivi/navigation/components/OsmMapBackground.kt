package com.test.design.presentation.ivi.navigation.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/** San Francisco demo center — matches mock IVI navigation destination. */
val DefaultMapCenter = GeoPoint(37.7749, -122.4194)

private val DemoRoutePoints = listOf(
    GeoPoint(37.7599, -122.4148),
    GeoPoint(37.7655, -122.4190),
    GeoPoint(37.7710, -122.4225),
    GeoPoint(37.7749, -122.4194),
    GeoPoint(37.7792, -122.4149),
)

/**
 * Dark Carto basemap over OpenStreetMap data — suits the automotive night UI.
 */
private val CartoDarkTiles: OnlineTileSourceBase = object : XYTileSource(
    "CartoDark",
    1,
    19,
    256,
    ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/dark_all/",
        "https://b.basemaps.cartocdn.com/dark_all/",
        "https://c.basemaps.cartocdn.com/dark_all/",
    ),
    "© OpenStreetMap contributors © CARTO",
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl +
            MapTileIndex.getZoom(pMapTileIndex) + "/" +
            MapTileIndex.getX(pMapTileIndex) + "/" +
            MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
    }
}

/**
 * Full-bleed OpenStreetMap view for IVI navigation / driving hub.
 * OsmDroid + Carto dark tiles (OSM data).
 */
@Composable
fun OsmMapBackground(
    modifier: Modifier = Modifier,
    center: GeoPoint = DefaultMapCenter,
    zoom: Double = 14.5,
    showRoute: Boolean = true,
    interactive: Boolean = true,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        Configuration.getInstance().apply {
            load(
                context,
                context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE),
            )
            userAgentValue = context.packageName
        }
        MapView(context).apply {
            setTileSource(CartoDarkTiles)
            setMultiTouchControls(interactive)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            minZoomLevel = 3.0
            maxZoomLevel = 19.0
            controller.setZoom(zoom)
            controller.setCenter(center)
            if (showRoute) {
                overlays.add(
                    Polyline().apply {
                        setPoints(DemoRoutePoints)
                        outlinePaint.color = 0xFF4EA1FF.toInt()
                        outlinePaint.strokeWidth = 14f
                    },
                )
                overlays.add(
                    Marker(this).apply {
                        position = DemoRoutePoints.last()
                        title = "Destination"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    },
                )
                overlays.add(
                    Marker(this).apply {
                        position = DemoRoutePoints.first()
                        title = "Vehicle"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    },
                )
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            if (view.mapCenter.latitude != center.latitude ||
                view.mapCenter.longitude != center.longitude
            ) {
                view.controller.setCenter(center)
            }
            if (view.zoomLevelDouble != zoom) {
                view.controller.setZoom(zoom)
            }
        },
    )
}
