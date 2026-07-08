package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetPreviewSharedElement
import com.test.design.theme.rememberClimateDialShape
import com.test.design.theme.rememberMediaAlbumShape
import com.test.design.theme.rememberVehicleGaugeShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MediaWidgetPreview(
    album: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    playing: Boolean,
    modifier: Modifier = Modifier,
) {
    val albumShape = rememberMediaAlbumShape(playing = playing)
    Box(
        modifier = widgetPreviewSharedElement(
            widget = DashboardWidget.Media,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
                .fillMaxWidth()
                .clip(albumShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    ),
                ),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = album,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClimateWidgetPreview(
    temperature: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    acEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val dialShape = rememberClimateDialShape(acEnabled = acEnabled)
    Box(
        modifier = widgetPreviewSharedElement(
            widget = DashboardWidget.Climate,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
                .fillMaxWidth()
                .clip(dialShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$temperature°",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = if (acEnabled) "A/C" else "Off",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.NavigationWidgetPreview(
    destination: String,
    etaMinutes: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = widgetPreviewSharedElement(
            widget = DashboardWidget.Navigation,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1B5E20),
                            Color(0xFF2E7D32),
                            Color(0xFF66BB6A),
                        ),
                    ),
                ),
        ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = androidx.compose.ui.geometry.Offset(size.width * 0.12f, size.height * 0.72f),
                end = androidx.compose.ui.geometry.Offset(size.width * 0.88f, size.height * 0.28f),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom,
        ) {
            Text(
                text = destination,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$etaMinutes min",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.VehicleWidgetPreview(
    batteryPercent: Int,
    rangeMiles: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sportMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val gaugeShape = rememberVehicleGaugeShape(sportMode = sportMode)
    val arcColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = widgetPreviewSharedElement(
            widget = DashboardWidget.Vehicle,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
                .fillMaxWidth()
                .clip(gaugeShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                    ),
                ),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val sweep = 270f * (batteryPercent / 100f)
            rotate(135f) {
                drawArc(
                    color = Color.White.copy(alpha = 0.14f),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = arcColor,
                    startAngle = 0f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$batteryPercent%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "$rangeMiles mi",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
