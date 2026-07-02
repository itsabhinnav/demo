package com.test.design.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.core.DrivingUxState
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.currentTouchTarget
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.core.driving.LocalDrivingUxUpdater

@Composable
fun GlobalDrivingUxPanel(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val selectedState = currentDrivingUxState()
    val onUpdate = LocalDrivingUxUpdater.current

    Column(modifier = modifier) {
        CustomSectionHeader(
            title = "Driving State",
            subtitle = if (compact) {
                "App-wide UXR simulation"
            } else {
                "Toggle Parked, Driving, or Restricted to preview behavior on every screen"
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm, bottom = if (compact) OemSpacing.sm else OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            DrivingUxState.entries.forEach { state ->
                CustomChip(
                    label = state.name,
                    selected = selectedState == state,
                    onClick = { onUpdate(state) },
                )
            }
        }
        if (!compact) {
            Text(
                text = "Touch ${currentTouchTarget().value.toInt()}dp · " +
                    "Anim ${RestrictedComponentPolicy.maxAnimationDurationMs(selectedState)}ms · " +
                    RestrictedComponentPolicy.restrictionSummary(selectedState).first(),
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )
        }
    }
}
