package com.test.design.presentation.ivi.cluster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.design.core.cluster.ClusterUiState
import com.test.design.presentation.ivi.common.SimulatedBadge

/**
 * Thin cluster companion strip — Display Safety / DriverUI narrative without a real HAR.
 */
@Composable
fun ClusterGlanceStrip(
    cluster: ClusterUiState,
    batteryPercent: Int,
    rangeMiles: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.widthIn(max = 560.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xE6121820),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${cluster.speedMph}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 34.sp,
                ),
                color = Color.White,
            )
            Text(
                text = "MPH",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.55f),
            )
            GearPills(active = cluster.gear)
            Text(
                text = "LIM ${cluster.speedLimitMph}",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFFF5C542),
            )
            Text(
                text = "$batteryPercent% · $rangeMiles mi",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.8f),
            )
            if (cluster.isSimulated) {
                SimulatedBadge()
            }
        }
    }
}

@Composable
private fun GearPills(active: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf("P", "R", "N", "D").forEach { gear ->
            val selected = gear == active
            Text(
                text = gear,
                style = if (selected) {
                    MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                } else {
                    MaterialTheme.typography.titleSmall
                },
                color = if (selected) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.35f)
                },
            )
        }
    }
}
