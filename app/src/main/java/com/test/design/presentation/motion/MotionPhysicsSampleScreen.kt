package com.test.design.presentation.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.core.motion.LocalEffectiveMotionScheme
import com.test.design.presentation.motion.components.MotionSpringShowcase
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTouchTarget

private enum class MotionTab(val label: String) {
    Springs("Springs"),
    List("List"),
    Scroll("Scroll"),
    Cards("Cards"),
    Controls("Controls"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotionPhysicsSampleScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeScheme = LocalEffectiveMotionScheme.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = MotionTab.entries

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Motion System Lab",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "Scheme: ${activeScheme.label}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                        navigationIcon = {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.carTouchTarget(),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    )
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.height(CarDesignTokens.MinTouchTarget),
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                modifier = Modifier.height(CarDesignTokens.MinTouchTarget),
                                text = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            val motionScheme = MaterialTheme.motionScheme
            AnimatedContent(
                targetState = selectedTabIndex,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                transitionSpec = {
                    fadeIn(animationSpec = motionScheme.defaultEffectsSpec()) togetherWith
                        fadeOut(animationSpec = motionScheme.defaultEffectsSpec())
                },
                label = "motion_tab_content",
            ) { tabIndex ->
                when (tabs[tabIndex]) {
                    MotionTab.Springs -> MotionSpringShowcase()
                    MotionTab.List -> ListTabContent()
                    MotionTab.Scroll -> ScrollTabContent()
                    MotionTab.Cards -> CardsTabContent()
                    MotionTab.Controls -> ControlsTabContent()
                }
            }
        }
}

@Composable
private fun ListTabContent() {
    val items = remember {
        listOf(
            "Spring-damped list transitions" to Icons.Default.MusicNote,
            "Expressive tab indicator motion" to Icons.Default.Favorite,
            "Shared-axis content fades" to Icons.Default.Place,
            "Physics-based chip selection" to Icons.Default.Settings,
            "Container transform cards" to Icons.Default.MusicNote,
            "Emphasized deceleration curves" to Icons.Default.Favorite,
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(CarDesignTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Text(
                text = "List motion",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = CarDesignTokens.TouchTargetSpacing),
            )
        }
        items(items, key = { it.first }) { (title, icon) ->
            ListItem(
                modifier = Modifier.carListItemHeight(),
                headlineContent = {
                    Text(title, style = MaterialTheme.typography.bodyLarge)
                },
                supportingContent = {
                    Text(
                        "Tap rows to feel expressive list physics",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                    )
                },
                colors = ListItemDefaults.colors(),
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun ScrollTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CarDesignTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        Text(
            text = "Scrollable text",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Expressive motion uses spring physics and emphasized easing so UI elements " +
                "decelerate naturally as they settle. Scroll this pane to preview long-form " +
                "content with car-scale typography.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        repeat(6) { index ->
            Text(
                text = buildString {
                    append("Section ${index + 1}. ")
                    append(
                        "Material 3 expressive motion is tuned for glanceable, in-vehicle " +
                            "interfaces. Transitions between tabs, chips, and cards inherit the " +
                            "motion scheme from the theme — springs feel looser and more playful " +
                            "than the standard driving scheme.",
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun CardsTabContent() {
    val cards = remember {
        listOf(
            "Fade through" to "Cross-fade between destinations",
            "Shared axis" to "Horizontal emphasis for lateral navigation",
            "Container transform" to "Morph surfaces into expanded detail",
            "Elevated surface" to "Depth changes with spring overshoot",
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(CarDesignTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        item {
            Text(
                text = "Card transitions",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = CarDesignTokens.TouchTargetSpacing),
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
                items(cards, key = { it.first }) { (title, description) ->
                    Card(
                        modifier = Modifier
                            .fillParentMaxWidth(0.45f)
                            .height(200.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }
        }
        items(cards, key = { "row-${it.first}" }) { (title, description) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                ListItem(
                    modifier = Modifier.carListItemHeight(),
                    headlineContent = {
                        Text(title, style = MaterialTheme.typography.bodyLarge)
                    },
                    supportingContent = {
                        Text(description, style = MaterialTheme.typography.bodyMedium)
                    },
                    colors = ListItemDefaults.colors(),
                )
            }
        }
    }
}

@Composable
private fun ControlsTabContent() {
    var motionBoost by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf("Springs", "Easing", "Duration", "Overshoot")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CarDesignTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        Text(
            text = "Interactive controls",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Toggle chips and switches to observe expressive state animations.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ListItem(
            modifier = Modifier.carListItemHeight(),
            headlineContent = {
                Text("Expressive motion boost", style = MaterialTheme.typography.bodyLarge)
            },
            supportingContent = {
                Text("Amplify spring stiffness on state changes", style = MaterialTheme.typography.bodyMedium)
            },
            trailingContent = {
                Switch(
                    checked = motionBoost,
                    onCheckedChange = { motionBoost = it },
                    modifier = Modifier.height(CarDesignTokens.SecondaryIcon),
                )
            },
            colors = ListItemDefaults.colors(),
        )
        ListItem(
            modifier = Modifier.carListItemHeight(),
            headlineContent = {
                Text("Haptic feedback", style = MaterialTheme.typography.bodyLarge)
            },
            supportingContent = {
                Text("Simulated preference for demo", style = MaterialTheme.typography.bodyMedium)
            },
            trailingContent = {
                Switch(
                    checked = hapticsEnabled,
                    onCheckedChange = { hapticsEnabled = it },
                    modifier = Modifier.height(CarDesignTokens.SecondaryIcon),
                )
            },
            colors = ListItemDefaults.colors(),
        )
        Text(
            text = "Physics presets",
            style = MaterialTheme.typography.titleMedium,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
            items(filters.size) { index ->
                FilterChip(
                    selected = selectedFilter == index,
                    onClick = { selectedFilter = index },
                    modifier = Modifier
                        .carTouchTarget()
                        .height(CarDesignTokens.MinTouchTarget),
                    label = {
                        Text(filters[index], style = MaterialTheme.typography.labelLarge)
                    },
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Active preset: ${filters[selectedFilter]}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "Uses MaterialTheme.motionScheme from home (Standard, Expressive, or Custom). " +
                        "Tab, chip, switch, and content transitions all inherit the active scheme.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
