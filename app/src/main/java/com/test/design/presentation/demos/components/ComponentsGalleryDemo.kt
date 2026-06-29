package com.test.design.presentation.demos.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.test.design.component.theme.NissanSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun ComponentsGalleryDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Components Gallery",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "48dp minimum touch targets on all controls",
                    "4.5:1 contrast ratio for legibility while driving",
                    "Use color sparingly — Nissan Red for primary actions",
                    "Limit animations; prefer instant state changes",
                    "One primary action per screen zone",
                ),
            )
        },
    ) {
        ButtonsSection()
        IconButtonsSection()
        FabSection()
        ChipsSection()
        SelectionControlsSection()
        InputsSection()
        ProgressSection()
        MetricsSection()
        ListsSection()
        StatusSection()
        FeedbackSection()
        CardsAndTabsSection()
        ImagesSection()
    }
}

@Composable
private fun ButtonsSection() {
    CustomSectionHeader(title = "Buttons", subtitle = "Material 3 button variants — OEM styled")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.sm),
    ) {
        CustomButton(text = "Primary", onClick = {}, style = ButtonStyle.Primary)
        CustomButton(text = "Tonal", onClick = {}, style = ButtonStyle.Tonal)
        CustomButton(text = "Secondary", onClick = {}, style = ButtonStyle.Secondary)
        CustomButton(text = "Text", onClick = {}, style = ButtonStyle.Text)
        CustomButton(text = "Delete", onClick = {}, style = ButtonStyle.Destructive)
    }
}

@Composable
private fun IconButtonsSection() {
    CustomSectionHeader(title = "Icon Buttons", subtitle = "Standard, filled, and tonal")
    Row(
        modifier = Modifier.padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.md),
    ) {
        CustomIconButton(Icons.Default.Settings, "Settings", {}, style = IconButtonStyle.Standard)
        CustomIconButton(Icons.Default.Navigation, "Nav", {}, style = IconButtonStyle.Filled)
        CustomIconButton(Icons.Default.Notifications, "Alerts", {}, style = IconButtonStyle.Tonal)
        CustomBadge(count = 3)
    }
}

@Composable
private fun FabSection() {
    CustomSectionHeader(title = "FABs", subtitle = "Floating action buttons")
    Row(
        modifier = Modifier.padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.lg),
    ) {
        CustomFab(Icons.Default.Add, "Add", {}, size = FabSize.Standard)
        CustomFab(Icons.Default.Add, "Add large", {}, size = FabSize.Large)
        CustomExtendedFab("Navigate", Icons.Default.Navigation, {})
    }
}

@Composable
private fun ChipsSection() {
    var filterIndex by remember { mutableIntStateOf(0) }
    var inputSelected by remember { mutableStateOf(false) }
    val filters = listOf("All", "Climate", "Nav", "Media")

    CustomSectionHeader(title = "Chips", subtitle = "Filter, assist, suggestion, and input chips")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = NissanSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.sm),
    ) {
        filters.forEachIndexed { i, label ->
            CustomChip(label = label, selected = filterIndex == i, onClick = { filterIndex = i })
        }
    }
    Row(
        modifier = Modifier.padding(vertical = NissanSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.sm),
    ) {
        CustomAssistChip("Add stop", {}, leadingIcon = Icons.Default.Add)
        CustomSuggestionChip("Home", {})
        CustomInputChip("Eco Mode", inputSelected, { inputSelected = !inputSelected })
    }
}

@Composable
private fun SelectionControlsSection() {
    var switchOn by remember { mutableStateOf(true) }
    var checked by remember { mutableStateOf(false) }
    var radioIndex by remember { mutableIntStateOf(0) }
    var segmentIndex by remember { mutableIntStateOf(0) }

    CustomSectionHeader(title = "Selection", subtitle = "Switch, checkbox, radio, segmented button")
    CustomSwitch("Auto climate", switchOn, { switchOn = it })
    CustomCheckbox("Heated seats", checked, { checked = it })
    CustomRadioButton("Standard mode", radioIndex == 0, { radioIndex = 0 })
    CustomRadioButton("Sport mode", radioIndex == 1, { radioIndex = 1 })
    CustomSegmentedButtonRow(
        options = listOf("Off", "Auto", "Max"),
        selectedIndex = segmentIndex,
        onOptionSelected = { segmentIndex = it },
    )
}

