package com.test.design.component.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import com.test.design.component.theme.OemSpacing

object RestrictedComponentPolicy {

    fun allowsKeyboardInput(state: DrivingUxState): Boolean =
        state == DrivingUxState.Parked

    fun allowsFineControls(state: DrivingUxState): Boolean =
        state == DrivingUxState.Parked

    fun allowsDialogs(state: DrivingUxState): Boolean =
        state == DrivingUxState.Parked

    fun allowsSecondaryActions(state: DrivingUxState): Boolean =
        state != DrivingUxState.Restricted

    fun allowsDestructiveActions(state: DrivingUxState): Boolean =
        state == DrivingUxState.Parked

    fun allowsExtendedFab(state: DrivingUxState): Boolean =
        state == DrivingUxState.Parked

    fun allowsInputChips(state: DrivingUxState): Boolean =
        state == DrivingUxState.Parked

    fun maxVisibleListItems(state: DrivingUxState): Int = when (state) {
        DrivingUxState.Parked -> Int.MAX_VALUE
        DrivingUxState.Driving -> 4
        DrivingUxState.Restricted -> 2
    }

    fun maxVisibleTabs(state: DrivingUxState): Int = when (state) {
        DrivingUxState.Parked -> Int.MAX_VALUE
        DrivingUxState.Driving -> 3
        DrivingUxState.Restricted -> 2
    }

    fun maxAnimationDurationMs(state: DrivingUxState): Int = when (state) {
        DrivingUxState.Parked -> 200
        DrivingUxState.Driving -> 0
        DrivingUxState.Restricted -> 0
    }

    fun touchTarget(state: DrivingUxState): Dp = when (state) {
        DrivingUxState.Parked -> OemSpacing.minTouchTarget
        DrivingUxState.Driving -> OemSpacing.drivingTouchTarget
        DrivingUxState.Restricted -> OemSpacing.restrictedTouchTarget
    }

    fun keyboardBlockedMessage(state: DrivingUxState): String? = when (state) {
        DrivingUxState.Parked -> null
        DrivingUxState.Driving -> "Park to enter text"
        DrivingUxState.Restricted -> "Unavailable while driving"
    }

    fun restrictionSummary(state: DrivingUxState): List<String> = when (state) {
        DrivingUxState.Parked -> listOf(
            "76dp touch targets (AAOS baseline)",
            "20sp minimum body text",
            "Full keyboard and dialogs",
        )
        DrivingUxState.Driving -> listOf(
            "84dp touch targets",
            "Keyboard blocked — voice/search presets only",
            "No sliders or fine-grain controls",
            "Lists capped at 4 items",
            "Zero animation duration",
        )
        DrivingUxState.Restricted -> listOf(
            "88dp touch targets (strict UXR)",
            "Primary actions only — no secondary/destructive",
            "Lists capped at 2 glanceable items",
            "Dialogs and input chips blocked",
            "Extended FAB hidden",
        )
    }
}

@Composable
@ReadOnlyComposable
fun currentDrivingUxState(): DrivingUxState = LocalDrivingUxState.current
