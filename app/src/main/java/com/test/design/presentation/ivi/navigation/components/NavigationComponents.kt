package com.test.design.presentation.ivi.navigation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.navigation.FavoritePlace
import com.test.design.presentation.ivi.navigation.RouteStep
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@Composable
fun TurnInstructionCard(
    instruction: String,
    maneuverIcon: String,
    distanceRemaining: String,
    etaMinutes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val gap = if (compact) 12.dp else CarDesignTokens.TouchTargetSpacing
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
            emphasized = !compact,
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = maneuverIcon,
                    style = if (compact) {
                        MaterialTheme.typography.headlineMedium
                    } else {
                        MaterialTheme.typography.displayMedium
                    },
                    color = MaterialTheme.colorScheme.primary,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text,
                        style = if (compact) {
                            MaterialTheme.typography.titleMedium
                        } else {
                            MaterialTheme.typography.titleLarge
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "$distanceRemaining · $etaMinutes min",
                        style = if (compact) {
                            MaterialTheme.typography.bodySmall
                        } else {
                            MaterialTheme.typography.bodyMedium
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteDestinationsRow(
    favorites: List<FavoritePlace>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val chipHeight = if (compact) 56.dp else CarDesignTokens.MinTouchTarget
    val gap = if (compact) 8.dp else CarDesignTokens.TouchTargetSpacing
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        items(favorites, key = { it.id }) { place ->
            FilterChip(
                selected = selectedId == place.id,
                onClick = { onSelected(place.id) },
                modifier = Modifier
                    .then(if (compact) Modifier else Modifier.carTouchTarget())
                    .height(chipHeight),
                label = {
                    Text(
                        "${place.name} · ${place.etaLabel}",
                        style = if (compact) {
                            MaterialTheme.typography.labelMedium
                        } else {
                            MaterialTheme.typography.labelLarge
                        },
                    )
                },
            )
        }
    }
}

@Composable
fun RouteStepsList(
    steps: List<RouteStep>,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp),
    ) {
        steps.forEach { step ->
            DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        step.instruction,
                        style = if (compact) {
                            MaterialTheme.typography.bodyMedium
                        } else {
                            MaterialTheme.typography.bodyLarge
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        step.distanceLabel,
                        style = if (compact) {
                            MaterialTheme.typography.labelMedium
                        } else {
                            MaterialTheme.typography.labelLarge
                        },
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