@Composable
private fun InputsSection() {
    var text by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    CustomSectionHeader(title = "Text Inputs", subtitle = "Outlined fields and search for in-car forms")
    CustomSearchBar(
        query = search,
        onQueryChange = { search = it },
        onSearch = {},
        placeholder = "Search destinations",
        modifier = Modifier.padding(vertical = NissanSpacing.sm),
    )
    CustomTextField(
        value = text,
        onValueChange = { text = it },
        label = "Destination",
        placeholder = "Enter address",
        modifier = Modifier.padding(vertical = NissanSpacing.md),
    )
}

@Composable
private fun ProgressSection() {
    var sliderValue by remember { mutableFloatStateOf(22f) }
    CustomSectionHeader(title = "Progress & Sliders", subtitle = "Temperature, volume, loading states")
    CustomSlider(value = sliderValue, onValueChange = { sliderValue = it }, label = "Temperature °C", valueRange = 16f..30f)
    CustomLinearProgress(progress = { 0.65f }, label = "Battery charge", modifier = Modifier.padding(vertical = NissanSpacing.md))
    CustomCircularProgress(label = "Syncing…")
}

@Composable
private fun MetricsSection() {
    CustomSectionHeader(title = "Metric Cards", subtitle = "OEM dashboard value displays")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.md),
    ) {
        CustomMetricCard("Range", "287", "km", modifier = Modifier.weight(1f))
        CustomMetricCard("Speed", "65", "km/h", modifier = Modifier.weight(1f))
        CustomMetricCard("Efficiency", "6.2", "km/kWh", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ListsSection() {
    CustomSectionHeader(title = "List Tiles", subtitle = "Navigation rows with icons and chevrons")
    CustomListTile("Vehicle settings", subtitle = "Doors, locks, mirrors", leadingIcon = Icons.Default.Settings, onClick = {})
    CustomListTile("Navigation", subtitle = "Home — 12 min", leadingIcon = Icons.Default.Navigation, onClick = {})
}

@Composable
private fun StatusSection() {
    CustomSectionHeader(title = "Status Indicators", subtitle = "Vehicle and system state")
    Row(
        modifier = Modifier.padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.lg),
    ) {
        CustomStatusIndicator("Systems OK", StatusLevel.Normal)
        CustomStatusIndicator("Low tire", StatusLevel.Warning)
        CustomStatusIndicator("Brake fault", StatusLevel.Critical)
        CustomStatusIndicator("OTA update", StatusLevel.Info)
    }
}

@Composable
private fun FeedbackSection() {
    var showDialog by remember { mutableStateOf(false) }
    CustomSectionHeader(title = "Feedback", subtitle = "Dialogs, snackbars, empty states")
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
    CustomSnackbarMessage(
        message = "Route updated",
        actionLabel = "Undo",
        modifier = Modifier.padding(vertical = NissanSpacing.md),
    )
    CustomEmptyState(
        icon = Icons.Default.Search,
        title = "No results",
        message = "Try a different search term",
    )
}

@Composable
private fun CardsAndTabsSection() {
    var tabIndex by remember { mutableIntStateOf(0) }
    CustomSectionHeader(title = "Cards & Tabs", subtitle = "Content containers and navigation")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.md),
    ) {
        CustomCard(modifier = Modifier.weight(1f), onClick = {}) {
            androidx.compose.material3.Text("Climate", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            androidx.compose.material3.Text("22°C", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        }
        CustomCard(modifier = Modifier.weight(1f), onClick = {}) {
            androidx.compose.material3.Text("Battery", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            androidx.compose.material3.Text("87%", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        }
    }
    CustomTabs(
        tabs = listOf("Overview", "Details", "Settings"),
        selectedIndex = tabIndex,
        onTabSelected = { tabIndex = it },
        modifier = Modifier.padding(vertical = NissanSpacing.md),
    )
}

@Composable
private fun ImagesSection() {
    CustomSectionHeader(title = "Images", subtitle = "Placeholder and vector images")
    Row(
        modifier = Modifier.padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.lg),
    ) {
        CustomImage(contentDescription = "Placeholder", size = NissanSpacing.xl * 2)
        CustomImage(contentDescription = "Vehicle", painter = rememberVectorPainter(Icons.Default.DirectionsCar), size = NissanSpacing.xl * 2)
    }
}
