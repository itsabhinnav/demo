package com.test.design.presentation.motion.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget
import kotlin.math.roundToInt

private enum class MotionToken(
    val label: String,
    val description: String,
) {
    DefaultSpatial("defaultSpatialSpec", "Primary layout movement"),
    FastSpatial("fastSpatialSpec", "Quick repositioning"),
    SlowSpatial("slowSpatialSpec", "Emphasized spatial settle"),
    DefaultEffects("defaultEffectsSpec", "Opacity and scale effects"),
    FastEffects("fastEffectsSpec", "Snappy micro-interactions"),
    SlowEffects("slowEffectsSpec", "Gentle effect transitions"),
}

@Composable
fun MotionTokenReference(modifier: Modifier = Modifier) {
    var trigger by remember { mutableIntStateOf(0) }
    var activeToken by remember { mutableIntStateOf(-1) }

    val motionScheme = MaterialTheme.motionScheme
    val spatialOffset by animateDpAsState(
        targetValue = if (trigger % 2 == 0) 0.dp else 220.dp,
        animationSpec = when (activeToken) {
            MotionToken.DefaultSpatial.ordinal -> motionScheme.defaultSpatialSpec()
            MotionToken.FastSpatial.ordinal -> motionScheme.fastSpatialSpec()
            MotionToken.SlowSpatial.ordinal -> motionScheme.slowSpatialSpec()
            else -> motionScheme.defaultSpatialSpec()
        },
        label = "token_spatial",
    )
    val effectsScale by animateFloatAsState(
        targetValue = if (trigger % 2 == 0) 0.7f else 1f,
        animationSpec = when (activeToken) {
            MotionToken.DefaultEffects.ordinal -> motionScheme.defaultEffectsSpec()
            MotionToken.FastEffects.ordinal -> motionScheme.fastEffectsSpec()
            MotionToken.SlowEffects.ordinal -> motionScheme.slowEffectsSpec()
            else -> motionScheme.defaultEffectsSpec()
        },
        label = "token_effects",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CarDesignTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        Text(
            text = "Motion tokens",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Tap a token to fire its spec on the spatial orb and effects ring below.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(ExpressiveShapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp)
                    .scale(effectsScale)
                    .alpha(0.35f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
            )
            Box(
                modifier = Modifier
                    .offset { IntOffset(spatialOffset.roundToPx(), 52.dp.roundToPx()) }
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }

        MotionToken.entries.forEach { token ->
            val selected = activeToken == token.ordinal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .carTouchTarget()
                    .clickable {
                        activeToken = token.ordinal
                        trigger++
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(CarDesignTokens.TouchTargetSpacing),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = token.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                        Text(
                            text = token.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    Text(
                        text = if (selected) "●" else "○",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
