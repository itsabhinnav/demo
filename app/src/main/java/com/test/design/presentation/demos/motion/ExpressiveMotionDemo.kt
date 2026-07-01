package com.test.design.presentation.demos.motion

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSegmentedButton
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun ExpressiveMotionDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var useExpressive by remember { mutableStateOf(true) }
    val drivingState = com.test.design.component.core.currentDrivingUxState()
    val animationsEnabled = RestrictedComponentPolicy.maxAnimationDurationMs(drivingState) > 0

    DemoScaffold(
        title = "Expressive Motion",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "MaterialTheme.motionScheme provides spring physics from material3",
                    "Spatial specs animate size, position, and bounds",
                    "Effects specs animate color, alpha, and elevation",
                    "Expressive = lower damping and more bounce for hero UI",
                    "OemTheme uses expressive motion when parked, standard while driving",
                    if (animationsEnabled) {
                        "Tap the cards to compare standard vs expressive feel"
                    } else {
                        "Animations disabled while driving (AAOS policy)"
                    },
                ),
            )
        },
    ) {
        SchemeSection(
            useExpressive = useExpressive,
            onSchemeChanged = { useExpressive = it },
            animationsEnabled = animationsEnabled,
        )

        val motionScheme = if (useExpressive) {
            MotionScheme.expressive()
        } else {
            MotionScheme.standard()
        }

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            motionScheme = motionScheme,
        ) {
            SpatialMotionSection(animationsEnabled = animationsEnabled)
            EffectsMotionSection(animationsEnabled = animationsEnabled)
            SpeedComparisonSection(animationsEnabled = animationsEnabled)
            MaterialComponentsSection(animationsEnabled = animationsEnabled)
        }
    }
}

@Composable
private fun SchemeSection(
    useExpressive: Boolean,
    onSchemeChanged: (Boolean) -> Unit,
    animationsEnabled: Boolean,
) {
    CustomSectionHeader(
        title = "Motion Scheme",
        subtitle = if (animationsEnabled) {
            "Switch between Material 3 standard and expressive physics"
        } else {
            "Park to preview spring-based motion"
        },
    )
    CustomSegmentedButton(
        options = listOf("Standard", "Expressive"),
        selectedIndex = if (useExpressive) 1 else 0,
        onOptionSelected = { onSchemeChanged(it == 1) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
    )
}

@Composable
private fun SpatialMotionSection(animationsEnabled: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    val motionScheme = MaterialTheme.motionScheme
    val targetSize = if (expanded) 160.dp else 96.dp
    val size = animateDpWithPolicy(
        targetValue = targetSize,
        animationSpec = motionScheme.defaultSpatialSpec(),
        animationsEnabled = animationsEnabled,
        label = "spatial-size",
    )

    CustomSectionHeader(
        title = "Spatial Motion",
        subtitle = "MaterialTheme.motionScheme.defaultSpatialSpec()",
    )
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md)
            .clickable(enabled = animationsEnabled) { expanded = !expanded },
    ) {
        Text(
            text = if (expanded) "Tap to collapse" else "Tap to expand",
            style = MaterialTheme.typography.bodyMedium,
            color = OemOnSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .padding(top = OemSpacing.md)
                .size(size)
                .clip(OemVisuals.cardShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Spatial",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun EffectsMotionSection(animationsEnabled: Boolean) {
    var highlighted by remember { mutableStateOf(false) }
    val motionScheme = MaterialTheme.motionScheme
    val containerColor by animateColorAsState(
        targetValue = if (highlighted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            OemSurfaceElevated
        },
        animationSpec = effectsSpecForPolicy(
            animationSpec = motionScheme.defaultEffectsSpec(),
            animationsEnabled = animationsEnabled,
        ),
        label = "effects-color",
    )

    CustomSectionHeader(
        title = "Effects Motion",
        subtitle = "MaterialTheme.motionScheme.defaultEffectsSpec()",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md)
            .height(120.dp)
            .clip(OemVisuals.cardShape)
            .background(containerColor)
            .clickable(enabled = animationsEnabled) { highlighted = !highlighted },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (highlighted) "Effects active" else "Tap to highlight",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SpeedComparisonSection(animationsEnabled: Boolean) {
    var activeIndex by remember { mutableStateOf(0) }
    val motionScheme = MaterialTheme.motionScheme
    val specs = listOf(
        "Default" to motionScheme.defaultSpatialSpec<Dp>(),
        "Fast" to motionScheme.fastSpatialSpec<Dp>(),
        "Slow" to motionScheme.slowSpatialSpec<Dp>(),
    )
    val targetOffset = when (activeIndex) {
        1 -> 72.dp
        2 -> 144.dp
        else -> 0.dp
    }
    val offset = animateDpWithPolicy(
        targetValue = targetOffset,
        animationSpec = specs[activeIndex].second,
        animationsEnabled = animationsEnabled,
        label = "speed-offset",
    )

    CustomSectionHeader(
        title = "Spatial Speed",
        subtitle = "fastSpatialSpec() vs default vs slowSpatialSpec()",
    )
    CustomSegmentedButton(
        options = specs.map { it.first },
        selectedIndex = activeIndex,
        onOptionSelected = { activeIndex = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = OemSpacing.md),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md)
            .height(80.dp)
            .clip(OemVisuals.cardShape)
            .background(OemSurfaceElevated),
    ) {
        Box(
            modifier = Modifier
                .padding(start = offset, top = OemSpacing.md)
                .size(48.dp)
                .clip(OemVisuals.chipShape)
                .background(MaterialTheme.colorScheme.secondary),
        )
    }
}

@Composable
private fun MaterialComponentsSection(animationsEnabled: Boolean) {
    var checked by remember { mutableStateOf(false) }

    CustomSectionHeader(
        title = "Material Components",
        subtitle = "M3 controls inherit MaterialTheme.motionScheme automatically",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = { if (animationsEnabled) checked = it },
            enabled = animationsEnabled,
        )
        Text(
            text = "Switch uses theme motion when toggled",
            style = MaterialTheme.typography.bodyMedium,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}

@Composable
private fun animateDpWithPolicy(
    targetValue: Dp,
    animationSpec: FiniteAnimationSpec<Dp>,
    animationsEnabled: Boolean,
    label: String,
): Dp {
    val animated by animateDpAsState(
        targetValue = targetValue,
        animationSpec = if (animationsEnabled) animationSpec else snap(),
        label = label,
    )
    return animated
}

@Composable
private fun <T> effectsSpecForPolicy(
    animationSpec: AnimationSpec<T>,
    animationsEnabled: Boolean,
): AnimationSpec<T> = if (animationsEnabled) animationSpec else snap()
