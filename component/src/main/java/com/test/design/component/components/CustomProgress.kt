package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.oemDrivingTouchTarget
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder

@Composable
fun CustomLinearProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val shape = OemVisuals.chipShape
    val trackHeight = OemSpacing.sm + OemSpacing.xs
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(bottom = OemSpacing.sm),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(shape)
                .background(OemSurfaceVariant)
                .oemSurfaceBorder(shape, OemBorder),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress().coerceIn(0f, 1f))
                    .height(trackHeight)
                    .clip(shape)
                    .background(OemPrimary),
            )
        }
    }
}

@Composable
fun CustomCircularProgress(
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val shape = OemVisuals.iconContainerShape
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(OemSpacing.xl + OemSpacing.md)
                .clip(shape)
                .background(OemSurfaceVariant)
                .oemSurfaceBorder(shape, OemBorder),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(OemSpacing.lg)
                    .clip(OemVisuals.chipShape)
                    .background(OemPrimary),
            )
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.sm),
            )
        }
    }
}

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    label: String = "Value",
    steps: Int = 0,
    enabled: Boolean = true,
) {
    val drivingState = currentDrivingUxState()
    val fineControlsAllowed = RestrictedComponentPolicy.allowsFineControls(drivingState)
    val sliderEnabled = enabled && fineControlsAllowed
    val shape = OemVisuals.chipShape
    val normalized = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$label: ${value.toInt()}",
            style = MaterialTheme.typography.titleMedium,
            color = if (sliderEnabled) OemOnSurface else OemOnSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.padding(bottom = OemSpacing.sm),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(OemSpacing.minTouchTarget)
                .oemDrivingTouchTarget(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OemSpacing.sm)
                    .clip(shape)
                    .background(OemSurfaceVariant)
                    .oemSurfaceBorder(shape, OemBorder),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(normalized)
                        .height(OemSpacing.sm)
                        .clip(shape)
                        .background(if (sliderEnabled) OemPrimary else OemPrimary.copy(alpha = 0.4f)),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(normalized)
                    .padding(end = OemSpacing.sm),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Box(
                    modifier = Modifier
                        .size(OemSpacing.lg)
                        .clip(OemVisuals.iconContainerShape)
                        .background(if (sliderEnabled) OemPrimary else OemPrimary.copy(alpha = 0.4f))
                        .oemSurfaceBorder(OemVisuals.iconContainerShape, OemBorder),
                )
            }
            if (sliderEnabled) {
                androidx.compose.material3.Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    steps = steps,
                    modifier = Modifier.matchParentSize(),
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = androidx.compose.ui.graphics.Color.Transparent,
                        activeTrackColor = androidx.compose.ui.graphics.Color.Transparent,
                        inactiveTrackColor = androidx.compose.ui.graphics.Color.Transparent,
                        activeTickColor = androidx.compose.ui.graphics.Color.Transparent,
                        inactiveTickColor = androidx.compose.ui.graphics.Color.Transparent,
                    ),
                )
            }
        }
    }
}
