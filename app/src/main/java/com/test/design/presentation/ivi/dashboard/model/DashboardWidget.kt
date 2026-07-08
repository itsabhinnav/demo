package com.test.design.presentation.ivi.dashboard.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

enum class DashboardWidget(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val sharedElementKey: String,
    val gridColumnSpan: Int = 1,
    val gridRowSpan: Int = 2,
) {
    Media(
        title = "Media",
        subtitle = "Now playing · Synthwave Nights",
        icon = Icons.Default.MusicNote,
        sharedElementKey = "widget_media",
    ),
    Climate(
        title = "Climate",
        subtitle = "22°C · Auto airflow",
        icon = Icons.Default.AcUnit,
        sharedElementKey = "widget_climate",
    ),
    Navigation(
        title = "Navigation",
        subtitle = "Home · 12 min",
        icon = Icons.Default.Navigation,
        sharedElementKey = "widget_navigation",
    ),
    Vehicle(
        title = "Vehicle",
        subtitle = "82% charge · 240 mi",
        icon = Icons.Default.DirectionsCar,
        sharedElementKey = "widget_vehicle",
    ),
    Energy(
        title = "Energy",
        subtitle = "Regeneration · Balanced",
        icon = Icons.Default.ElectricBolt,
        sharedElementKey = "widget_energy",
    ),
    Calls(
        title = "Calls",
        subtitle = "2 recent contacts",
        icon = Icons.Default.Call,
        sharedElementKey = "widget_calls",
    ),
    Camera(
        title = "Camera",
        subtitle = "Rear view · Ready",
        icon = Icons.Default.CameraAlt,
        sharedElementKey = "widget_camera",
    ),
    Trips(
        title = "Trips",
        subtitle = "This week · 184 mi",
        icon = Icons.Default.Timeline,
        sharedElementKey = "widget_trips",
    ),
}
