package com.test.design.presentation.home.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Checklist
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
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.domain.model.FeatureDemo

fun mapFeatureIcon(feature: FeatureDemo): ImageVector {
    return when (feature.id) {
        "design-system" -> Icons.Default.FormatPaint
        "theming-lab" -> Icons.Default.Palette
        "token-browser" -> Icons.Default.Tune
        "accessibility-audit" -> Icons.Default.AccessibilityNew
        "figma-checklist" -> Icons.Default.Checklist
        "expressive-motion" -> Icons.Default.Animation
        "motion-easing-duration" -> Icons.Default.Animation
        "motion-transition-patterns" -> Icons.Default.Animation
        "driving-ux" -> Icons.Default.DirectionsCar
        "components-gallery" -> Icons.Default.Widgets
        "component-state-matrix" -> Icons.Default.GridOn
        "component-specs" -> Icons.Default.Description
        "component-playground" -> Icons.Default.TouchApp
        "flow-builder" -> Icons.Default.AccountTree
        "input-modality" -> Icons.Default.TouchApp
        "adaptive-layouts" -> Icons.Default.Dashboard
        "lists-grids" -> Icons.Default.GridView
        "tabs-demo" -> Icons.Default.Tab
        "ev-dashboard" -> Icons.Default.BatteryChargingFull
        "software-update" -> Icons.Default.SystemUpdate
        "telematics" -> Icons.Default.LocationOn
        else -> Icons.Default.ViewModule
    }
}
