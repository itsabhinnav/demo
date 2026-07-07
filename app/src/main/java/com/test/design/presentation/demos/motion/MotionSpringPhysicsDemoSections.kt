package com.test.design.presentation.demos.motion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemSpacing
import kotlinx.coroutines.delay

enum class MotionPhysicsSectionSet {
    All,
    Primary,
    Secondary,
}

@Composable
fun MotionPhysicsComponentsSection(
    animationsEnabled: Boolean,
    sections: MotionPhysicsSectionSet = MotionPhysicsSectionSet.All,
) {
    when (sections) {
        MotionPhysicsSectionSet.All, MotionPhysicsSectionSet.Primary -> {
            ButtonsSection(animationsEnabled = animationsEnabled)
            IconButtonsSection(animationsEnabled = animationsEnabled)
            FabSection(animationsEnabled = animationsEnabled)
            SelectionControlsSection(animationsEnabled = animationsEnabled)
            SliderSection(animationsEnabled = animationsEnabled)
            ChipsSection(animationsEnabled = animationsEnabled)
            SegmentedControlsSection(animationsEnabled = animationsEnabled)
        }
        MotionPhysicsSectionSet.Secondary -> Unit
    }
    when (sections) {
        MotionPhysicsSectionSet.All -> {
            TabsSection(animationsEnabled = animationsEnabled)
            NavigationSection(animationsEnabled = animationsEnabled)
            ProgressSection(animationsEnabled = animationsEnabled)
            CardsSection(animationsEnabled = animationsEnabled)
            BadgeSection(animationsEnabled = animationsEnabled)
            ListSection(animationsEnabled = animationsEnabled)
        }
        MotionPhysicsSectionSet.Primary -> Unit
        MotionPhysicsSectionSet.Secondary -> {
            TabsSection(animationsEnabled = animationsEnabled)
            NavigationSection(animationsEnabled = animationsEnabled)
            ProgressSection(animationsEnabled = animationsEnabled)
            CardsSection(animationsEnabled = animationsEnabled)
            BadgeSection(animationsEnabled = animationsEnabled)
            ListSection(animationsEnabled = animationsEnabled)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ButtonsSection(animationsEnabled: Boolean) {
    MotionComponentCard(title = "Buttons") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            Button(onClick = {}, enabled = animationsEnabled) { Text("Filled") }
            FilledTonalButton(onClick = {}, enabled = animationsEnabled) { Text("Tonal") }
            ElevatedButton(onClick = {}, enabled = animationsEnabled) { Text("Elevated") }
            OutlinedButton(onClick = {}, enabled = animationsEnabled) { Text("Outlined") }
            TextButton(onClick = {}, enabled = animationsEnabled) { Text("Text") }
        }
    }
}

@Composable
private fun IconButtonsSection(animationsEnabled: Boolean) {
    MotionComponentCard(title = "Icon buttons") {
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            IconButton(onClick = {}, enabled = animationsEnabled) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
            FilledIconButton(onClick = {}, enabled = animationsEnabled) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite")
            }
            FilledTonalIconButton(onClick = {}, enabled = animationsEnabled) {
                Icon(Icons.Default.MusicNote, contentDescription = "Media")
            }
            OutlinedIconButton(onClick = {}, enabled = animationsEnabled) {
                Icon(Icons.Default.Map, contentDescription = "Map")
            }
        }
    }
}

