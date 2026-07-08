package com.test.design.presentation.ivi.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes

private const val MAP_CENTER_LAT = 37.7749
private const val MAP_CENTER_LON = -122.4194

private fun staticMapUrl(): String =
    "https://staticmap.openstreetmap.de/staticmap.php" +
        "?center=$MAP_CENTER_LAT,$MAP_CENTER_LON&zoom=13&size=960x540&maptype=mapnik" +
        "&markers=$MAP_CENTER_LAT,$MAP_CENTER_LON,red-pushpin"

@Composable
fun DummyRouteMap(
    destination: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .heightIn(min = 220.dp)
            .clip(ExpressiveShapes.extraLarge),
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(staticMapUrl())
                .crossfade(true)
                .build(),
            contentDescription = "Map routing to $destination",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = { MapLoadingPlaceholder(destination) },
            error = { MapLoadingPlaceholder(destination) },
        )

        RouteOverlay(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(CarDesignTokens.TouchTargetSpacing)
                .clip(ExpressiveShapes.medium)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                text = "→ $destination",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun MapLoadingPlaceholder(destination: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Loading map to $destination…",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RouteOverlay(modifier: Modifier = Modifier) {
    val routeColor = MaterialTheme.colorScheme.primary
    val haloColor = Color.White.copy(alpha = 0.85f)
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.78f)
            cubicTo(
                size.width * 0.28f, size.height * 0.62f,
                size.width * 0.42f, size.height * 0.48f,
                size.width * 0.55f, size.height * 0.42f,
            )
            cubicTo(
                size.width * 0.68f, size.height * 0.36f,
                size.width * 0.78f, size.height * 0.28f,
                size.width * 0.52f, size.height * 0.18f,
            )
        }
        drawPath(path, color = haloColor, style = Stroke(width = 14.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
        drawPath(path, color = routeColor, style = Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
        drawCircle(routeColor, radius = 10.dp.toPx(), center = Offset(size.width * 0.52f, size.height * 0.18f))
        drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(size.width * 0.52f, size.height * 0.18f))
    }
}
