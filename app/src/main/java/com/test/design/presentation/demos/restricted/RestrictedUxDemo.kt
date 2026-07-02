package com.test.design.presentation.demos.restricted

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomExtendedFab
import com.test.design.component.components.CustomInputChip
import com.test.design.component.components.CustomListTile
import com.test.design.component.components.CustomMetricCard
import com.test.design.component.components.CustomSearchBar
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSegmentedButtonRow
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomTabs
import com.test.design.component.components.CustomTextField
import com.test.design.component.core.DrivingUxState
import com.test.design.component.core.LocalDrivingUxState
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.currentTouchTarget
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemTheme
import com.test.design.component.theme.OemVisuals
import com.test.design.component.tokens.DesignTokens
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun RestrictedUxDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedState = currentDrivingUxState()

    DemoScaffold(
        title = "Driving UX & Restrictions",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = RestrictedComponentPolicy.restrictionSummary(selectedState) + listOf(
                    "Use the global Driving State toggle in the yellow zone",
                    "Google Design for Driving: glanceable, shorter, safer",
                    "4.5:1 contrast minimum at all restriction levels",
                ),
            )
        },
    ) {
        CustomSectionHeader(
            title = "Live restriction preview",
            subtitle = "Components below reflect the global ${selectedState.name} UXR state",
        )
        ScaleReferencePanel(state = selectedState)
        RestrictedComponentPreview(state = selectedState)
    }
}

@Composable
private fun ScaleReferencePanel(state: DrivingUxState) {
    val touchTarget = RestrictedComponentPolicy.touchTarget(state)
    CustomSectionHeader(
        title = "Automotive Scale",
        subtitle = "Enlarged from phone defaults — ${touchTarget.value.toInt()}dp touch target",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
    ) {
        ScaleMetric("Touch", "${touchTarget.value.toInt()}dp", Modifier.weight(1f))
        ScaleMetric("Body", "${DesignTokens.minBodyTextSp}sp", Modifier.weight(1f))
        ScaleMetric("Caption", "${DesignTokens.minCaptionTextSp}sp", Modifier.weight(1f))
        ScaleMetric("Anim", "${RestrictedComponentPolicy.maxAnimationDurationMs(state)}ms", Modifier.weight(1f))
    }
}

@Composable
private fun ScaleMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val numeric = value.filter { it.isDigit() || it == '.' }
    val unit = value.filter { !it.isDigit() && it != '.' }
    CustomMetricCard(label, numeric.ifEmpty { value }, unit, modifier = modifier)
}

@Composable
private fun RestrictedComponentPreview(state: DrivingUxState) {
    OemTheme(drivingUxState = state) {
        CompositionLocalProvider(LocalDrivingUxState provides state) {
            Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.lg)) {
                ButtonsRestrictedSection()
                InputsRestrictedSection()
                ControlsRestrictedSection()
                NavigationRestrictedSection()
                ListsRestrictedSection()
            }
        }
    }
}

@Composable
private fun RestrictionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = OemOnSurfaceVariant,
        modifier = Modifier.padding(bottom = OemSpacing.sm),
    )
}

@Composable
private fun RestrictedPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val touchTarget = currentTouchTarget()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(OemVisuals.cardShape)
            .background(OemSurfaceElevated)
            .border(1.dp, OemBorder, OemVisuals.cardShape)
            .padding(OemSpacing.md),
    ) {
        Column {
            Text(
                text = "Min target ${touchTarget.value.toInt()}dp",
                style = MaterialTheme.typography.labelMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = OemSpacing.sm),
            )
            content()
        }
    }
}

@Composable
private fun ButtonsRestrictedSection() {
    CustomSectionHeader(
        title = "Actions",
        subtitle = "Restricted: primary only — secondary, tonal, text, destructive disabled",
    )
    RestrictedPanel {
        RestrictionLabel("Button styles under current driving state")
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            CustomButton(text = "Primary", onClick = {}, style = ButtonStyle.Primary)
            CustomButton(text = "Tonal", onClick = {}, style = ButtonStyle.Tonal)
            CustomButton(text = "Secondary", onClick = {}, style = ButtonStyle.Secondary)
            CustomButton(text = "Delete", onClick = {}, style = ButtonStyle.Destructive)
        }
    }
}

