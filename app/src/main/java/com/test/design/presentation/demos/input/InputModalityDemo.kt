package com.test.design.presentation.demos.input

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomListTile
import com.test.design.component.components.CustomSearchBar
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomTextField
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.currentTouchTarget
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemVisuals
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun InputModalityDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drivingState = currentDrivingUxState()
    var focusedIndex by remember { mutableIntStateOf(0) }
    val rotaryItems = listOf("Climate", "Navigation", "Media", "Settings", "Phone")

    DemoScaffold(
        title = "Input Modality Lab",
        onBack = onBack,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Touch targets scale with global Driving State (76 → 84 → 88dp)",
                    "Keyboard input is blocked while Driving or Restricted",
                    "Rotary focus order matters for non-touch AAOS hardware",
                ),
            )
        },
    ) {
        CustomSectionHeader(
            title = "Touch Targets",
            subtitle = "Current minimum: ${currentTouchTarget().value.toInt()}dp for ${drivingState.name}",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
        ) {
            TouchTargetSample("Parked", 76)
            TouchTargetSample("Driving", 84)
            TouchTargetSample("Restricted", 88)
        }

        CustomSectionHeader(
            title = "Keyboard & Voice",
            subtitle = RestrictedComponentPolicy.keyboardBlockedMessage(drivingState)
                ?: "Keyboard available while parked",
        )
        CustomTextField(
            value = "",
            onValueChange = {},
            label = "Destination",
            placeholder = "Enter address",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.sm),
        )
        CustomSearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {},
            placeholder = "Search POI",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = OemSpacing.md),
        )

        CustomSectionHeader(
            title = "Rotary Focus Order",
            subtitle = "Simulated focus ring for controller / knob navigation",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            rotaryItems.forEachIndexed { index, label ->
                CustomChip(
                    label = label,
                    selected = focusedIndex == index,
                    onClick = { focusedIndex = index },
                    modifier = Modifier
                        .then(
                            if (focusedIndex == index) {
                                Modifier.border(2.dp, OemBorder, OemVisuals.chipShape)
                            } else {
                                Modifier
                            },
                        ),
                )
            }
        }
        CustomListTile(
            title = "Focus item ${focusedIndex + 1}",
            subtitle = rotaryItems[focusedIndex],
            onClick = { focusedIndex = (focusedIndex + 1) % rotaryItems.size },
        )
    }
}

@Composable
private fun TouchTargetSample(label: String, targetDp: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(targetDp.dp)
                .clip(OemVisuals.iconContainerShape)
                .border(1.dp, OemBorder, OemVisuals.iconContainerShape)
                .focusable(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size((targetDp - 16).coerceAtLeast(44).dp)
                    .clip(OemVisuals.chipShape)
                    .border(1.dp, OemBorder, OemVisuals.chipShape),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
        Text(
            text = "${targetDp}dp",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )
    }
}
