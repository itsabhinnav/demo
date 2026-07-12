package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens

/**
 * Vertical map control stack (compass / zoom / chargers / settings).
 */
@Composable
fun MapSideControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onCompass: () -> Unit = {},
    onChargers: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MapControlButton(Icons.Default.Explore, "Compass", onCompass)
        MapControlButton(Icons.Default.Add, "Zoom in", onZoomIn)
        MapControlButton(Icons.Default.Remove, "Zoom out", onZoomOut)
        MapControlButton(Icons.Default.EvStation, "Chargers", onChargers)
        MapControlButton(Icons.Default.Settings, "Settings", onOpenSettings)
    }
}

@Composable
private fun MapControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color(0xF01C1C1E),
        shadowElevation = 8.dp,
        modifier = Modifier.size(CarDesignTokens.MinTouchTarget),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
            )
        }
    }
}
