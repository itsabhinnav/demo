package com.test.design.presentation.material

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarButtonContentPadding
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTouchTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialComponentsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Material Components",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "AAOS-sized touch targets and typography",
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
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(CarDesignTokens.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            ButtonsSection()
            ChipsSection()
            SelectionControlsSection()
            SliderSection()
            TextFieldsSection()
            CardsSection()
            ProgressSection()
            FabSection()
            SegmentedButtonSection()
            ListItemsSection()
        }
    }
}

@Composable
private fun ComponentSection(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(CarDesignTokens.SectionPadding),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content()
        }
    }
}

@Composable
private fun CarButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.carTouchTarget(),
        contentPadding = CarButtonContentPadding,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ButtonsSection() {
    ComponentSection(
        title = "Buttons",
        description = "Minimum 76×76dp touch targets with centered labels.",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            CarButton(onClick = {}, label = "Filled")
            FilledTonalButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                contentPadding = CarButtonContentPadding,
            ) { Text("Tonal", style = MaterialTheme.typography.labelLarge) }
            ElevatedButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                contentPadding = CarButtonContentPadding,
            ) { Text("Elevated", style = MaterialTheme.typography.labelLarge) }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                contentPadding = CarButtonContentPadding,
            ) { Text("Outlined", style = MaterialTheme.typography.labelLarge) }
            TextButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                contentPadding = CarButtonContentPadding,
            ) { Text("Text", style = MaterialTheme.typography.labelLarge) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
            FilledIconButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            }
            FilledTonalIconButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            }
            OutlinedIconButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                colors = IconButtonDefaults.iconButtonColors(),
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            }
        }
    }
}

@Composable
private fun CarChipLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun ChipsSection() {
    var filterSelected by remember { mutableStateOf(true) }
    val chipModifier = Modifier
        .carTouchTarget()
        .height(CarDesignTokens.MinTouchTarget)

    ComponentSection(
        title = "Chips",
        description = "Assist, filter, input, and suggestion chips at car scale.",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            AssistChip(
                onClick = {},
                modifier = chipModifier,
                label = { CarChipLabel("Assist") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                    )
                },
            )
            FilterChip(
                selected = filterSelected,
                onClick = { filterSelected = !filterSelected },
                modifier = chipModifier,
                label = { CarChipLabel("Filter") },
            )
            InputChip(
                selected = false,
                onClick = {},
                modifier = chipModifier,
                label = { CarChipLabel("Input") },
                trailingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                    )
                },
            )
            SuggestionChip(
                onClick = {},
                modifier = chipModifier,
                label = { CarChipLabel("Suggestion") },
            )
        }
    }
}

@Composable
private fun SelectionControlsSection() {
    var checked by remember { mutableStateOf(true) }
    var switched by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableIntStateOf(0) }

    ComponentSection(
        title = "Selection controls",
        description = "Checkbox, switch, and radio with 76dp row touch areas.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.carTouchTarget(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                    colors = CheckboxDefaults.colors(),
                )
                Text(
                    "Checkbox",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
            Row(
                modifier = Modifier.carTouchTarget(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = switched,
                    onCheckedChange = { switched = it },
                    modifier = Modifier.height(CarDesignTokens.SecondaryIcon),
                    colors = SwitchDefaults.colors(),
                )
                Text(
                    "Switch",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Option A", "Option B", "Option C").forEachIndexed { index, label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .carListItemHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedOption == index,
                        onClick = { selectedOption = index },
                        modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                        colors = RadioButtonDefaults.colors(),
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SliderSection() {
    var sliderValue by remember { mutableFloatStateOf(0.5f) }

    ComponentSection(
        title = "Slider",
        description = "Wide track for glanceable value adjustment.",
    ) {
        Text(
            text = "Value: ${"%.0f".format(sliderValue * 100)}%",
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(CarDesignTokens.MinTouchTarget),
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.size(CarDesignTokens.TouchTargetSpacing),
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(8.dp),
                )
            },
        )
    }
}

@Composable
private fun TextFieldsSection() {
    var filledText by remember { mutableStateOf("Filled text field") }
    var outlinedText by remember { mutableStateOf("") }

    ComponentSection(
        title = "Text fields",
        description = "Filled and outlined inputs with 24sp+ labels.",
    ) {
        TextField(
            value = filledText,
            onValueChange = { filledText = it },
            label = { Text("Filled", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .height(CarDesignTokens.ListItemHeight),
            singleLine = true,
        )
        OutlinedTextField(
            value = outlinedText,
            onValueChange = { outlinedText = it },
            label = { Text("Outlined", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter text…", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .height(CarDesignTokens.ListItemHeight),
            singleLine = true,
        )
    }
}

@Composable
private fun CardsSection() {
    ComponentSection(
        title = "Cards",
        description = "Elevated and filled surfaces with Body 1 text.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing)) {
                    Text("Elevated card", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Raised surface with shadow.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
            ) {
                Column(modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing)) {
                    Text("Filled card", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Surface container color.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressSection() {
    ComponentSection(
        title = "Progress indicators",
        description = "Linear and circular loading at car scale.",
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                strokeWidth = 4.dp,
            )
            BadgedBox(
                badge = {
                    Badge {
                        Text("3", style = MaterialTheme.typography.labelLarge)
                    }
                },
            ) {
                FilledIconButton(
                    onClick = {},
                    modifier = Modifier.carTouchTarget(),
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                    )
                }
            }
        }
    }
}

@Composable
private fun FabSection() {
    ComponentSection(
        title = "Floating action buttons",
        description = "76dp FAB with extended variant for primary actions.",
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.size(CarDesignTokens.MinTouchTarget),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            }
            ExtendedFloatingActionButton(
                onClick = {},
                modifier = Modifier.height(CarDesignTokens.MinTouchTarget),
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                    )
                },
                text = { Text("Extended FAB", style = MaterialTheme.typography.labelLarge) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentedButtonSection() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Day", "Week", "Month")

    ComponentSection(
        title = "Segmented buttons",
        description = "Single-choice row with 76dp segment height.",
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    modifier = Modifier.height(CarDesignTokens.MinTouchTarget),
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun ListItemsSection() {
    ComponentSection(
        title = "List items",
        description = "116dp list rows with 44dp primary icons.",
    ) {
        ListItem(
            modifier = Modifier.carListItemHeight(),
            headlineContent = {
                Text("One-line item", style = MaterialTheme.typography.bodyLarge)
            },
            leadingContent = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            },
            colors = ListItemDefaults.colors(),
        )
        HorizontalDivider()
        ListItem(
            modifier = Modifier.carListItemHeight(),
            headlineContent = {
                Text("Two-line item", style = MaterialTheme.typography.bodyLarge)
            },
            supportingContent = {
                Text(
                    "Supporting text for additional context",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingContent = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            },
            colors = ListItemDefaults.colors(),
        )
        HorizontalDivider()
        ListItem(
            modifier = Modifier.carListItemHeight(),
            overlineContent = {
                Text("Overline", style = MaterialTheme.typography.bodyMedium)
            },
            headlineContent = {
                Text("Three-line item", style = MaterialTheme.typography.bodyLarge)
            },
            supportingContent = {
                Text(
                    "Supporting text with overline label",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingContent = {
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    modifier = Modifier.height(CarDesignTokens.SecondaryIcon),
                )
            },
            colors = ListItemDefaults.colors(),
        )
    }
}
