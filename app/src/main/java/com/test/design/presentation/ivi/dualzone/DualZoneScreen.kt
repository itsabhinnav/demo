package com.test.design.presentation.ivi.dualzone

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.test.design.core.DrivingUxState
import com.test.design.core.LocalDrivingUxState
import com.test.design.core.cluster.ClusterUiState
import com.test.design.presentation.ivi.common.SimulatedBadge
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.media.MediaEvent
import com.test.design.presentation.ivi.media.MediaUiState
import com.test.design.presentation.ivi.media.components.MediaTransportControlsBar
import com.test.design.presentation.ivi.navigation.NavigationUiState
import com.test.design.theme.CarDesignTokens

/**
 * Driver vs passenger dual-zone mock — MUMD story without multi-display APIs.
 * Driver pane is Restricted (glanceables only); passenger pane runs Parked motion / full media.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DualZoneScreen(
    mediaState: MediaUiState,
    onMediaEvent: (MediaEvent) -> Unit,
    navigationState: NavigationUiState,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val cluster = ClusterUiState.fromDrivingUx(DrivingUxState.Restricted)

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                widgetContainerTransform(
                    widget = DashboardWidget.DualZone,
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            )
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0E141C), Color(0xFF1A1520)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CarDesignTokens.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            WidgetScreenHeader(
                widget = DashboardWidget.DualZone,
                onBack = onBack,
                animatedVisibilityScope = animatedVisibilityScope,
                trailingContent = { SimulatedBadge() },
            )

            Text(
                text = "Multi-user multi-display (MUMD) mock — driver distraction policy vs passenger freedom",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CompositionLocalProvider(
                    LocalDrivingUxState provides DrivingUxState.Restricted,
                ) {
                    ZonePane(
                        title = "Driver",
                        subtitle = "Restricted · Standard motion · glanceables",
                        accent = Color(0xFF5B9CFF),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        DriverRestrictedContent(
                            cluster = cluster,
                            navigationState = navigationState,
                            mediaState = mediaState,
                        )
                    }
                }
                CompositionLocalProvider(
                    LocalDrivingUxState provides DrivingUxState.Parked,
                ) {
                    ZonePane(
                        title = "Passenger",
                        subtitle = "Parked motion · full media controls",
                        accent = Color(0xFFE8B86D),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        PassengerFullContent(
                            mediaState = mediaState,
                            onMediaEvent = onMediaEvent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ZonePane(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xE6141C26),
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(accent, RoundedCornerShape(50)),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun DriverRestrictedContent(
    cluster: ClusterUiState,
    navigationState: NavigationUiState,
    mediaState: MediaUiState,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GlanceRow(
            icon = Icons.Default.Speed,
            title = "${cluster.speedMph} MPH · ${cluster.gear}",
            body = "Cluster glance · limit ${cluster.speedLimitMph}",
        )
        GlanceRow(
            icon = Icons.Default.Person,
            title = navigationState.currentInstruction,
            body = "${navigationState.distanceRemaining} · ${navigationState.etaMinutes} min",
        )
        GlanceRow(
            icon = Icons.Default.MusicNote,
            title = mediaState.currentTrack.title,
            body = if (mediaState.isPlaying) "Playing" else "Paused",
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0x33FF8A65),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFFFAB91),
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Deep apps & expressive motion locked while driving",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun PassengerFullContent(
    mediaState: MediaUiState,
    onMediaEvent: (MediaEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Now playing",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.55f),
            )
            Text(
                text = mediaState.currentTrack.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Text(
                text = mediaState.currentTrack.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
            )
            Text(
                text = "Browse, queue, and expressive motion stay available on the passenger seat.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.55f),
            )
        }
        MediaTransportControlsBar(
            isPlaying = mediaState.isPlaying,
            onToggleQueue = { },
            onPrevious = { onMediaEvent(MediaEvent.PreviousTrack) },
            onTogglePlayback = { onMediaEvent(MediaEvent.TogglePlayback) },
            onNext = { onMediaEvent(MediaEvent.NextTrack) },
            showQueue = false,
        )
    }
}

@Composable
private fun GlanceRow(
    icon: ImageVector,
    title: String,
    body: String,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.06f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF9EC5FF),
                modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 2,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                )
            }
        }
    }
}
