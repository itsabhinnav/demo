package com.test.design.presentation.material

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarButtonContentPadding
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.OemActionShape
import com.test.design.theme.OemIconContainerShape
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTouchTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OemComponentShowcase(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        OemActionsSection()
        OemTagsSection()
        OemTogglesSection()
        OemSliderSection()
        OemInputsSection()
        OemPanelsSection()
        OemStatusSection()
        OemQuickActionsSection()
        OemTabsSection()
        OemRowsSection()
    }
}

@Composable
private fun OemActionsSection() {
    ComponentSection(
        title = "Actions",
        description = "Primary, secondary, and icon controls with flat OEM styling.",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            Button(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                contentPadding = CarButtonContentPadding,
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("Confirm", style = MaterialTheme.typography.labelLarge)
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                contentPadding = CarButtonContentPadding,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(
                onClick = {},
                modifier = Modifier.carTouchTarget(),
                contentPadding = CarButtonContentPadding,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Text("More", style = MaterialTheme.typography.labelLarge)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
            OemIconAction(Icons.Default.Favorite, "Favorite")
            OemIconAction(Icons.Default.Settings, "Settings")
            OemIconAction(Icons.Default.Add, "Add", highlighted = true)
        }
    }
}

@Composable
private fun OemIconAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    highlighted: Boolean = false,
) {
    val container = if (highlighted) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val tint = if (highlighted) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        modifier = Modifier
            .carTouchTarget()
            .clickable(onClick = {}),
        shape = OemIconContainerShape,
        color = container,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                tint = tint,
            )
        }
    }
}

@Composable
private fun OemTagsSection() {
    var selectedTag by remember { mutableStateOf("Climate") }
    val tags = listOf("Climate", "Navigation", "Media")

    ComponentSection(
        title = "Tags",
        description = "Flat filter tags for glanceable category selection.",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            tags.forEach { tag ->
                val selected = selectedTag == tag
                FilterChip(
                    selected = selected,
                    onClick = { selectedTag = tag },
                    modifier = Modifier.height(CarDesignTokens.MinTouchTarget),
                    label = { Text(tag, style = MaterialTheme.typography.labelLarge) },
                    shape = MaterialTheme.shapes.small,
                    border = if (selected) {
                        null
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
            }
        }
    }
}

@Composable
private fun OemTogglesSection() {
    var checked by remember { mutableStateOf(true) }
    var switched by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableIntStateOf(0) }
    val options = listOf("Eco", "Comfort", "Sport")

    ComponentSection(
        title = "Toggles",
        description = "Checkbox, switch, and radio tuned to the brand palette.",
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
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
                Text(
                    "Remember",
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
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                )
                Text(
                    "Enabled",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEachIndexed { index, label ->
                val selected = selectedOption == index
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .carListItemHeight()
                        .clickable { selectedOption = index },
                    shape = MaterialTheme.shapes.medium,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    } else {
                        Color.Transparent
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = { selectedOption = index },
                            modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.outline,
                            ),
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
}

@Composable
private fun OemSliderSection() {
    var sliderValue by remember { mutableFloatStateOf(0.62f) }

    ComponentSection(
        title = "Level control",
        description = "Thin track with a compact thumb for cabin adjustments.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Brightness", style = MaterialTheme.typography.bodyLarge)
            Text(
                "${"%.0f".format(sliderValue * 100)}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(CarDesignTokens.MinTouchTarget),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier
                        .size(24.dp)
                        .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape),
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(4.dp),
                    drawStopIndicator = null,
                )
            },
        )
    }
}

@Composable
private fun OemInputsSection() {
    var query by remember { mutableStateOf("Home") }
    var note by remember { mutableStateOf("") }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )

    ComponentSection(
        title = "Inputs",
        description = "Low-contrast fields with a single accent on focus.",
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Destination", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .height(CarDesignTokens.ListItemHeight),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = fieldColors,
        )
        TextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Optional message", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .height(CarDesignTokens.ListItemHeight),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@Composable
private fun OemPanelsSection() {
    ComponentSection(
        title = "Panels",
        description = "Flat bordered surfaces instead of elevated Material cards.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            OemPanel(
                modifier = Modifier.weight(1f),
                title = "Trip summary",
                body = "142 km remaining · 1h 48m",
                accent = MaterialTheme.colorScheme.primary,
            )
            OemPanel(
                modifier = Modifier.weight(1f),
                title = "Energy",
                body = "Battery 78% · Regen active",
                accent = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun OemPanel(
    title: String,
    body: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing)) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(3.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(accent),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OemStatusSection() {
    ComponentSection(
        title = "Status",
        description = "Subtle progress and loading indicators.",
    ) {
        LinearProgressIndicator(
            progress = { 0.68f },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(MaterialTheme.shapes.extraSmall),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            Column {
                Text("Syncing profile", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Connected to vehicle cloud",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OemQuickActionsSection() {
    ComponentSection(
        title = "Quick actions",
        description = "Squared action controls suited to wide automotive layouts.",
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = {},
                modifier = Modifier.size(CarDesignTokens.MinTouchTarget),
                shape = OemActionShape,
                color = MaterialTheme.colorScheme.primary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
            Surface(
                onClick = {},
                modifier = Modifier.height(CarDesignTokens.MinTouchTarget),
                shape = OemActionShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                    Text("New route", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun OemTabsSection() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Details", "History")

    ComponentSection(
        title = "Tabs",
        description = "Underline tab bar instead of Material segmented controls.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            tabs.forEachIndexed { index, label ->
                val selected = selectedTab == index
                Column(
                    modifier = Modifier
                        .carTouchTarget()
                        .clickable { selectedTab = index },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(if (selected) 48.dp else 0.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(
                                if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Transparent
                                },
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun OemRowsSection() {
    ComponentSection(
        title = "Rows",
        description = "List rows with icon containers and hairline separators.",
    ) {
        OemRow(
            title = "Driver profile",
            subtitle = "Personal preferences synced",
            icon = Icons.Default.Favorite,
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        OemRow(
            title = "Vehicle settings",
            subtitle = "Suspension · Lighting · Locks",
            icon = Icons.Default.Settings,
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        OemRow(
            title = "Notifications",
            subtitle = "Alerts and reminders",
            icon = Icons.Default.Add,
            trailing = {
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    modifier = Modifier.height(CarDesignTokens.SecondaryIcon),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ),
                )
            },
        )
    }
}

@Composable
private fun OemRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .carListItemHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = OemIconContainerShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.size(52.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        trailing?.invoke()
    }
}
