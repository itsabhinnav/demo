package com.test.design.presentation.demos.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.test.design.component.motion.M3MotionTokens
import com.test.design.component.theme.OemSpacing
import kotlinx.coroutines.delay

enum class TransitionPattern {
    ContainerTransform,
    ForwardBackward,
    Lateral,
    TopLevel,
    EnterExit,
    SkeletonLoaders,
}

fun TransitionPattern.label(): String = when (this) {
    TransitionPattern.ContainerTransform -> "Container transform"
    TransitionPattern.ForwardBackward -> "Forward & backward"
    TransitionPattern.Lateral -> "Lateral"
    TransitionPattern.TopLevel -> "Top level"
    TransitionPattern.EnterExit -> "Enter & exit"
    TransitionPattern.SkeletonLoaders -> "Skeleton loaders"
}

fun TransitionPattern.motionSpec(): String = when (this) {
    TransitionPattern.ContainerTransform ->
        "Enter: Emphasized decelerate · ${M3MotionTokens.EmphasizedDecelerateDurationMs}ms\n" +
            "Exit: Emphasized accelerate · ${M3MotionTokens.EmphasizedAccelerateDurationMs}ms"
    TransitionPattern.ForwardBackward ->
        "Forward enter: Emphasized decelerate · ${M3MotionTokens.EmphasizedDecelerateDurationMs}ms\n" +
            "Back exit: Emphasized accelerate · ${M3MotionTokens.EmphasizedAccelerateDurationMs}ms"
    TransitionPattern.Lateral ->
        "Same-level slide: Standard · ${M3MotionTokens.StandardDurationMs}ms\n" +
            "Reverse: Standard accelerate · ${M3MotionTokens.StandardAccelerateDurationMs}ms"
    TransitionPattern.TopLevel ->
        "Destination change: Emphasized · ${M3MotionTokens.EmphasizedDurationMs}ms\n" +
            "Cross-fade between root sections"
    TransitionPattern.EnterExit ->
        "Panel enter: Emphasized decelerate · ${M3MotionTokens.EmphasizedDecelerateDurationMs}ms\n" +
            "Panel exit: Emphasized accelerate · ${M3MotionTokens.EmphasizedAccelerateDurationMs}ms"
    TransitionPattern.SkeletonLoaders ->
        "Pulse: Standard · 700ms repeat\n" +
            "Reveal: Emphasized decelerate · ${M3MotionTokens.EmphasizedDecelerateDurationMs}ms"
}

@Composable
fun AaosTransitionPatternContent(
    pattern: TransitionPattern,
    replayKey: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(OemSpacing.md),
    ) {
        when (pattern) {
            TransitionPattern.ContainerTransform -> ContainerTransformPattern(replayKey)
            TransitionPattern.ForwardBackward -> ForwardBackwardPattern(replayKey)
            TransitionPattern.Lateral -> LateralPattern(replayKey)
            TransitionPattern.TopLevel -> TopLevelPattern(replayKey)
            TransitionPattern.EnterExit -> EnterExitPattern(replayKey)
            TransitionPattern.SkeletonLoaders -> SkeletonLoaderPattern(replayKey)
        }
    }
}

private data class TripCard(
    val title: String,
    val subtitle: String,
    val color: Color,
    val stops: List<String>,
)