@Composable
private fun InputsRestrictedSection() {
    var text by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var inputSelected by remember { mutableStateOf(false) }
    val state = currentDrivingUxState()

    CustomSectionHeader(
        title = "Input",
        subtitle = "Keyboard blocked while driving — shows park-to-edit placeholder",
    )
    RestrictedPanel {
        CustomSearchBar(
            query = search,
            onQueryChange = { search = it },
            onSearch = {},
            placeholder = "Search destinations",
            modifier = Modifier.padding(bottom = OemSpacing.md),
        )
        CustomTextField(
            value = text,
            onValueChange = { text = it },
            label = "Destination",
            placeholder = "Enter address",
        )
        if (!RestrictedComponentPolicy.allowsInputChips(state)) {
            Text(
                text = "Input chips hidden while driving",
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.md),
            )
        } else {
            CustomInputChip(
                label = "Eco Mode",
                selected = inputSelected,
                onClick = { inputSelected = !inputSelected },
                modifier = Modifier.padding(top = OemSpacing.md),
            )
        }
    }
}

@Composable
private fun ControlsRestrictedSection() {
    var sliderValue by remember { mutableFloatStateOf(22f) }
    var segmentIndex by remember { mutableIntStateOf(1) }
    val state = currentDrivingUxState()

    CustomSectionHeader(
        title = "Fine Controls",
        subtitle = "Sliders and segmented pickers disabled — value shown glanceably",
    )
    RestrictedPanel {
        CustomSlider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            label = "Temperature °C",
            valueRange = 16f..30f,
        )
        CustomSegmentedButtonRow(
            options = listOf("Off", "Auto", "Max"),
            selectedIndex = segmentIndex,
            onOptionSelected = { segmentIndex = it },
            modifier = Modifier.padding(top = OemSpacing.md),
        )
        if (!RestrictedComponentPolicy.allowsExtendedFab(state)) {
            Text(
                text = "Extended FAB hidden — use compact FAB only",
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.md),
            )
        } else {
            CustomExtendedFab(
                text = "Navigate",
                icon = Icons.Default.Navigation,
                onClick = {},
                modifier = Modifier.padding(top = OemSpacing.md),
            )
        }
    }
}

@Composable
private fun NavigationRestrictedSection() {
    var tabIndex by remember { mutableIntStateOf(0) }
    val state = currentDrivingUxState()
    val allTabs = listOf("Overview", "Details", "Settings", "Diagnostics")
    val visibleTabs = allTabs.take(RestrictedComponentPolicy.maxVisibleTabs(state))

    CustomSectionHeader(
        title = "Navigation",
        subtitle = "Tabs capped at ${visibleTabs.size} — excess tabs removed while driving",
    )
    RestrictedPanel {
        CustomTabs(
            tabs = visibleTabs,
            selectedIndex = tabIndex.coerceIn(visibleTabs.indices),
            onTabSelected = { tabIndex = it },
        )
    }
}

@Composable
private fun ListsRestrictedSection() {
    val state = currentDrivingUxState()
    val destinations = listOf(
        "Home" to "12 min",
        "Work" to "28 min",
        "Airport" to "45 min",
        "Charging station" to "8 min",
    )
    val visible = destinations.take(RestrictedComponentPolicy.maxVisibleListItems(state))

    CustomSectionHeader(
        title = "Lists",
        subtitle = "${visible.size} of ${destinations.size} items — truncated for glanceability",
    )
    RestrictedPanel {
        visible.forEach { (title, subtitle) ->
            CustomListTile(
                title = title,
                subtitle = subtitle,
                leadingIcon = Icons.Default.Navigation,
                onClick = {},
            )
        }
        if (visible.size < destinations.size) {
            Text(
                text = "+${destinations.size - visible.size} more — park to browse full list",
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.sm),
            )
        }
    }
}
