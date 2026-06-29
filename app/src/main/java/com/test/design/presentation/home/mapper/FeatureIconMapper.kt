package com.test.design.presentation.home.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.domain.model.FeatureDemo

fun mapFeatureIcon(feature: FeatureDemo): ImageVector {
    return when (feature.id) {
        "design-system" -> Icons.Default.FormatPaint
        "driving-ux" -> Icons.Default.DirectionsCar
        "components-gallery" -> Icons.Default.Widgets
        "compose-basics" -> Icons.Default.Apps
        "adaptive-layouts" -> Icons.Default.Dashboard
        "lists-grids" -> Icons.Default.GridView
        "tabs-demo" -> Icons.Default.Tab
        else -> Icons.Default.ViewModule
    }
}
