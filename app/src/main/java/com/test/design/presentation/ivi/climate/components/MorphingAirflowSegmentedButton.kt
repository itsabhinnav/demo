package com.test.design.presentation.ivi.climate.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.AirflowMode
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@Composable
fun MorphingAirflowSegmentedButton(
    selectedMode: AirflowMode,
    onModeSelected: (AirflowMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = AirflowMode.entries
    val selectedIndex = modes.indexOf(selectedMode)
    val motionSpec = MaterialTheme.motionScheme.slowSpatialSpec<Dp>()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(CarDesignTokens.MinTouchTarget + 12.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        val segmentWidth = maxWidth / modes.size
        val indicatorOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = motionSpec,
            label = "airflow_indicator_offset",
        )
        val indicatorWidth by animateDpAsState(
            targetValue = segmentWidth,
            animationSpec = motionSpec,
            label = "airflow_indicator_width",
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .fillMaxHeight()
                .padding(4.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary),
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            modes.forEach { mode ->
                val isSelected = mode == selectedMode
                val contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .carTouchTarget()
                        .clickable { onModeSelected(mode) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = mode.icon,
                        contentDescription = mode.label,
                        tint = contentColor,
                        modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                    )
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

private val AirflowMode.icon: ImageVector
    get() = when (this) {
        AirflowMode.Face -> AirflowFaceIcon
        AirflowMode.BiLevel -> AirflowBiLevelIcon
        AirflowMode.Feet -> AirflowFeetIcon
        AirflowMode.Auto -> Icons.Default.AutoMode
    }
