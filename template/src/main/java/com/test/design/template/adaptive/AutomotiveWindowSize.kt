package com.test.design.template.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AutomotiveDisplayProfile(
    val label: String,
    val approximateResolution: String,
) {
    Compact12_3("12.3\"", "1920×720"),
    Mid14_3("14.3\"", "2240×820"),
    Large15_3("15.3\"", "2560×960"),
    Unknown("Unknown", "—"),
}

data class AutomotiveWindowInfo(
    val widthDp: Dp,
    val heightDp: Dp,
    val profile: AutomotiveDisplayProfile,
    val blueZoneHeightFraction: Float,
    val leftColumnWeight: Float,
    val rightColumnWeight: Float,
)

@Composable
fun rememberAutomotiveWindowInfo(
    maxWidth: Dp,
    maxHeight: Dp,
): AutomotiveWindowInfo {
    val profile = when {
        maxWidth >= 2500.dp -> AutomotiveDisplayProfile.Large15_3
        maxWidth >= 2100.dp -> AutomotiveDisplayProfile.Mid14_3
        maxWidth >= 1800.dp -> AutomotiveDisplayProfile.Compact12_3
        else -> AutomotiveDisplayProfile.Unknown
    }

    val blueZoneFraction = when (profile) {
        AutomotiveDisplayProfile.Mid14_3 -> 0.12f
        AutomotiveDisplayProfile.Large15_3 -> 0.12f
        else -> 0.11f
    }

    val (leftWeight, rightWeight) = when (profile) {
        AutomotiveDisplayProfile.Large15_3 -> 0.75f to 0.25f
        else -> 0.7f to 0.3f
    }

    return AutomotiveWindowInfo(
        widthDp = maxWidth,
        heightDp = maxHeight,
        profile = profile,
        blueZoneHeightFraction = blueZoneFraction,
        leftColumnWeight = leftWeight,
        rightColumnWeight = rightWeight,
    )
}

fun formatDpForDisplay(dp: Dp, density: Float): String {
    val px = (dp.value * density).toInt()
    return "${px}px (${dp.value.toInt()}dp)"
}
