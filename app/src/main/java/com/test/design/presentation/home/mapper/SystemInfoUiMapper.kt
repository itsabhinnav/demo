package com.test.design.presentation.home.mapper

import com.test.design.presentation.home.model.SystemInfoUiState
import com.test.design.template.adaptive.AutomotiveWindowInfo
import com.test.design.template.adaptive.formatDpForDisplay

fun mapToSystemInfoUiState(
    windowInfo: AutomotiveWindowInfo,
    density: Float,
): SystemInfoUiState {
    return SystemInfoUiState(
        displayLabel = "${windowInfo.profile.label} — ${windowInfo.profile.approximateResolution}",
        widthLabel = formatDpForDisplay(windowInfo.widthDp, density),
        heightLabel = formatDpForDisplay(windowInfo.heightDp, density),
        layoutLabel = formatLayoutSplit(windowInfo.leftColumnWeight, windowInfo.rightColumnWeight),
        blueZoneLabel = formatBlueZoneFraction(windowInfo.blueZoneHeightFraction),
    )
}

private fun formatLayoutSplit(leftWeight: Float, rightWeight: Float): String {
    val leftPercent = (leftWeight * 100).toInt()
    val rightPercent = (rightWeight * 100).toInt()
    return "$leftPercent% / $rightPercent%"
}

private fun formatBlueZoneFraction(fraction: Float): String {
    val percent = (fraction * 100).toInt()
    return "$percent% height"
}
