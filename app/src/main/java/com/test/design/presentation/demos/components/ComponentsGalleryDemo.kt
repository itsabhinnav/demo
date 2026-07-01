package com.test.design.presentation.demos.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomAssistChip
import com.test.design.component.components.CustomBadge
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomCheckbox
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomCircularProgress
import com.test.design.component.components.CustomDialog
import com.test.design.component.components.CustomEmptyState
import com.test.design.component.components.CustomExtendedFab
import com.test.design.component.components.CustomFab
import com.test.design.component.components.CustomIconButton
import com.test.design.component.components.CustomImage
import com.test.design.component.components.CustomInputChip
import com.test.design.component.components.CustomLinearProgress
import com.test.design.component.components.CustomListTile
import com.test.design.component.components.CustomMetricCard
import com.test.design.component.components.CustomRadioButton
import com.test.design.component.components.CustomSearchBar
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSegmentedButtonRow
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomSnackbarMessage
import com.test.design.component.components.CustomStatusIndicator
import com.test.design.component.components.CustomSuggestionChip
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.CustomTabs
import com.test.design.component.components.CustomTextField
import com.test.design.component.components.FabSize
import com.test.design.component.components.IconButtonStyle
import com.test.design.component.components.StatusLevel
import com.test.design.component.theme.OemSpacing
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.motion.OemMotion
import com.test.design.presentation.demos.playground.ComponentDetailEditor
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun ComponentsGalleryDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingComponentId by remember { mutableStateOf<String?>(null) }
    val drivingState = currentDrivingUxState()

    AnimatedContent(
        targetState = editingComponentId,
        modifier = modifier,
        transitionSpec = {
            val openDuration = OemMotion.durationMs(
                state = drivingState,
                opening = targetState != null,
                requestedMs = if (targetState != null) {
                    OemMotion.DetailOpenDurationMs
                } else {
                    OemMotion.DetailCloseDurationMs
                },
            )
            if (openDuration == 0) {
                fadeIn() togetherWith fadeOut()
            } else {
                val spec = tween<Float>(durationMillis = openDuration, easing = OemMotion.StandardEasing)
                if (targetState != null) {
                    (scaleIn(initialScale = 0.94f, animationSpec = spec) + fadeIn(spec)) togetherWith
                        fadeOut(spec)
                } else {
                    fadeIn(spec) togetherWith
                        (scaleOut(targetScale = 0.94f, animationSpec = spec) + fadeOut(spec))
                }
            }
        },
        label = "galleryDetailEditor",
    ) { componentId ->
        if (componentId != null) {
            ComponentDetailEditor(
                componentId = componentId,
                onBack = { editingComponentId = null },
            )
        } else {
            GalleryContent(onBack = onBack, onCustomize = { editingComponentId = it })
        }
    }
}

@Composable
private fun GalleryContent(
    onBack: () -> Unit,
    onCustomize: (String) -> Unit,
) {
    DemoScaffold(
        title = "Components Gallery",
        onBack = onBack,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Long-press any highlighted component to open the editor",
                    "Motion follows AAOS patterns: fast-out-slow-in easing, short durations",
                    "Customize labels, appearance, and values then tap Save",
                    "76dp minimum touch targets (AAOS, not phone 48dp)",
                    "20sp body text minimum for in-car legibility",
                    "4.5:1 contrast ratio for legibility while driving",
                    "Use color sparingly — white for primary actions",
                ),
            )
        },
    ) {
        ButtonsSection(onCustomize = onCustomize)
        IconButtonsSection(onCustomize = onCustomize)
        FabSection(onCustomize = onCustomize)
        ChipsSection(onCustomize = onCustomize)
        SelectionControlsSection(onCustomize = onCustomize)
        InputsSection(onCustomize = onCustomize)
        ProgressSection(onCustomize = onCustomize)
        MetricsSection(onCustomize = onCustomize)
        ListsSection(onCustomize = onCustomize)
        StatusSection(onCustomize = onCustomize)
        FeedbackSection(onCustomize = onCustomize)
        CardsAndTabsSection(onCustomize = onCustomize)
        ImagesSection(onCustomize = onCustomize)
    }
}

@Composable
private fun GalleryComponentSlot(
    componentId: String,
    onCustomize: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.padding(vertical = OemSpacing.xs),
        contentAlignment = Alignment.TopStart,
    ) {
        content()
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(componentId) {
                    detectTapGestures(
                        onLongPress = { onCustomize(componentId) },
                    )
                },
        )
    }
}

