package com.test.design.presentation.ivi.dashboard.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

enum class DashboardWidget(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val sharedElementKey: String,
    val gridColumnSpan: Int = 1,
    val gridRowSpan: Int = 2,
) {
    AdaptiveSpace(
        title = "Adaptive Space",
        subtitle = "Scalable UI · Play demo",
        icon = Icons.Default.Layers,
        sharedElementKey = "widget_adaptive_space",
    ),
    DualZone(
        title = "Dual Zone",
        subtitle = "Driver · Passenger MUMD",
        icon = Icons.Default.People,
        sharedElementKey = "widget_dual_zone",
    ),
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
    VirtualAssistant(
        title = "Assistant",
        subtitle = "Eyes · gaze · lip-sync · drive context",
        icon = Icons.Default.AutoAwesome,
        sharedElementKey = "widget_virtual_assistant",
    ),
    MaterialComponents(
        title = "Material",
        subtitle = "Buttons, chips, cards, sliders",
        icon = Icons.Default.Widgets,
        sharedElementKey = "widget_material",
    ),
    CustomizedMaterial(
        title = "Customized",
        subtitle = "OEM brand on Material",
        icon = Icons.Default.Palette,
        sharedElementKey = "widget_customized",
    ),
    Settings(
        title = "Settings",
        subtitle = "Driving UX · Motion · Display",
        icon = Icons.Default.Settings,
        sharedElementKey = "widget_settings",
    ),
}
