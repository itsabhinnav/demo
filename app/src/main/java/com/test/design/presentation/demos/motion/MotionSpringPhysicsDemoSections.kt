package com.test.design.presentation.demos.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.motion.OemMotionScheme
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SpringPhysicsTokensSection(useExpressive: Boolean) {
    CustomSectionHeader(
        title = "M3 Spring Physics Tokens",
        subtitle = "Stiffness (k) and damping ratio (ζ) from Material motion tokens",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        Text(
            text = if (useExpressive) {
                "ExpressiveMotionTokens — lower damping, softer springs for hero IVI UI"
            } else {
                "StandardMotionTokens — tighter springs for utilitarian AAOS controls"
            },
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(bottom = OemSpacing.sm),
        )
        SpringTokenRow("Default spatial", useExpressive, OemMotionScheme.SpringToken.DefaultSpatial)
        SpringTokenRow("Fast spatial", useExpressive, OemMotionScheme.SpringToken.FastSpatial)
        SpringTokenRow("Slow spatial", useExpressive, OemMotionScheme.SpringToken.SlowSpatial)
        SpringTokenRow("Default effects", useExpressive, OemMotionScheme.SpringToken.DefaultEffects)
        SpringTokenRow("Fast effects", useExpressive, OemMotionScheme.SpringToken.FastEffects)
        SpringTokenRow("Slow effects", useExpressive, OemMotionScheme.SpringToken.SlowEffects)
        Text(
            text = "initialVelocity defaults to 0 in MotionScheme.expressive(). " +
                "Pass v₀ to spring() for swipe/fling panels (see below).",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}

@Composable
private fun SpringTokenRow(
    label: String,
    useExpressive: Boolean,
    token: OemMotionScheme.SpringToken,
) {
    val physics = if (useExpressive) {
        OemMotionScheme.expressivePhysics(token)
    } else {
        OemMotionScheme.standardPhysics(token)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = physics.label(),
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )
    }
}

@Composable
fun SpringSideBySideSection(animationsEnabled: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    val target = if (expanded) 120.dp else 48.dp
    val expressiveSize by animateDpAsState(
        targetValue = target,
        animationSpec = OemMotionScheme.springSpec<Dp>(
            OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial),
        ).let { if (animationsEnabled) it else snap() },
        label = "expressive-compare",
    )
    val standardSize by animateDpAsState(
        targetValue = target,
        animationSpec = OemMotionScheme.springSpec<Dp>(
            OemMotionScheme.standardPhysics(OemMotionScheme.SpringToken.DefaultSpatial),
        ).let { if (animationsEnabled) it else snap() },
        label = "standard-compare",
    )

    CustomSectionHeader(
        title = "Standard vs Expressive Springs",
        subtitle = "Same target — compare ζ and k from token sets side by side",
    )
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md)
            .then(
                if (animationsEnabled) {
                    Modifier.clickableToggle { expanded = !expanded }
                } else {
                    Modifier
                },
            ),
    ) {
        Text(
            text = if (expanded) "Tap to collapse both" else "Tap to expand both",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.md),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            SpringCompareColumn(
                title = "Expressive",
                subtitle = OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial).label(),
                size = expressiveSize,
                color = MaterialTheme.colorScheme.primary,
            )
            SpringCompareColumn(
                title = "Standard",
                subtitle = OemMotionScheme.standardPhysics(OemMotionScheme.SpringToken.DefaultSpatial).label(),
                size = standardSize,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun SpringCompareColumn(
    title: String,
    subtitle: String,
    size: androidx.compose.ui.unit.Dp,
    color: androidx.compose.ui.graphics.Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(OemVisuals.cardShape)
                .background(color),
        )
        Text(text = title, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = OemSpacing.sm))
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = OemOnSurfaceVariant)
    }
}

