package com.test.design.presentation.demos.motion

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomLinearProgress
import com.test.design.component.components.CustomList
import com.test.design.component.components.CustomListItem
import com.test.design.component.components.CustomListItemRow
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSegmentedButton
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.motion.OemMotionPhysicsConfig
import com.test.design.component.motion.progressSpec
import com.test.design.component.motion.rememberOemFlingBehavior
import com.test.design.component.motion.toMotionScheme
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.presentation.demos.shared.DemoScaffold
import kotlinx.coroutines.delay

@Composable
fun ExpressiveMotionDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var config by remember { mutableStateOf(OemMotionPhysicsConfig.Default) }
    val drivingState = com.test.design.component.core.currentDrivingUxState()
    val animationsEnabled = RestrictedComponentPolicy.maxAnimationDurationMs(drivingState) > 0

    DemoScaffold(
        title = "Motion Physics",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            MotionPhysicsConfigPanel(
                config = config,
                onConfigChange = { config = it },
                animationsEnabled = animationsEnabled,
            )
        },
    ) {
        val motionScheme = config.toMotionScheme()

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            motionScheme = motionScheme,
        ) {
            PresetSummarySection(config = config)

            ListScrollMotionSection(
                config = config,
                animationsEnabled = animationsEnabled,
            )
            ProgressMotionSection(
                config = config,
                animationsEnabled = animationsEnabled,
            )
            SpatialMotionSection(animationsEnabled = animationsEnabled)
            EffectsMotionSection(animationsEnabled = animationsEnabled)
            SpeedComparisonSection(animationsEnabled = animationsEnabled)
            MaterialComponentsSection(animationsEnabled = animationsEnabled)
        }
    }
}

@Composable
private fun PresetSummarySection(config: OemMotionPhysicsConfig) {
    CustomSectionHeader(
        title = "OEM motion profile",
        subtitle = "${config.preset.name} — tweak springs and scroll physics in the sidebar",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        Text(
            text = buildString {
                append("Fling friction ×${"%.2f".format(config.flingFrictionMultiplier)}")
                append(" · Progress ")
                append(
                    if (config.progressUseSpring) {
                        "spring (${config.progressStiffness.toInt()} stiffness)"
                    } else {
                        "${config.progressDurationMs}ms tween"
                    },
                )
            },
            style = MaterialTheme.typography.bodyMedium,
            color = OemOnSurfaceVariant,
        )
    }
}

@Composable
private fun ListScrollMotionSection(
    config: OemMotionPhysicsConfig,
    animationsEnabled: Boolean,
) {
    val flingBehavior = rememberOemFlingBehavior(config, animationsEnabled)
    val listItems = remember {
        (1..24).map { index ->
            CustomListItem(
                id = index.toString(),
                title = "Media track $index",
                subtitle = "Fling to feel OEM scroll decay",
            )
        }
    }

    CustomSectionHeader(
        title = "List scroll physics",
        subtitle = "LazyColumn fling decay — common in media, settings, and nav lists",
    )
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
    ) {
        Text(
            text = if (animationsEnabled) {
                "Swipe fast and release — adjust fling friction in the sidebar"
            } else {
                "Scrolling only while parked"
            },
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(bottom = OemSpacing.sm),
        )
        CustomList(
            items = listItems,
            key = { it.id },
            scrollable = true,
            flingBehavior = flingBehavior,
            modifier = Modifier.height(220.dp),
            onItemClick = {},
        ) { item ->
            CustomListItemRow(title = item.title, subtitle = item.subtitle)
        }
    }
}

@Composable
private fun ProgressMotionSection(
    config: OemMotionPhysicsConfig,
    animationsEnabled: Boolean,
) {
    var targetProgress by remember { mutableFloatStateOf(0.25f) }
    var simulationTick by remember { mutableIntStateOf(0) }
    val progressSpec = config.progressSpec(animationsEnabled)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = progressSpec,
        label = "oem-progress",
    )

    LaunchedEffect(simulationTick) {
        if (simulationTick == 0) return@LaunchedEffect
        if (!animationsEnabled) {
            targetProgress = 1f
            return@LaunchedEffect
        }
        targetProgress = 0f
        delay(120)
        targetProgress = 0.35f
        delay((config.progressDurationMs * 0.6).toLong().coerceAtLeast(200))
        targetProgress = 0.72f
        delay((config.progressDurationMs * 0.5).toLong().coerceAtLeast(180))
        targetProgress = 1f
    }

    CustomSectionHeader(
        title = "Progress bar motion",
        subtitle = "Software update, EV charge, and task completion fills",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        CustomLinearProgress(
            progress = { animatedProgress },
            label = "Download ${(animatedProgress * 100).toInt()}%",
            modifier = Modifier.fillMaxWidth(),
        )
        CustomButton(
            text = if (simulationTick == 0) "Simulate download" else "Replay",
            onClick = { simulationTick++ },
            style = ButtonStyle.Primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.md),
            enabled = animationsEnabled,
        )
    }
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
        title = "Spatial motion",
        subtitle = "motionScheme.defaultSpatialSpec() — bounds and layout",
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
        title = "Effects motion",
        subtitle = "motionScheme.defaultEffectsSpec() — color and surface",
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
        title = "Spatial speed tokens",
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
        title = "Material components",
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
