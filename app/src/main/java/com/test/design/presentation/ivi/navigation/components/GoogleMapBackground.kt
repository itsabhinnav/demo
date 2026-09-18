package com.test.design.presentation.ivi.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

/** San Francisco demo center — matches mock IVI navigation destination. */
val DefaultMapCenter = LatLng(37.7749, -122.4194)

internal val MapPlaceholderColor = Color(0xFF1A1C1E)

private val DemoRouteColor = Color(0xFF4EA1FF)

private val DemoRoutePoints = listOf(
    LatLng(37.7599, -122.4148),
    LatLng(37.7655, -122.4190),
    LatLng(37.7710, -122.4225),
    LatLng(37.7749, -122.4194),
    LatLng(37.7792, -122.4149),
)

/** Forces Compose chrome into its own layer so it stays above the Maps SDK [GoogleMap] view. */
fun Modifier.mapChromeLayer(): Modifier = graphicsLayer { alpha = 0.99f }

/**
 * Maps SDK for Android background (dark color scheme) with optional demo route.
 * Pair overlay chrome with [mapChromeLayer] so Compose siblings draw above this AndroidView.
 */
@Composable
fun GoogleMapBackground(
    modifier: Modifier = Modifier,
    center: LatLng = DefaultMapCenter,
    zoom: Double = 14.5,
    showRoute: Boolean = true,
    interactive: Boolean = true,
) {
    val context = LocalContext.current
    var attachMap by remember { mutableStateOf(false) }
    val playServicesAvailable = remember(context) {
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        withFrameNanos { }
        attachMap = true
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, zoom.toFloat())
    }
    LaunchedEffect(center.latitude, center.longitude, zoom) {
        val next = CameraPosition.fromLatLngZoom(center, zoom.toFloat())
        if (cameraPositionState.position != next) {
            cameraPositionState.position = next
        }
    }

    val destinationMarker = rememberUpdatedMarkerState(position = DemoRoutePoints.last())
    val vehicleMarker = rememberUpdatedMarkerState(position = DemoRoutePoints.first())
    val uiSettings = remember(interactive) {
        MapUiSettings(
            compassEnabled = false,
            indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = interactive,
            scrollGesturesEnabled = interactive,
            tiltGesturesEnabled = interactive,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = interactive,
        )
    }
    val mapProperties = remember {
        MapProperties(
            isBuildingEnabled = true,
            minZoomPreference = 3f,
            maxZoomPreference = 19f,
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MapPlaceholderColor),
    ) {
        when {
            !playServicesAvailable -> DrivingMapBackdrop(modifier = Modifier.fillMaxSize())
            attachMap -> GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
                mapColorScheme = ComposeMapColorScheme.DARK,
            ) {
                if (showRoute) {
                    Polyline(
                        points = DemoRoutePoints,
                        color = DemoRouteColor,
                        width = 14f,
                    )
                    Marker(
                        state = destinationMarker,
                        title = "Destination",
                    )
                    Marker(
                        state = vehicleMarker,
                        title = "Vehicle",
                    )
                }
            }
        }
    }
}