@Composable
private fun ButtonsSection(onCustomize: (String) -> Unit) {
    CustomSectionHeader(title = "Buttons", subtitle = "Rounded monochrome button variants")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        GalleryComponentSlot("button-primary", onCustomize) {
            CustomButton(text = "Primary", onClick = {}, style = ButtonStyle.Primary)
        }
        GalleryComponentSlot("button-tonal", onCustomize) {
            CustomButton(text = "Tonal", onClick = {}, style = ButtonStyle.Tonal)
        }
        GalleryComponentSlot("button-secondary", onCustomize) {
            CustomButton(text = "Secondary", onClick = {}, style = ButtonStyle.Secondary)
        }
        CustomButton(text = "Text", onClick = {}, style = ButtonStyle.Text)
        CustomButton(text = "Delete", onClick = {}, style = ButtonStyle.Destructive)
    }
}

@Composable
private fun IconButtonsSection(onCustomize: (String) -> Unit) {
    CustomSectionHeader(title = "Icon Buttons", subtitle = "Standard, filled, and tonal")
    GalleryComponentSlot("icon-button", onCustomize) {
        Row(
            modifier = Modifier.padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            CustomIconButton(Icons.Default.Settings, "Settings", {}, style = IconButtonStyle.Standard)
            CustomIconButton(Icons.Default.Navigation, "Nav", {}, style = IconButtonStyle.Filled)
            CustomIconButton(Icons.Default.Notifications, "Alerts", {}, style = IconButtonStyle.Tonal)
            CustomBadge(count = 3)
        }
    }
}

@Composable
private fun FabSection(onCustomize: (String) -> Unit) {
    CustomSectionHeader(title = "FABs", subtitle = "Floating action buttons")
    Row(
        modifier = Modifier.padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
    ) {
        GalleryComponentSlot("fab", onCustomize) {
            CustomFab(Icons.Default.Add, "Add", {}, size = FabSize.Standard)
        }
        CustomFab(Icons.Default.Add, "Add large", {}, size = FabSize.Large)
        GalleryComponentSlot("extended-fab", onCustomize) {
            CustomExtendedFab("Navigate", Icons.Default.Navigation, {})
        }
    }
}

@Composable
private fun ChipsSection(onCustomize: (String) -> Unit) {
    var filterIndex by remember { mutableIntStateOf(0) }
    var inputSelected by remember { mutableStateOf(false) }
    val filters = listOf("All", "Climate", "Nav", "Media")

    CustomSectionHeader(title = "Chips", subtitle = "Filter, assist, suggestion, and input chips")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = OemSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        GalleryComponentSlot("filter-chip", onCustomize) {
            filters.forEachIndexed { i, label ->
                CustomChip(label = label, selected = filterIndex == i, onClick = { filterIndex = i })
            }
        }
    }
    Row(
        modifier = Modifier.padding(vertical = OemSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        GalleryComponentSlot("assist-chip", onCustomize) {
            CustomAssistChip("Add stop", {}, leadingIcon = Icons.Default.Add)
        }
        GalleryComponentSlot("suggestion-chip", onCustomize) {
            CustomSuggestionChip("Home", {})
        }
        GalleryComponentSlot("input-chip", onCustomize) {
            CustomInputChip("Eco Mode", inputSelected, { inputSelected = !inputSelected })
        }
    }
}

@Composable
private fun SelectionControlsSection(onCustomize: (String) -> Unit) {
    var switchOn by remember { mutableStateOf(true) }
    var checked by remember { mutableStateOf(false) }
    var radioIndex by remember { mutableIntStateOf(0) }
    var segmentIndex by remember { mutableIntStateOf(0) }

    CustomSectionHeader(title = "Selection", subtitle = "Switch, checkbox, radio, segmented button")
    GalleryComponentSlot("switch", onCustomize) {
        CustomSwitch("Auto climate", switchOn, { switchOn = it })
    }
    GalleryComponentSlot("checkbox", onCustomize) {
        CustomCheckbox("Heated seats", checked, { checked = it })
    }
    GalleryComponentSlot("radio", onCustomize) {
        CustomRadioButton("Standard mode", radioIndex == 0, { radioIndex = 0 })
    }
    CustomRadioButton("Sport mode", radioIndex == 1, { radioIndex = 1 })
    GalleryComponentSlot("segmented-button", onCustomize) {
        CustomSegmentedButtonRow(
            options = listOf("Off", "Auto", "Max"),
            selectedIndex = segmentIndex,
            onOptionSelected = { segmentIndex = it },
        )
    }
}

@Composable
private fun InputsSection(onCustomize: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    CustomSectionHeader(title = "Text Inputs", subtitle = "Outlined fields and search for in-car forms")
    GalleryComponentSlot("search-bar", onCustomize) {
        CustomSearchBar(
            query = search,
            onQueryChange = { search = it },
            onSearch = {},
            placeholder = "Search destinations",
            modifier = Modifier.padding(vertical = OemSpacing.sm),
        )
    }
    GalleryComponentSlot("text-field", onCustomize) {
        CustomTextField(
            value = text,
            onValueChange = { text = it },
            label = "Destination",
            placeholder = "Enter address",
            modifier = Modifier.padding(vertical = OemSpacing.md),
        )
    }
}

