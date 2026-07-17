package com.test.design.presentation.ivi.glanceables

import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.design.presentation.ivi.vehicle.VehicleViewModel

/** Scalable UI `driving_status` TaskPanel — speed / range / gear glanceable. */
class DrivingStatusGlanceActivity : GlanceableActivity() {

    private val vehicleViewModel: VehicleViewModel by viewModels()

    @Composable
    override fun GlanceContent() {
        val vehicleState by vehicleViewModel.state.collectAsStateWithLifecycle()
        GlanceRoot {
            DrivingStatusGlance(
                speedMph = 54,
                speedLimitMph = 60,
                gear = "D",
                batteryPercent = vehicleState.batteryPercent,
                rangeMiles = vehicleState.rangeMiles,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun DrivingStatusGlance(
    speedMph: Int,
    speedLimitMph: Int,
    gear: String,
    batteryPercent: Int,
    rangeMiles: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = GlanceCardBg,
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$speedMph MPH",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 52.sp,
                )
                SpeedLimitBadge(limit = speedLimitMph)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LinearProgressIndicator(
                    progress = { batteryPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = AccentGreen,
                    trackColor = Color.White.copy(alpha = 0.12f),
                )
                Text(
                    text = "$rangeMiles miles",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                listOf("P", "R", "N", "D").forEach { g ->
                    val selected = g == gear
                    Text(
                        text = g,
                        fontSize = 24.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            selected && g == "D" -> AccentRed
                            selected -> AccentGreen
                            else -> Color.White.copy(alpha = 0.3f)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedLimitBadge(limit: Int) {
    Box(
        modifier = Modifier
            .size(58.dp)
            .border(2.5.dp, Color.White.copy(alpha = 0.85f), CircleShape)
            .padding(4.dp)
            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$limit",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp,
            )
            Text(
                text = "MAX",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 10.sp,
            )
        }
    }
}

internal val GlanceCardBg = Color(0xF01C1C1E)
private val AccentGreen = Color(0xFF34C759)
private val AccentRed = Color(0xFFE53935)
