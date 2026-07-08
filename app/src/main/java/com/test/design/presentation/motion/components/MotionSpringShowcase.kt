package com.test.design.presentation.motion.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.test.design.core.motion.LocalEffectiveMotionScheme
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carTouchTarget
import kotlin.math.roundToInt

@Composable
fun MotionSpringShowcase(modifier: Modifier = Modifier) {
    val scheme = LocalEffectiveMotionScheme.current
    val haptic = LocalHapticFeedback.current
    var spatialTrigger by remember { mutableIntStateOf(0) }
    var effectsTrigger by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var slideIndex by remember { mutableIntStateOf(0) }

    val spatialOffset by animateDpAsState(
        targetValue = if (spatialTrigger % 2 == 0) 0.dp else 280.dp,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "spatial_demo",
    )
    val effectsScale by animateDpAsState(
        targetValue = if (effectsTrigger % 2 == 0) 56.dp else 96.dp,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "effects_demo",
    )

    LaunchedEffect(spatialTrigger) {
        if (spatialTrigger == 0) return@LaunchedEffect
        delay(480)
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    LaunchedEffect(effectsTrigger) {
        if (effectsTrigger == 0) return@LaunchedEffect
        delay(420)
        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CarDesignTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        Text(
            text = "Active scheme: ${scheme.label}",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Change Standard / Expressive / Custom on the home side panel, then interact below.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        TokenDemoCard(
            title = "defaultSpatialSpec",
            description = "Tap to slide the orb using spatial physics.",
            onClick = { spatialTrigger++ },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(ExpressiveShapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(spatialOffset.roundToPx(), 24.dp.roundToPx()) }
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }

        TokenDemoCard(
            title = "defaultEffectsSpec",
            description = "Tap to pulse size using effects physics.",
            onClick = { effectsTrigger++ },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(effectsScale)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary),
                )
            }
        }

        TokenDemoCard(
            title = "AnimatedVisibility",
            description = "Expand/collapse with theme motion.",
            onClick = { expanded = !expanded },
        ) {
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Text(
                        text = "Expanded surface",
                        modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        TokenDemoCard(
            title = "AnimatedContent shared-axis",
            description = "Horizontal slide between numbered states.",
            onClick = { slideIndex = (slideIndex + 1) % 3 },
        ) {
            val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntOffset>()
            val effectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
            AnimatedContent(
                targetState = slideIndex,
                transitionSpec = {
                    slideInHorizontally(animationSpec = spatialSpec) { it } + fadeIn(animationSpec = effectsSpec) togetherWith
                        slideOutHorizontally(animationSpec = spatialSpec) { -it } + fadeOut(animationSpec = effectsSpec)
                },
                label = "shared_axis_demo",
            ) { index ->
                Text(
                    text = "State $index",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
            Box(modifier = Modifier.weight(1f)) {
                FastSlowSpatialButton(label = "slowSpatial")
            }
            Box(modifier = Modifier.weight(1f)) {
                FastSlowSpatialButton(label = "fastSpatial", fast = true)
            }
        }
    }
}

@Composable
private fun FastSlowSpatialButton(label: String, fast: Boolean = false) {
    var toggled by remember { mutableStateOf(false) }
    val spec = if (fast) {
        MaterialTheme.motionScheme.fastSpatialSpec<androidx.compose.ui.unit.Dp>()
    } else {
        MaterialTheme.motionScheme.slowSpatialSpec<androidx.compose.ui.unit.Dp>()
    }
    val offset by animateDpAsState(
        targetValue = if (toggled) 48.dp else 0.dp,
        animationSpec = spec,
        label = label,
    )
    Button(
        onClick = { toggled = !toggled },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .offset(y = offset)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun TokenDemoCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .carTouchTarget()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}
