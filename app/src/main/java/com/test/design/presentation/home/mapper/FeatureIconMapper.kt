package com.test.design.presentation.home.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.domain.model.FeatureDemo

fun mapFeatureIcon(feature: FeatureDemo): ImageVector {
    return when (feature.id) {
        "design-system" -> Icons.Default.FormatPaint
        "expressive-motion" -> Icons.Default.Animation
        "driving-ux" -> Icons.Default.DirectionsCar
        "components-gallery" -> Icons.Default.Widgets
        "component-playground" -> Icons.Default.TouchApp
        "adaptive-layouts" -> Icons.Default.Dashboard
        "lists-grids" -> Icons.Default.GridView
        "tabs-demo" -> Icons.Default.Tab
        "ev-dashboard" -> Icons.Default.BatteryChargingFull
        "software-update" -> Icons.Default.SystemUpdate
        "telematics" -> Icons.Default.LocationOn
        else -> Icons.Default.ViewModule
    }
}