@Composable
private fun ContainerTransformPattern(replayKey: Int) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<TripCard?>(null) }
    val checked = remember { mutableStateListOf<String>() }

    LaunchedEffect(replayKey) {
        expanded = false
        selectedCard = null
    }

    val cards = remember {
        listOf(
            TripCard("Next charging stop", "45 min · 38 mi", Color(0xFFB8E6C8), listOf("Supercharger", "Rest area")),
            TripCard("Trip stops", "3 planned", Color(0xFFD4C4F0), listOf("Coffee", "Groceries", "Restroom", "Fuel")),
            TripCard("Route summary", "I-280 North", Color(0xFFB8D4F0), listOf("Merge", "Exit 12", "Arrive")),
            TripCard("Efficiency", "4.2 mi/kWh", Color(0xFFFFF0A8), listOf("Eco mode on")),
        )
    }

    AnimatedContent(
        targetState = expanded,
        transitionSpec = {
            if (targetState) {
                (fadeIn(M3MotionTokens.emphasizedDecelerateTween()) +
                    slideInHorizontally(M3MotionTokens.emphasizedDecelerateTween()) { it / 5 }) togetherWith
                    (fadeOut(M3MotionTokens.emphasizedAccelerateTween()) +
                        slideOutHorizontally(M3MotionTokens.emphasizedAccelerateTween()) { -it / 8 })
            } else {
                (fadeIn(M3MotionTokens.emphasizedDecelerateTween(300)) +
                    slideInHorizontally(M3MotionTokens.emphasizedDecelerateTween(300)) { -it / 8 }) togetherWith
                    (fadeOut(M3MotionTokens.emphasizedAccelerateTween()) +
                        slideOutHorizontally(M3MotionTokens.emphasizedAccelerateTween()) { it / 5 })
            }
        },
        label = "containerTransform",
    ) { isExpanded ->
        if (!isExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
                Text("Trip planner", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Tap a card — it expands into a full detail surface (container transform).",
                    style = MaterialTheme.typography.bodySmall,
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(cards) { card ->
                        Box(
                            modifier = Modifier
                                .height(120.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(card.color)
                                .clickable {
                                    selectedCard = card
                                    expanded = true
                                }
                                .padding(OemSpacing.md),
                        ) {
                            Column {
                                Text(card.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(card.subtitle, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        } else {
            val card = selectedCard ?: return@AnimatedContent
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                ) {
                    FilledIconButton(onClick = { expanded = false }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(card.title, style = MaterialTheme.typography.titleLarge)
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
                ) {
                    items(card.stops) { stop ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
                        ) {
                            Checkbox(
                                checked = stop in checked,
                                onCheckedChange = { on ->
                                    if (on) checked.add(stop) else checked.remove(stop)
                                },
                            )
                            Text(stop, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

private data class MediaItem(val title: String, val artist: String, val color: Color)

@Composable
private fun ForwardBackwardPattern(replayKey: Int) {
    var showDetail by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<MediaItem?>(null) }

    LaunchedEffect(replayKey) {
        showDetail = false
        selected = null
    }

    val items = remember {
        listOf(
            MediaItem("Highway mix", "Driving playlist", Color(0xFFE8D4F0)),
            MediaItem("Focus drive", "Instrumental", Color(0xFFD4E8F0)),
            MediaItem("Podcast queue", "Tech & design", Color(0xFFF0E8D4)),
            MediaItem("Favorites", "Saved tracks", Color(0xFFD4F0E0)),
        )
    }

    AnimatedContent(
        targetState = showDetail,
        transitionSpec = {
            if (targetState) {
                (fadeIn(M3MotionTokens.emphasizedDecelerateTween()) +
                    slideInHorizontally(M3MotionTokens.emphasizedDecelerateTween()) { it / 4 }) togetherWith
                    fadeOut(M3MotionTokens.emphasizedAccelerateTween())
            } else {
                fadeIn(M3MotionTokens.standardTween(250)) togetherWith
                    (fadeOut(M3MotionTokens.emphasizedAccelerateTween()) +
                        slideOutHorizontally(M3MotionTokens.emphasizedAccelerateTween()) { it / 4 })
            }
        },
        label = "forwardBackward",
    ) { detail ->
        if (!detail) {
            Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
                Text("Media library", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Forward navigation opens slower; back exits faster.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items.take(4).forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(item.color)
                                .clickable {
                                    selected = item
                                    showDetail = true
                                }
                                .padding(OemSpacing.sm),
                            contentAlignment = Alignment.BottomStart,
                        ) {
                            Column {
                                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(item.artist, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        } else {
            val item = selected ?: return@AnimatedContent
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(onClick = { showDetail = false }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(OemSpacing.sm))
                    Text(item.title, style = MaterialTheme.typography.headlineSmall)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(item.color),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(72.dp))
                }
                Text(item.artist, style = MaterialTheme.typography.bodyLarge)
                FilledTonalButton(onClick = { showDetail = false }) {
                    Text("Back to library")
                }
            }
        }
    }
}

@Composable
private fun LateralPattern(replayKey: Int) {
    var zone by remember { mutableIntStateOf(0) }
    val zones = listOf("Driver climate", "Passenger climate")

    LaunchedEffect(replayKey) { zone = 0 }

    Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
        Text("Climate zones", style = MaterialTheme.typography.titleMedium)
        Text(
            "Same-level lateral move — slide between sibling panels.",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            zones.forEachIndexed { index, label ->
                FilledTonalButton(onClick = { zone = index }) {
                    Text(label)
                }
            }
        }
        AnimatedContent(
            targetState = zone,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally(M3MotionTokens.standardTween()) { it / 3 } +
                        fadeIn(M3MotionTokens.standardTween())) togetherWith
                        (slideOutHorizontally(M3MotionTokens.standardTween(M3MotionTokens.StandardAccelerateDurationMs)) { -it / 3 } +
                            fadeOut(M3MotionTokens.standardTween(M3MotionTokens.StandardAccelerateDurationMs)))
                } else {
                    (slideInHorizontally(M3MotionTokens.standardTween()) { -it / 3 } +
                        fadeIn(M3MotionTokens.standardTween())) togetherWith
                        (slideOutHorizontally(M3MotionTokens.standardTween(M3MotionTokens.StandardAccelerateDurationMs)) { it / 3 } +
                            fadeOut(M3MotionTokens.standardTween(M3MotionTokens.StandardAccelerateDurationMs)))
                }
            },
            label = "lateral",
            modifier = Modifier.fillMaxSize(),
        ) { index ->
            ClimateZonePanel(
                title = zones[index],
                temperature = if (index == 0) "72°F" else "68°F",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ClimateZonePanel(title: String, temperature: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(OemSpacing.lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(temperature, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Light)
            Text("Auto · Fan mid · Sync off", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private enum class TopDestination { Home, Navigation, Media, Settings }

@Composable
private fun TopLevelPattern(replayKey: Int) {
    var destination by remember { mutableStateOf(TopDestination.Home) }

    LaunchedEffect(replayKey) { destination = TopDestination.Home }

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(modifier = Modifier.fillMaxHeight()) {
            TopDestination.entries.forEach { dest ->
                val (icon, label) = when (dest) {
                    TopDestination.Home -> Icons.Default.Map to "Home"
                    TopDestination.Navigation -> Icons.Default.Map to "Nav"
                    TopDestination.Media -> Icons.Default.MusicNote to "Media"
                    TopDestination.Settings -> Icons.Default.Settings to "Settings"
                }
                NavigationRailItem(
                    selected = destination == dest,
                    onClick = { destination = dest },
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                )
            }
        }
        AnimatedContent(
            targetState = destination,
            transitionSpec = {
                fadeIn(M3MotionTokens.emphasizedTween()) togetherWith fadeOut(M3MotionTokens.emphasizedTween(350))
            },
            label = "topLevel",
            modifier = Modifier
                .weight(1f)
                .padding(start = OemSpacing.md),
        ) { dest ->
            val (title, body) = when (dest) {
                TopDestination.Home -> "Vehicle home" to "Top-level cross-fade when switching root destinations in the nav rail."
                TopDestination.Navigation -> "Navigation" to "Full-screen root swap — emphasized easing, on-screen begin and end."
                TopDestination.Media -> "Media" to "Distinct from lateral: replaces the entire main stage, not a sibling panel."
                TopDestination.Settings -> "Settings" to "OEM settings and vehicle preferences live at this tier."
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(OemSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
            ) {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                Text(body, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun EnterExitPattern(replayKey: Int) {
    var showPanel by remember { mutableStateOf(false) }

    LaunchedEffect(replayKey) { showPanel = false }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(OemSpacing.md),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            Text("Route briefing", style = MaterialTheme.typography.titleLarge)
            Text(
                "Mindfulness routine for long drives: take breaks every 90 minutes, " +
                    "keep climate steady, and review charging stops before departure.",
                style = MaterialTheme.typography.bodyMedium,
            )
            FilledTonalButton(onClick = { showPanel = !showPanel }) {
                Icon(Icons.Default.Chat, contentDescription = null)
                Spacer(Modifier.width(OemSpacing.sm))
                Text(if (showPanel) "Hide co-pilot notes" else "Show co-pilot notes")
            }
        }
        AnimatedVisibility(
            visible = showPanel,
            enter = expandHorizontally(M3MotionTokens.emphasizedDecelerateTween()) +
                fadeIn(M3MotionTokens.emphasizedDecelerateTween()),
            exit = shrinkHorizontally(M3MotionTokens.emphasizedAccelerateTween()) +
                fadeOut(M3MotionTokens.emphasizedAccelerateTween()),
            modifier = Modifier
                .weight(0.38f)
                .padding(start = OemSpacing.sm),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(OemSpacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Co-pilot notes", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { showPanel = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close panel")
                    }
                }
                Text("Ziad · 2 min ago", style = MaterialTheme.typography.labelMedium)
                Text(
                    "Temporary side sheet — emphasized decelerate on enter, accelerate on permanent dismiss.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = OemSpacing.sm),
                )
            }
        }
    }
}

@Composable
private fun SkeletonLoaderPattern(replayKey: Int) {
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(replayKey) {
        loaded = false
        delay(1800)
        loaded = true
    }

    Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
        Text("Navigation results", style = MaterialTheme.typography.titleMedium)
        Text(
            "Skeleton placeholders pulse while content loads, then reveal with emphasized decelerate.",
            style = MaterialTheme.typography.bodySmall,
        )
        FilledTonalButton(onClick = { loaded = false }) {
            Text("Reload")
        }
        AnimatedContent(
            targetState = loaded,
            transitionSpec = {
                fadeIn(M3MotionTokens.emphasizedDecelerateTween()) togetherWith
                    fadeOut(M3MotionTokens.emphasizedAccelerateTween(160))
            },
            label = "skeletonReveal",
            modifier = Modifier.fillMaxSize(),
        ) { isLoaded ->
            if (!isLoaded) {
                SkeletonResultsList()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
                    items(listOf("Whole Foods · 0.8 mi", "ChargePoint · 2.1 mi", "Rest area · 12 mi")) { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(OemSpacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null)
                            Spacer(Modifier.width(OemSpacing.md))
                            Text(place, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonResultsList() {
    val pulse = rememberInfiniteTransition(label = "skeleton")
    val alpha by pulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = M3MotionTokens.Standard),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            )
        }
    }
}