@Composable
fun InitialVelocityFlingSection(animationsEnabled: Boolean) {
    val scope = rememberCoroutineScope()
    val offsetAnim = remember { Animatable(0f) }
    var flingCount by remember { mutableIntStateOf(0) }

    CustomSectionHeader(
        title = "Initial Velocity (v₀)",
        subtitle = "Swipe/fling media drawer — expressive spatial spring with v₀",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        Text(
            text = "AAOS pattern: parked media queue panel flung open from edge swipe. " +
                "OemMotionScheme.initialVelocitySpatialSpec(v₀) adds momentum to the spring.",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.md)
                .height(100.dp)
                .clip(OemVisuals.cardShape)
                .background(OemSurfaceElevated),
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetAnim.value.roundToInt(), 0) }
                    .size(width = 140.dp, height = 100.dp)
                    .clip(OemVisuals.cardShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(OemSpacing.md),
                contentAlignment = Alignment.CenterStart,
            ) {
                Column {
                    Icon(Icons.Default.MusicNote, contentDescription = null)
                    Text(text = "Queue", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            CustomButton(
                text = "Fling open (v₀=1200)",
                onClick = {
                    if (!animationsEnabled) return@CustomButton
                    flingCount++
                    scope.launch {
                        offsetAnim.animateTo(
                            targetValue = 180f,
                            animationSpec = OemMotionScheme.springSpec(
                                OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial),
                            ),
                            initialVelocity = 1200f,
                        )
                    }
                },
                enabled = animationsEnabled,
            )
            CustomButton(
                text = "Snap closed",
                onClick = {
                    if (!animationsEnabled) return@CustomButton
                    scope.launch {
                        offsetAnim.animateTo(
                            targetValue = 0f,
                            animationSpec = OemMotionScheme.springSpec(
                                OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.FastSpatial),
                            ),
                        )
                    }
                },
                style = ButtonStyle.Secondary,
                enabled = animationsEnabled,
            )
        }
        if (flingCount > 0) {
            val physics = OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial)
            Text(
                text = "Fling #$flingCount · ${physics.label()} · v₀ passed to Animatable.animateTo()",
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.sm),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AaosMaterial3MotionSection(animationsEnabled: Boolean) {
    var switchOn by remember { mutableStateOf(true) }
    var checkboxOn by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0.4f) }
    var segmentIndex by remember { mutableIntStateOf(0) }
    var navIndex by remember { mutableIntStateOf(0) }
    var filterSelected by remember { mutableStateOf(false) }

    CustomSectionHeader(
        title = "M3 Components Using MotionScheme",
        subtitle = "Jetpack Material 3 reads MaterialTheme.motionScheme — no manual spring wiring",
    )
    Text(
        text = "Wrap AAOS screens in OemTheme { } with motionScheme = expressive (parked) or standard (driving). " +
            "Built-in M3 components inherit physics automatically.",
        style = MaterialTheme.typography.bodySmall,
        color = OemOnSurfaceVariant,
        modifier = Modifier.padding(bottom = OemSpacing.sm),
    )

    M3MotionComponentCard(
        title = "Switch · Checkbox · Slider",
        aaosUse = "Parked: defrost toggle, seat heater. Driving: avoid sliders — use presets.",
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            Switch(
                checked = switchOn,
                onCheckedChange = { if (animationsEnabled) switchOn = it },
                enabled = animationsEnabled,
            )
            Checkbox(
                checked = checkboxOn,
                onCheckedChange = { if (animationsEnabled) checkboxOn = it },
                enabled = animationsEnabled,
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = { if (animationsEnabled) sliderValue = it },
            enabled = animationsEnabled,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }

    M3MotionComponentCard(
        title = "SegmentedButton",
        aaosUse = "Drive mode selector (Eco/Normal/Sport) — spatial indicator uses theme springs",
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf("Eco", "Normal", "Sport").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = segmentIndex == index,
                    onClick = { if (animationsEnabled) segmentIndex = index },
                    enabled = animationsEnabled,
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                    icon = {},
                    label = { Text(label) },
                )
            }
        }
    }

    M3MotionComponentCard(
        title = "NavigationBar",
        aaosUse = "Bottom launcher rail — indicator slide uses defaultSpatialSpec()",
    ) {
        NavigationBar {
            val items = listOf(
                Triple("Climate", Icons.Default.AcUnit, 0),
                Triple("Nav", Icons.Default.Map, 1),
                Triple("Media", Icons.Default.MusicNote, 2),
                Triple("Settings", Icons.Default.Settings, 3),
            )
            items.forEach { (label, icon, index) ->
                NavigationBarItem(
                    selected = navIndex == index,
                    onClick = { if (animationsEnabled) navIndex = index },
                    enabled = animationsEnabled,
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                )
            }
        }
    }

    M3MotionComponentCard(
        title = "FilterChip",
        aaosUse = "Quick filters on parked media/search — effects spring on selection tint",
    ) {
        FilterChip(
            selected = filterSelected,
            onClick = { if (animationsEnabled) filterSelected = !filterSelected },
            enabled = animationsEnabled,
            label = { Text("Favorites") },
            leadingIcon = if (filterSelected) {
                { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else {
                null
            },
        )
    }

    CustomCard(modifier = Modifier.padding(top = OemSpacing.sm)) {
        Text(text = "MotionScheme token resolution", style = MaterialTheme.typography.titleSmall)
        Text(
            text = "OemMotionScheme reads ζ and k from MotionScheme.expressive() / standard():",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
        OemMotionScheme.SpringToken.entries.forEach { token ->
            val physics = OemMotionScheme.expressivePhysics(token)
            Text(
                text = "${token.name}: ${physics.label()} · resolved via MotionScheme.expressive()",
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.xs),
            )
        }
    }
}

@Composable
private fun M3MotionComponentCard(
    title: String,
    aaosUse: String,
    content: @Composable () -> Unit,
) {
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.xs)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(
            text = aaosUse,
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs, bottom = OemSpacing.sm),
        )
        content()
    }
}

private fun Modifier.clickableToggle(onClick: () -> Unit): Modifier =
    clickable(onClick = onClick).then(this)
