package com.test.design.presentation.demos.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomCheckbox
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomLinearProgress
import com.test.design.component.components.CustomList
import com.test.design.component.components.CustomListItem
import com.test.design.component.components.CustomListItemRow
import com.test.design.component.components.CustomSegmentedButton
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.CustomTabs
import com.test.design.component.motion.OemMotionPhysicsConfig
import com.test.design.component.motion.progressSpec
import com.test.design.component.motion.rememberOemFlingBehavior
import com.test.design.component.theme.OemSpacing
import kotlinx.coroutines.delay

@Composable
fun MotionPhysicsComponentsSection(
    config: OemMotionPhysicsConfig,
    animationsEnabled: Boolean,
) {
    ButtonsSection(animationsEnabled = animationsEnabled)
    TogglesSection(animationsEnabled = animationsEnabled)
    ChipsSection(animationsEnabled = animationsEnabled)
    SegmentedControlsSection(animationsEnabled = animationsEnabled)
    TabsSection(animationsEnabled = animationsEnabled)
    SliderSection(animationsEnabled = animationsEnabled)
    ListSection(config = config, animationsEnabled = animationsEnabled)
    ProgressSection(config = config, animationsEnabled = animationsEnabled)
    CardsSection(animationsEnabled = animationsEnabled)
}

@Composable
private fun ButtonsSection(animationsEnabled: Boolean) {
    MotionComponentCard(title = "Buttons") {
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            CustomButton(
                text = "Primary",
                onClick = {},
                enabled = animationsEnabled,
            )
            CustomButton(
                text = "Secondary",
                onClick = {},
                style = ButtonStyle.Secondary,
                enabled = animationsEnabled,
            )
            CustomButton(
                text = "Tonal",
                onClick = {},
                style = ButtonStyle.Tonal,
                enabled = animationsEnabled,
            )
        }
    }
}

@Composable
private fun TogglesSection(animationsEnabled: Boolean) {
    var switchOn by remember { mutableStateOf(true) }
    var checkboxOn by remember { mutableStateOf(false) }
    var customSwitchOn by remember { mutableStateOf(false) }
    var customCheckboxOn by remember { mutableStateOf(true) }

    MotionComponentCard(title = "Switch & checkbox") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
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
        CustomSwitch(
            label = "Defrost",
            checked = customSwitchOn,
            onCheckedChange = { if (animationsEnabled) customSwitchOn = it },
            enabled = animationsEnabled,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
        CustomCheckbox(
            label = "Rear climate",
            checked = customCheckboxOn,
            onCheckedChange = { if (animationsEnabled) customCheckboxOn = it },
            enabled = animationsEnabled,
        )
    }
}

@Composable
private fun ChipsSection(animationsEnabled: Boolean) {
    var selectedChip by remember { mutableIntStateOf(0) }
    var filterSelected by remember { mutableStateOf(false) }
    val chips = listOf("Driver", "Passenger", "Rear")

    MotionComponentCard(title = "Chips") {
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            chips.forEachIndexed { index, label ->
                CustomChip(
                    label = label,
                    selected = selectedChip == index,
                    onClick = { if (animationsEnabled) selectedChip = index },
                    enabled = animationsEnabled,
                )
            }
        }
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
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentedControlsSection(animationsEnabled: Boolean) {
    var m3Segment by remember { mutableIntStateOf(1) }
    var customSegment by remember { mutableIntStateOf(0) }

    MotionComponentCard(title = "Segmented buttons") {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf("Eco", "Normal", "Sport").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = m3Segment == index,
                    onClick = { if (animationsEnabled) m3Segment = index },
                    enabled = animationsEnabled,
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                    icon = {},
                    label = { Text(label) },
                )
            }
        }
        CustomSegmentedButton(
            options = listOf("List", "Grid"),
            selectedIndex = customSegment,
            onOptionSelected = { if (animationsEnabled) customSegment = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm),
        )
    }
}

