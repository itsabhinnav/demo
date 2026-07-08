package com.test.design.presentation.ivi.navigation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.navigation.FavoritePlace
import com.test.design.presentation.ivi.navigation.RouteStep
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget

@Composable
fun TurnInstructionCard(
    instruction: String,
    maneuverIcon: String,
    distanceRemaining: String,
    etaMinutes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    AnimatedContent(
        targetState = instruction,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = {
            (scaleIn(animationSpec = motionSpec, initialScale = 0.9f) + fadeIn(animationSpec = motionSpec))
                .togetherWith(scaleOut(animationSpec = motionSpec, targetScale = 1.05f) + fadeOut(animationSpec = motionSpec))
        },
        label = "turn_instruction",
    ) { text ->
        DetailSurfaceCard(
            emphasized = true,
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = maneuverIcon,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = "$distanceRemaining · $etaMinutes min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun RouteMapPlaceholder(
    destination: String,
    modifier: Modifier = Modifier,
) {
    val pulse by animateFloatAsState(
        targetValue = 1f,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>(),
        label = "map_pulse",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(ExpressiveShapes.extraLarge)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = pulse; scaleY = pulse }
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Routing to $destination",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
fun FavoriteDestinationsRow(
    favorites: List<FavoritePlace>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        items(favorites, key = { it.id }) { place ->
            FilterChip(
                selected = selectedId == place.id,
                onClick = { onSelected(place.id) },
                modifier = Modifier
                    .carTouchTarget()
                    .height(CarDesignTokens.MinTouchTarget),
                label = { Text("${place.name} · ${place.etaLabel}", style = MaterialTheme.typography.labelLarge) },
            )
        }
    }
}

@Composable
fun RouteStepsList(
    steps: List<RouteStep>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        steps.forEach { step ->
            DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(step.instruction, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Text(step.distanceLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
