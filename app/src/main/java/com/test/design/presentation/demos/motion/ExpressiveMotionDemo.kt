package com.test.design.presentation.demos.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomLinearProgress
import com.test.design.component.components.CustomList
import com.test.design.component.components.CustomListItem
import com.test.design.component.components.CustomListItemRow
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSegmentedButton
import com.test.design.component.components.CustomTabs
import com.test.design.component.core.DrivingUxState
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.motion.MotionSchemePreset
import com.test.design.component.motion.OemMotion
import com.test.design.component.motion.OemMotionPhysicsConfig
import com.test.design.component.motion.progressSpec
import com.test.design.component.motion.rememberOemFlingBehavior
import com.test.design.component.motion.toMotionScheme
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.navigation.NavMotion
import kotlinx.coroutines.delay

@Composable
fun ExpressiveMotionDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var config by remember { mutableStateOf(OemMotionPhysicsConfig.Default) }
    val drivingState = currentDrivingUxState()
    val animationsEnabled = RestrictedComponentPolicy.maxAnimationDurationMs(drivingState) > 0
    val useExpressive = config.preset != MotionSchemePreset.Standard

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

            OemThemePolicySection(
                drivingState = drivingState,
                config = config,
            )

            SpringPhysicsTokensSection(useExpressive = useExpressive)

            ListScrollMotionSection(
                config = config,
                animationsEnabled = animationsEnabled,
            )
            ProgressMotionSection(
                config = config,
                animationsEnabled = animationsEnabled,
            )

            AaosOemPlaybookSection()

            CarScreenScenariosSection(animationsEnabled = animationsEnabled)
            SpringSideBySideSection(animationsEnabled = animationsEnabled)
            InitialVelocityFlingSection(animationsEnabled = animationsEnabled)
            SpatialMotionSection(animationsEnabled = animationsEnabled)
            EffectsMotionSection(animationsEnabled = animationsEnabled)
            SpeedComparisonSection(animationsEnabled = animationsEnabled)
            NavigationTransitionSection(
                drivingState = drivingState,
                animationsEnabled = animationsEnabled,
            )
            PressFeedbackSection(animationsEnabled = animationsEnabled)
            OemMotionTokensSection(drivingState = drivingState)
            AaosMaterial3MotionSection(animationsEnabled = animationsEnabled)
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
private fun OemThemePolicySection(
    drivingState: DrivingUxState,
    config: OemMotionPhysicsConfig,
) {
    val themeScheme = when (drivingState) {
        DrivingUxState.Parked -> "Expressive"
        DrivingUxState.Driving,
        DrivingUxState.Restricted,
        -> "Standard"
    }
    val animCap = RestrictedComponentPolicy.maxAnimationDurationMs(drivingState)

    CustomSectionHeader(
        title = "OEM Theme Policy",
        subtitle = "How OemTheme maps MotionScheme to UXR state",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        GuidanceRow("Parked", "MotionScheme.expressive() — setup wizards, profile pickers, OTA consent")
        GuidanceRow("Driving", "MotionScheme.standard() — glanceable panels, zero-duration cap ($animCap ms)")
        GuidanceRow("Restricted", "MotionScheme.standard() — primary actions only, no decorative motion")
        Text(
            text = "Sidebar preset: ${config.preset.name} " +
                "(live theme: $themeScheme for ${drivingState.name})",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}

@Composable
private fun AaosOemPlaybookSection() {
    CustomSectionHeader(
        title = "AAOS OEM Playbook",
        subtitle = "Where car-screen teams apply each motion pattern",
    )
    Column(
        modifier = Modifier.padding(vertical = OemSpacing.md),
        verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        PlaybookCard(
            title = "Media & Entertainment",
            pattern = "Spatial + Expressive (parked)",
            examples = "Now Playing artwork expand, queue drawer, source picker hero",
        )
        PlaybookCard(
            title = "Climate & Vehicle Controls",
            pattern = "Effects + Standard",
            examples = "Seat/zone highlight, mode chip selection, dual-zone slider tint",
        )
        PlaybookCard(
            title = "Navigation & Maps",
            pattern = "Spatial Fast + Standard",
            examples = "Route card peek, POI detail scale-in, lane guidance banner",
        )
        PlaybookCard(
            title = "Settings & Onboarding",
            pattern = "NavMotion detail + Expressive",
            examples = "Account setup drill-down, Wi-Fi pairing, driver profile creation",
        )
        PlaybookCard(
            title = "Alerts & System UI",
            pattern = "Effects Fast + snap while driving",
            examples = "OTA banner, tire pressure warning, do-not-disturb acknowledgment",
        )
    }
}

@Composable
private fun PlaybookCard(
    title: String,
    pattern: String,
    examples: String,
) {
    CustomCard {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(
            text = pattern,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
        Text(
            text = examples,
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
    }
}

@Composable
private fun CarScreenScenariosSection(animationsEnabled: Boolean) {
    CustomSectionHeader(
        title = "Car Screen Scenarios",
        subtitle = "Interactive AAOS patterns OEMs can ship today",
    )

    MediaNowPlayingScenario(animationsEnabled = animationsEnabled)
    ClimateZoneScenario(animationsEnabled = animationsEnabled)
    AppLauncherTabsScenario(animationsEnabled = animationsEnabled)
}

@Composable
private fun MediaNowPlayingScenario(animationsEnabled: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    val motionScheme = MaterialTheme.motionScheme
    val artworkSize = animateDpWithPolicy(
        targetValue = if (expanded) 120.dp else 64.dp,
        animationSpec = motionScheme.defaultSpatialSpec(),
        animationsEnabled = animationsEnabled,
        label = "media-artwork",
    )

    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.sm)
            .clickable(enabled = animationsEnabled) { expanded = !expanded },
    ) {
        Text(text = "Media — Now Playing", style = MaterialTheme.typography.titleSmall)
        Text(
            text = "Spatial expand for album art while parked; collapse for glanceable driving UI",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
        Row(
            modifier = Modifier.padding(top = OemSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(artworkSize)
                    .clip(OemVisuals.cardShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Column {
                Text(text = "Midnight Drive", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (expanded) "Tap to collapse artwork" else "Tap to expand artwork",
                    style = MaterialTheme.typography.bodySmall,
                    color = OemOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ClimateZoneScenario(animationsEnabled: Boolean) {
    var selectedZone by remember { mutableIntStateOf(0) }
    val zones = listOf("Driver", "Passenger", "Rear")
    val motionScheme = MaterialTheme.motionScheme
    val zoneColor by animateColorAsState(
        targetValue = when (selectedZone) {
            1 -> MaterialTheme.colorScheme.secondaryContainer
            2 -> MaterialTheme.colorScheme.tertiaryContainer
            else -> OemSurfaceElevated
        },
        animationSpec = effectsSpecForPolicy(
            animationSpec = motionScheme.defaultEffectsSpec(),
            animationsEnabled = animationsEnabled,
        ),
        label = "climate-zone",
    )

    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.sm)) {
        Text(text = "Climate — Zone Selection", style = MaterialTheme.typography.titleSmall)
        Text(
            text = "Effects tint communicates active HVAC zone without moving layout",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs, bottom = OemSpacing.sm),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            zones.forEachIndexed { index, zone ->
                CustomChip(
                    label = zone,
                    selected = selectedZone == index,
                    onClick = { if (animationsEnabled) selectedZone = index },
                    enabled = animationsEnabled,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.md)
                .height(72.dp)
                .clip(OemVisuals.cardShape)
                .background(zoneColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${zones[selectedZone]} · 72°F",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun AppLauncherTabsScenario(animationsEnabled: Boolean) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Climate", "Navigation", "Media")
    val tabIcons = listOf(Icons.Default.AcUnit, Icons.Default.Map, Icons.Default.MusicNote)
    val drivingState = currentDrivingUxState()

    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.sm)) {
        Text(text = "App Launcher — Same-Level Tabs", style = MaterialTheme.typography.titleSmall)
        Text(
            text = "NavMotion.sameLevelEnter mirrors horizontal tab switches in OEM launchers",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs, bottom = OemSpacing.sm),
        )
        CustomTabs(
            tabs = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { if (animationsEnabled) selectedTab = it },
        )
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (animationsEnabled) {
                    NavMotion.sameLevelEnter(drivingState) togetherWith NavMotion.sameLevelExit(drivingState)
                } else {
                    fadeIn(snap()) togetherWith fadeOut(snap())
                }
            },
            label = "launcher-tab-content",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.md),
        ) { tabIndex ->
            LauncherTabPreview(
                title = tabs[tabIndex],
                icon = tabIcons[tabIndex],
                subtitle = when (tabIndex) {
                    0 -> "72°F · Auto · Defrost off"
                    1 -> "12 min · I-280 North"
                    else -> "Now playing · Midnight Drive"
                },
            )
        }
    }
}

@Composable
private fun LauncherTabPreview(
    title: String,
    icon: ImageVector,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OemVisuals.cardShape)
            .background(OemSurfaceElevated)
            .padding(OemSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = OemOnSurfaceVariant)
        }
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
private fun NavigationTransitionSection(
    drivingState: DrivingUxState,
    animationsEnabled: Boolean,
) {
    var showDetail by remember { mutableStateOf(false) }

    CustomSectionHeader(
        title = "Screen Transitions",
        subtitle = "NavMotion detail enter/exit for settings drill-downs",
    )
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (showDetail) "Wi-Fi Settings" else "Vehicle Settings",
                style = MaterialTheme.typography.titleSmall,
            )
            CustomButton(
                text = if (showDetail) "Back" else "Open detail",
                onClick = { if (animationsEnabled) showDetail = !showDetail },
                style = ButtonStyle.Tonal,
                enabled = animationsEnabled,
            )
        }
        AnimatedContent(
            targetState = showDetail,
            transitionSpec = {
                if (animationsEnabled) {
                    NavMotion.detailEnter(drivingState) togetherWith NavMotion.detailExit(drivingState)
                } else {
                    fadeIn(snap()) togetherWith fadeOut(snap())
                }
            },
            label = "settings-detail",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.md),
        ) { isDetail ->
            if (isDetail) {
                Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.xs)) {
                    GuidanceRow("Network", "Home_5G · Connected")
                    GuidanceRow("Hotspot", "Off")
                    GuidanceRow("AAOS pattern", "Detail open ${OemMotion.DetailOpenDurationMs}ms")
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.xs)) {
                    GuidanceRow("Display", "Auto brightness")
                    GuidanceRow("Sound", "Surround on")
                    GuidanceRow("Connectivity", "Tap Open detail")
                }
            }
        }
    }
}