@Composable
private fun ProgressSection(onCustomize: (String) -> Unit) {
    var sliderValue by remember { mutableFloatStateOf(22f) }
    CustomSectionHeader(title = "Progress & Sliders", subtitle = "Temperature, volume, loading states")
    GalleryComponentSlot("slider", onCustomize) {
        CustomSlider(value = sliderValue, onValueChange = { sliderValue = it }, label = "Temperature °C", valueRange = 16f..30f)
    }
    GalleryComponentSlot("linear-progress", onCustomize) {
        CustomLinearProgress(progress = { 0.65f }, label = "Battery charge", modifier = Modifier.padding(vertical = OemSpacing.md))
    }
    GalleryComponentSlot("circular-progress", onCustomize) {
        CustomCircularProgress(label = "Syncing…")
    }
}

@Composable
private fun MetricsSection(onCustomize: (String) -> Unit) {
    CustomSectionHeader(title = "Metric Cards", subtitle = "OEM dashboard value displays")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        GalleryComponentSlot("metric-card", onCustomize, modifier = Modifier.weight(1f)) {
            CustomMetricCard("Range", "287", "km", modifier = Modifier.fillMaxWidth())
        }
        CustomMetricCard("Speed", "65", "km/h", modifier = Modifier.weight(1f))
        CustomMetricCard("Efficiency", "6.2", "km/kWh", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ListsSection(onCustomize: (String) -> Unit) {
    CustomSectionHeader(title = "List Tiles", subtitle = "Navigation rows with icons and chevrons")
    GalleryComponentSlot("list-tile", onCustomize) {
        CustomListTile("Vehicle settings", subtitle = "Doors, locks, mirrors", leadingIcon = Icons.Default.Settings, onClick = {})
    }
    CustomListTile("Navigation", subtitle = "Home — 12 min", leadingIcon = Icons.Default.Navigation, onClick = {})
}

@Composable
private fun StatusSection(onCustomize: (String) -> Unit) {
    CustomSectionHeader(title = "Status Indicators", subtitle = "Vehicle and system state")
    Row(
        modifier = Modifier.padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
    ) {
        GalleryComponentSlot("status-indicator", onCustomize) {
            CustomStatusIndicator("Systems OK", StatusLevel.Normal)
        }
        CustomStatusIndicator("Low tire", StatusLevel.Warning)
        CustomStatusIndicator("Brake fault", StatusLevel.Critical)
        CustomStatusIndicator("OTA update", StatusLevel.Info)
    }
}

@Composable
private fun FeedbackSection(onCustomize: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    CustomSectionHeader(title = "Feedback", subtitle = "Dialogs, snackbars, empty states")
    GalleryComponentSlot("dialog-trigger", onCustomize) {
        CustomButton(text = "Show dialog", onClick = { showDialog = true })
        if (showDialog) {
            CustomDialog(
                title = "Enable ProPILOT?",
                message = "Driver assistance will activate on supported roads.",
                confirmText = "Enable",
                dismissText = "Cancel",
                onConfirm = { showDialog = false },
                onDismiss = { showDialog = false },
            )
        }
    }
    GalleryComponentSlot("snackbar", onCustomize) {
        CustomSnackbarMessage(
            message = "Route updated",
            actionLabel = "Undo",
            modifier = Modifier.padding(vertical = OemSpacing.md),
        )
    }
    GalleryComponentSlot("empty-state", onCustomize) {
        CustomEmptyState(
            icon = Icons.Default.Search,
            title = "No results",
            message = "Try a different search term",
        )
    }
}

@Composable
private fun CardsAndTabsSection(onCustomize: (String) -> Unit) {
    var tabIndex by remember { mutableIntStateOf(0) }
    CustomSectionHeader(title = "Cards & Tabs", subtitle = "Content containers and navigation")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        GalleryComponentSlot("card", onCustomize, modifier = Modifier.weight(1f)) {
            CustomCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                Text("Climate", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("22°C", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        CustomCard(modifier = Modifier.weight(1f), onClick = {}) {
            Text("Battery", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("87%", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
    GalleryComponentSlot("tabs", onCustomize) {
        CustomTabs(
            tabs = listOf("Overview", "Details", "Settings"),
            selectedIndex = tabIndex,
            onTabSelected = { tabIndex = it },
            modifier = Modifier.padding(vertical = OemSpacing.md),
        )
    }
}

@Composable
private fun ImagesSection(onCustomize: (String) -> Unit) {
    CustomSectionHeader(title = "Images", subtitle = "Placeholder and vector images")
    Row(
        modifier = Modifier.padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
    ) {
        GalleryComponentSlot("image", onCustomize) {
            CustomImage(contentDescription = "Placeholder", size = OemSpacing.xl * 2)
        }
        CustomImage(contentDescription = "Vehicle", painter = rememberVectorPainter(Icons.Default.DirectionsCar), size = OemSpacing.xl * 2)
    }
}