@Composable
private fun FabSection(animationsEnabled: Boolean) {
    MotionComponentCard(title = "FAB") {
        Row(
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloatingActionButton(onClick = { if (animationsEnabled) { } }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            ExtendedFloatingActionButton(
                text = { Text("Compose") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = { if (animationsEnabled) { } },
            )
        }
    }
}

@Composable
private fun SelectionControlsSection(animationsEnabled: Boolean) {
    var switchOn by remember { mutableStateOf(true) }
    var checkboxOn by remember { mutableStateOf(false) }
    var triState by remember { mutableStateOf(ToggleableState.Indeterminate) }
    var selectedRadio by remember { mutableIntStateOf(0) }

    MotionComponentCard(title = "Switch, checkbox & radio") {
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
            TriStateCheckbox(
                state = triState,
                onClick = {
                    if (!animationsEnabled) return@TriStateCheckbox
                    triState = when (triState) {
                        ToggleableState.On -> ToggleableState.Off
                        ToggleableState.Off -> ToggleableState.Indeterminate
                        ToggleableState.Indeterminate -> ToggleableState.On
                    }
                },
                enabled = animationsEnabled,
            )
        }
        Row(
            modifier = Modifier.padding(top = OemSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            listOf("Driver", "Passenger").forEachIndexed { index, label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRadio == index,
                        onClick = { if (animationsEnabled) selectedRadio = index },
                        enabled = animationsEnabled,
                    )
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SliderSection(animationsEnabled: Boolean) {
    var sliderValue by remember { mutableFloatStateOf(0.4f) }
    var range by remember { mutableStateOf(0.2f..0.8f) }

    MotionComponentCard(title = "Slider") {
        Slider(
            value = sliderValue,
            onValueChange = { if (animationsEnabled) sliderValue = it },
            enabled = animationsEnabled,
        )
        RangeSlider(
            value = range,
            onValueChange = { if (animationsEnabled) range = it },
            enabled = animationsEnabled,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipsSection(animationsEnabled: Boolean) {
    var filterSelected by remember { mutableStateOf(false) }
    var inputSelected by remember { mutableStateOf(true) }

    MotionComponentCard(title = "Chips") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            AssistChip(
                onClick = {},
                enabled = animationsEnabled,
                label = { Text("Assist") },
                leadingIcon = {
                    Icon(Icons.Default.AcUnit, contentDescription = null, modifier = Modifier.size(18.dp))
                },
            )
            FilterChip(
                selected = filterSelected,
                onClick = { if (animationsEnabled) filterSelected = !filterSelected },
                enabled = animationsEnabled,
                label = { Text("Filter") },
                leadingIcon = if (filterSelected) {
                    { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else {
                    { Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, modifier = Modifier.size(18.dp)) }
                },
            )
            InputChip(
                selected = inputSelected,
                onClick = { if (animationsEnabled) inputSelected = !inputSelected },
                enabled = animationsEnabled,
                label = { Text("Input") },
                leadingIcon = {
                    Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp))
                },
            )
            SuggestionChip(
                onClick = {},
                enabled = animationsEnabled,
                label = { Text("Suggestion") },
                icon = {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentedControlsSection(animationsEnabled: Boolean) {
    var singleChoice by remember { mutableIntStateOf(1) }
    val multiChoice = remember { mutableStateListOf(0) }

    MotionComponentCard(title = "Segmented buttons") {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf("Eco", "Normal", "Sport").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = singleChoice == index,
                    onClick = { if (animationsEnabled) singleChoice = index },
                    enabled = animationsEnabled,
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                    icon = {},
                    label = { Text(label) },
                )
            }
        }
        MultiChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm),
        ) {
            listOf("Heat", "Cool", "Fan").forEachIndexed { index, label ->
                SegmentedButton(
                    checked = index in multiChoice,
                    onCheckedChange = { checked ->
                        if (!animationsEnabled) return@SegmentedButton
                        if (checked) multiChoice.add(index) else multiChoice.remove(index)
                    },
                    enabled = animationsEnabled,
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                    icon = {},
                    label = { Text(label) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabsSection(animationsEnabled: Boolean) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Climate", "Navigation", "Media")

    MotionComponentCard(title = "Tabs") {
        PrimaryTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { if (animationsEnabled) tabIndex = index },
                    enabled = animationsEnabled,
                    text = { Text(title) },
                )
            }
        }
        Text(
            text = when (tabIndex) {
                0 -> "72°F · Auto"
                1 -> "12 min · I-280 North"
                else -> "Now playing"
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}

@Composable
private fun NavigationSection(animationsEnabled: Boolean) {
    var navBarIndex by remember { mutableIntStateOf(0) }
    var railIndex by remember { mutableIntStateOf(0) }

    MotionComponentCard(title = "Navigation bar & rail") {
        NavigationBar {
            val items = listOf(
                Triple("Climate", Icons.Default.AcUnit, 0),
                Triple("Nav", Icons.Default.Map, 1),
                Triple("Media", Icons.Default.MusicNote, 2),
                Triple("Settings", Icons.Default.Settings, 3),
            )
            items.forEach { (label, icon, index) ->
                NavigationBarItem(
                    selected = navBarIndex == index,
                    onClick = { if (animationsEnabled) navBarIndex = index },
                    enabled = animationsEnabled,
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm),
        ) {
            NavigationRail {
                val railItems = listOf(
                    Triple("Climate", Icons.Default.AcUnit, 0),
                    Triple("Media", Icons.Default.MusicNote, 1),
                    Triple("Settings", Icons.Default.Settings, 2),
                )
                railItems.forEach { (label, icon, index) ->
                    NavigationRailItem(
                        selected = railIndex == index,
                        onClick = { if (animationsEnabled) railIndex = index },
                        enabled = animationsEnabled,
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressSection(animationsEnabled: Boolean) {
    var linearProgress by remember { mutableFloatStateOf(0.35f) }
    var circularProgress by remember { mutableFloatStateOf(0.6f) }
    var replayTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(replayTick) {
        if (replayTick == 0) return@LaunchedEffect
        if (!animationsEnabled) {
            linearProgress = 1f
            circularProgress = 1f
            return@LaunchedEffect
        }
        linearProgress = 0f
        circularProgress = 0f
        delay(120)
        linearProgress = 1f
        circularProgress = 1f
    }

    MotionComponentCard(title = "Progress indicators") {
        LinearProgressIndicator(
            progress = { linearProgress },
            modifier = Modifier.fillMaxWidth(),
        )
        CircularProgressIndicator(
            progress = { circularProgress },
            modifier = Modifier
                .padding(top = OemSpacing.md)
                .size(48.dp),
        )
        FilledTonalButton(
            onClick = { replayTick++ },
            enabled = animationsEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm),
        ) {
            Text(if (replayTick == 0) "Simulate" else "Replay")
        }
    }
}

@Composable
private fun CardsSection(animationsEnabled: Boolean) {
    var elevatedSelected by remember { mutableStateOf(false) }

    MotionComponentCard(title = "Cards") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            Card(
                onClick = {},
                enabled = animationsEnabled,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Filled",
                    modifier = Modifier.padding(OemSpacing.md),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            ElevatedCard(
                onClick = { if (animationsEnabled) elevatedSelected = !elevatedSelected },
                enabled = animationsEnabled,
                modifier = Modifier.weight(1f),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (elevatedSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        CardDefaults.elevatedCardColors().containerColor
                    },
                ),
            ) {
                Text(
                    text = if (elevatedSelected) "Elevated · on" else "Elevated",
                    modifier = Modifier.padding(OemSpacing.md),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            OutlinedCard(
                onClick = {},
                enabled = animationsEnabled,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Outlined",
                    modifier = Modifier.padding(OemSpacing.md),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@Composable
private fun BadgeSection(animationsEnabled: Boolean) {
    var showBadge by remember { mutableStateOf(true) }

    MotionComponentCard(title = "Badge") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
        ) {
            BadgedBox(
                badge = {
                    if (showBadge) {
                        Badge { Text("3") }
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(32.dp),
                )
            }
            FilledTonalButton(
                onClick = { if (animationsEnabled) showBadge = !showBadge },
                enabled = animationsEnabled,
            ) {
                Text(if (showBadge) "Hide badge" else "Show badge")
            }
        }
    }
}

@Composable
private fun ListSection(animationsEnabled: Boolean) {
    val tracks = remember {
        (1..8).map { index -> "Track $index" to "Album · Artist" }
    }

    MotionComponentCard(title = "List items") {
        LazyColumn(
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
        ) {
            items(tracks) { (title, subtitle) ->
                ListItem(
                    headlineContent = { Text(title) },
                    supportingContent = { Text(subtitle) },
                    leadingContent = {
                        Icon(Icons.Default.MusicNote, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun MotionComponentCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs),
    ) {
        Column(modifier = Modifier.padding(OemSpacing.md)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Column(
                modifier = Modifier.padding(top = OemSpacing.sm),
                content = { content() },
            )
        }
    }
}
