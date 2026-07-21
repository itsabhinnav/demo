package com.test.design.presentation.ivi.navigation.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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

internal val MapPlaceholderColor = Color(0xFF1A1C1E)

private val DemoRoutePoints = listOf(
    GeoPoint(37.7599, -122.4148),
    GeoPoint(37.7655, -122.4190),
    GeoPoint(37.7710, -122.4225),
    GeoPoint(37.7749, -122.4194),
    GeoPoint(37.7792, -122.4149),
)

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

/** Prefer [DrivingMapBackdrop] for in-Compose chrome. Use this only when MapView can own the window. */
fun Modifier.mapChromeLayer(): Modifier = graphicsLayer { alpha = 0.99f }

/**
 * OsmDroid map. Prefer [DrivingMapBackdrop] under Compose chrome — AndroidView paints above
 * Compose siblings and previously blacked out the AAOS launch buffer.
 */
@Composable
fun OsmMapBackground(
    modifier: Modifier = Modifier,
    center: GeoPoint = DefaultMapCenter,
    zoom: Double = 14.5,
    showRoute: Boolean = true,
    interactive: Boolean = true,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var attachMap by remember { mutableStateOf(false) }
    val mapHolder = remember { arrayOfNulls<MapView>(1) }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        withFrameNanos { }
        attachMap = true
    }

    DisposableEffect(lifecycleOwner, attachMap) {
        val observer = LifecycleEventObserver { _, event ->
            val view = mapHolder[0] ?: return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_RESUME -> view.onResume()
                Lifecycle.Event.ON_PAUSE -> view.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapHolder[0]?.let { map ->
                map.onPause()
                map.onDetach()
            }
            mapHolder[0] = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MapPlaceholderColor),
    ) {
        if (attachMap) {
            AndroidView(
                factory = { ctx ->
                    createMapView(ctx, center, zoom, showRoute, interactive).also { created ->
                        mapHolder[0] = created
                        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            created.onResume()
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
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
                onRelease = { view ->
                    view.onPause()
                    view.onDetach()
                    if (mapHolder[0] === view) mapHolder[0] = null
                },
            )
        }
    }
}

private fun createMapView(
    context: android.content.Context,
    center: GeoPoint,
    zoom: Double,
    showRoute: Boolean,
    interactive: Boolean,
): MapView = MapView(context).apply {
    setBackgroundColor(AndroidColor.parseColor("#1A1C1E"))
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
