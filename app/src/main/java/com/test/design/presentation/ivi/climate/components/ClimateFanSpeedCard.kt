package com.test.design.presentation.ivi.climate.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ClimateCardActiveRadii
import com.test.design.theme.ClimateCardRestRadii

@Composable
fun ClimateFanSpeedCard(
    fanSpeed: Int,
    maxFanSpeed: Int,
    isAcEnabled: Boolean,
    onSpeedSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    MorphingDetailSurfaceCard(
        morphExpanded = isAcEnabled,
        compactRadii = ClimateCardRestRadii,
        expandedRadii = ClimateCardActiveRadii,
        modifier = modifier.fillMaxWidth(),
    ) {
        if (!compact) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    painter = painterResource(ClimateHvacIcons.Fan),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(CarDesignTokens.TertiaryIcon),
                )
                Text(
                    "Fan speed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        FanSpeedBars(
            fanSpeed = fanSpeed,
            maxFanSpeed = maxFanSpeed,
            onSpeedSelected = onSpeedSelected,
            modifier = Modifier.padding(top = if (compact) 0.dp else 12.dp),
            compact = compact,
        )
    }
}