@Composable
private fun TabsSection(animationsEnabled: Boolean) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var navIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Climate", "Navigation", "Media")
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()

    MotionComponentCard(title = "Tabs & navigation") {
        CustomTabs(
            tabs = tabs,
            selectedIndex = tabIndex,
            onTabSelected = { if (animationsEnabled) tabIndex = it },
        )
        AnimatedContent(
            targetState = tabIndex,
            transitionSpec = {
                if (animationsEnabled) {
                    fadeIn(effectsSpec) togetherWith fadeOut(effectsSpec)
                } else {
                    fadeIn(snap()) togetherWith fadeOut(snap())
                }
            },
            label = "tab-preview",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm),
        ) { index ->
            Text(
                text = when (index) {
                    0 -> "72°F · Auto"
                    1 -> "12 min · I-280 North"
                    else -> "Now playing"
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        NavigationBar(modifier = Modifier.padding(top = OemSpacing.sm)) {
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
}

@Composable
private fun SliderSection(animationsEnabled: Boolean) {
    var sliderValue by remember { mutableFloatStateOf(0.4f) }

    MotionComponentCard(title = "Slider") {
        Slider(
            value = sliderValue,
            onValueChange = { if (animationsEnabled) sliderValue = it },
            enabled = animationsEnabled,
        )
    }
}

@Composable
private fun ListSection(
    config: OemMotionPhysicsConfig,
    animationsEnabled: Boolean,
) {
    val flingBehavior = rememberOemFlingBehavior(config, animationsEnabled)
    val listItems = remember {
        (1..16).map { index ->
            CustomListItem(
                id = index.toString(),
                title = "Track $index",
                subtitle = "Fling to feel scroll physics",
            )
        }
    }

    MotionComponentCard(title = "List") {
        CustomList(
            items = listItems,
            key = { it.id },
            scrollable = true,
            flingBehavior = flingBehavior,
            modifier = Modifier.height(180.dp),
            onItemClick = {},
        ) { item ->
            CustomListItemRow(title = item.title, subtitle = item.subtitle)
        }
    }
}

@Composable
private fun ProgressSection(
    config: OemMotionPhysicsConfig,
    animationsEnabled: Boolean,
) {
    var targetProgress by remember { mutableFloatStateOf(0.35f) }
    var replayTick by remember { mutableIntStateOf(0) }
    val progressSpec = config.progressSpec(animationsEnabled)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = progressSpec,
        label = "motion-progress",
    )

    LaunchedEffect(replayTick) {
        if (replayTick == 0) return@LaunchedEffect
        if (!animationsEnabled) {
            targetProgress = 1f
            return@LaunchedEffect
        }
        targetProgress = 0f
        delay(120)
        targetProgress = 1f
    }

    MotionComponentCard(title = "Progress") {
        CustomLinearProgress(
            progress = { animatedProgress },
            label = "Download ${(animatedProgress * 100).toInt()}%",
            modifier = Modifier.fillMaxWidth(),
        )
        CustomButton(
            text = if (replayTick == 0) "Simulate" else "Replay",
            onClick = { replayTick++ },
            style = ButtonStyle.Tonal,
            enabled = animationsEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm),
        )
    }
}

@Composable
private fun CardsSection(animationsEnabled: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    MotionComponentCard(title = "Cards") {
        CustomCard(
            onClick = { if (animationsEnabled) expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Text(
                    text = if (expanded) "Tap to collapse" else "Tap to expand",
                    style = MaterialTheme.typography.titleSmall,
                )
                if (expanded) {
                    Text(
                        text = "CustomCard uses press motion for in-car glanceability.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = OemSpacing.xs),
                    )
                }
            }
        }
    }
}

@Composable
private fun MotionComponentCard(
    title: String,
    content: @Composable () -> Unit,
) {
    CustomCard(modifier = Modifier.padding(vertical = OemSpacing.xs)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Column(
            modifier = Modifier.padding(top = OemSpacing.sm),
            content = { content() },
        )
    }
}