@Composable
private fun PressFeedbackSection(animationsEnabled: Boolean) {
    CustomSectionHeader(
        title = "Informative Press Motion",
        subtitle = "OemMotion.pressSpec() — 100ms scale/alpha on in-car controls",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        CustomButton(
            text = "Primary action",
            onClick = {},
            enabled = animationsEnabled,
        )
        CustomButton(
            text = "Secondary",
            onClick = {},
            style = ButtonStyle.Secondary,
            enabled = animationsEnabled,
        )
    }
    Text(
        text = "CustomButton and CustomChip use oemInteractiveMotion instead of ripple — " +
            "recommended for AAOS glanceability.",
        style = MaterialTheme.typography.bodySmall,
        color = OemOnSurfaceVariant,
    )
}

@Composable
private fun OemMotionTokensSection(drivingState: DrivingUxState) {
    CustomSectionHeader(
        title = "OEM Motion Tokens",
        subtitle = "Shared durations in OemMotion — capped by UXR state",
    )
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
        TokenRow("Open", OemMotion.OpenDurationMs, drivingState)
        TokenRow("Close", OemMotion.CloseDurationMs, drivingState)
        TokenRow("Detail open", OemMotion.DetailOpenDurationMs, drivingState)
        TokenRow("Detail close", OemMotion.DetailCloseDurationMs, drivingState)
        TokenRow("Same-level tab", OemMotion.SameLevelDurationMs, drivingState)
        TokenRow("Press feedback", OemMotion.PressDurationMs, drivingState)
        TokenRow("Disruptive enter", OemMotion.DisruptiveEnterMs, drivingState)
        TokenRow("Disruptive exit", OemMotion.DisruptiveExitMs, drivingState)
    }
}

@Composable
private fun TokenRow(label: String, requestedMs: Int, drivingState: DrivingUxState) {
    val effectiveMs = OemMotion.durationMs(drivingState, opening = true, requestedMs)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = if (effectiveMs == requestedMs) "${requestedMs}ms" else "$effectiveMs ms (capped)",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )
    }
}

@Composable
private fun GuidanceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.width(180.dp),
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
    var activeIndex by remember { mutableIntStateOf(0) }
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
        subtitle = "fastSpatialSpec() for banners; slowSpatialSpec() for hero panels",
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
