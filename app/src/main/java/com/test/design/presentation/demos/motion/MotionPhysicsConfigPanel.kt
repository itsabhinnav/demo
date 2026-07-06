package com.test.design.presentation.demos.motion

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.Spring
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSegmentedButton
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.ButtonStyle
import com.test.design.component.motion.MotionSchemePreset
import com.test.design.component.motion.OemMotionPhysicsConfig
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import kotlin.math.roundToInt

@Composable
fun MotionPhysicsConfigPanel(
    config: OemMotionPhysicsConfig,
    onConfigChange: (OemMotionPhysicsConfig) -> Unit,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        CustomSectionHeader(
            title = "Motion Physics",
            subtitle = if (animationsEnabled) {
                "OEM tuning — changes apply live to previews"
            } else {
                "Park to tune motion (AAOS policy)"
            },
        )

        CustomSegmentedButton(
            options = MotionSchemePreset.entries.map { it.name },
            selectedIndex = config.preset.ordinal,
            onOptionSelected = { index ->
                onConfigChange(OemMotionPhysicsConfig.fromPreset(MotionSchemePreset.entries[index]))
            },
            modifier = Modifier.padding(vertical = OemSpacing.xs),
        )

        if (config.preset == MotionSchemePreset.Custom) {
            SpringGroup(
                title = "Spatial springs",
                subtitle = "List bounds, expand/collapse, layout shifts",
                damping = config.spatialDampingRatio,
                stiffness = config.spatialStiffness,
                onDampingChange = { onConfigChange(config.copy(spatialDampingRatio = it)) },
                onStiffnessChange = { onConfigChange(config.copy(spatialStiffness = it)) },
                animationsEnabled = animationsEnabled,
            )
            SpringGroup(
                title = "Effects springs",
                subtitle = "Color, alpha, elevation",
                damping = config.effectsDampingRatio,
                stiffness = config.effectsStiffness,
                onDampingChange = { onConfigChange(config.copy(effectsDampingRatio = it)) },
                onStiffnessChange = { onConfigChange(config.copy(effectsStiffness = it)) },
                animationsEnabled = animationsEnabled,
            )
        }

        CustomSectionHeader(
            title = "List scroll",
            subtitle = "Fling decay after finger lift",
        )
        CustomSlider(
            value = config.flingFrictionMultiplier * 100f,
            onValueChange = {
                onConfigChange(config.copy(flingFrictionMultiplier = it / 100f))
            },
            valueRange = 40f..250f,
            label = "Fling friction",
            steps = 20,
            enabled = animationsEnabled,
        )
        Text(
            text = "Lower = longer coast; higher = snappier stop",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )

        CustomSectionHeader(
            title = "Progress bar",
            subtitle = "Download, charge, and task fills",
        )
        CustomSwitch(
            label = "Spring fill",
            checked = config.progressUseSpring,
            onCheckedChange = { onConfigChange(config.copy(progressUseSpring = it)) },
            enabled = animationsEnabled,
        )
        if (config.progressUseSpring) {
            CustomSlider(
                value = config.progressStiffness,
                onValueChange = { onConfigChange(config.copy(progressStiffness = it)) },
                valueRange = Spring.StiffnessVeryLow..Spring.StiffnessHigh,
                label = "Progress stiffness",
                steps = 12,
                enabled = animationsEnabled,
            )
        } else {
            CustomSlider(
                value = config.progressDurationMs.toFloat(),
                onValueChange = { onConfigChange(config.copy(progressDurationMs = it.roundToInt())) },
                valueRange = 100f..800f,
                label = "Progress duration (ms)",
                steps = 13,
                enabled = animationsEnabled,
            )
        }

        CustomButton(
            text = "Reset to defaults",
            onClick = { onConfigChange(OemMotionPhysicsConfig.Default) },
            style = ButtonStyle.Secondary,
            modifier = Modifier.padding(top = OemSpacing.sm),
            enabled = animationsEnabled,
        )
    }
}

@Composable
private fun SpringGroup(
    title: String,
    subtitle: String,
    damping: Float,
    stiffness: Float,
    onDampingChange: (Float) -> Unit,
    onStiffnessChange: (Float) -> Unit,
    animationsEnabled: Boolean,
) {
    CustomSectionHeader(title = title, subtitle = subtitle)
    CustomSlider(
        value = damping * 100f,
        onValueChange = { onDampingChange(it / 100f) },
        valueRange = 10f..100f,
        label = "Damping",
        steps = 8,
        enabled = animationsEnabled,
    )
    CustomSlider(
        value = stiffness,
        onValueChange = onStiffnessChange,
        valueRange = Spring.StiffnessVeryLow..Spring.StiffnessHigh,
        label = "Stiffness",
        steps = 12,
        enabled = animationsEnabled,
    )
}
