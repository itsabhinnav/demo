package com.test.design.presentation.demos.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold

private val StandardEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
private val Emphasized = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

private enum class TransitionScene { List, FullScreen }

@Composable
fun TransitionPatternsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var scene by remember { mutableStateOf(TransitionScene.List) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSkeleton by remember { mutableStateOf(true) }
    val tabs = listOf("Forward", "Backward")

    DemoScaffold(
        title = "Transition Patterns",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            Text(
                text = "Demonstrates full-screen enter/exit, card container transform, lateral same-level transitions, and skeleton loading.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = OemSpacing.md),
            )
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            tabs.forEachIndexed { index, label ->
                AssistChip(
                    onClick = { selectedTab = index },
                    label = { Text(label) },
                )
            }
        }

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally(
                        animationSpec = tween(280, easing = StandardEasing),
                        initialOffsetX = { it / 3 },
                    ) + fadeIn(tween(280))) togetherWith
                        (slideOutHorizontally(
                            animationSpec = tween(220, easing = StandardEasing),
                            targetOffsetX = { -it / 3 },
                        ) + fadeOut(tween(220)))
                } else {
                    (slideInHorizontally(
                        animationSpec = tween(280, easing = StandardEasing),
                        initialOffsetX = { -it / 3 },
                    ) + fadeIn(tween(280))) togetherWith
                        (slideOutHorizontally(
                            animationSpec = tween(220, easing = StandardEasing),
                            targetOffsetX = { it / 3 },
                        ) + fadeOut(tween(220)))
                }
            },
            label = "lateralScene",
        ) {
            if (it == 0) {
                CustomSectionHeader(
                    title = "Forward navigation",
                    subtitle = "Primary actions move card content into a full-screen route.",
                )
            } else {
                CustomSectionHeader(
                    title = "Backward navigation",
                    subtitle = "Closing exits faster to restore prior context quickly.",
                )
            }
        }

        AnimatedContent(
            targetState = scene,
            transitionSpec = {
                if (targetState == TransitionScene.FullScreen) {
                    (fadeIn(animationSpec = tween(380, easing = Emphasized))) togetherWith
                        (fadeOut(animationSpec = tween(180, easing = StandardEasing)))
                } else {
                    (fadeIn(animationSpec = tween(240, easing = StandardEasing))) togetherWith
                        (fadeOut(animationSpec = tween(160, easing = StandardEasing)))
                }
            },
            label = "fullScreenEnterExit",
        ) { current ->
            when (current) {
                TransitionScene.List -> {
                    TransitionList(
                        onCardClick = { scene = TransitionScene.FullScreen },
                        showSkeleton = showSkeleton,
                        onToggleSkeleton = { showSkeleton = !showSkeleton },
                    )
                }

                TransitionScene.FullScreen -> {
                    FullScreenContainerTransform(onClose = { scene = TransitionScene.List })
                }
            }
        }
    }
}

@Composable
private fun TransitionList(
    onCardClick: () -> Unit,
    showSkeleton: Boolean,
    onToggleSkeleton: () -> Unit,
) {
    FilledTonalButton(
        onClick = onToggleSkeleton,
        modifier = Modifier.padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
    ) {
        Text(if (showSkeleton) "Load real content" else "Show skeleton loading")
    }
    if (showSkeleton) {
        SkeletonList()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            items((1..4).toList()) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable { onCardClick() }
                        .padding(OemSpacing.md),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.xs)) {
                        Text("Route card $index", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Tap to transform into full-screen details with enter animation.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FullScreenContainerTransform(onClose: () -> Unit) {
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(420, easing = Emphasized),
        label = "containerScale",
    )
    val corner by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(420, easing = Emphasized),
        label = "cornerMorph",
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm)
            .graphicsLayer {
                scaleX = 0.95f + (0.05f * progress)
                scaleY = 0.95f + (0.05f * progress)
            }
            .clip(RoundedCornerShape(corner))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(OemSpacing.md),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            Text("Full-screen destination", style = MaterialTheme.typography.headlineSmall)
            Text(
                "This models a card-to-screen container transform with emphasized easing for forward navigation.",
                style = MaterialTheme.typography.bodyLarge,
            )
            FilledTonalButton(onClick = onClose) {
                Text("Back to cards")
            }
        }
    }
}

@Composable
private fun SkeletonList() {
    val pulse = rememberInfiniteTransition(label = "skeletonPulse")
    val alpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = StandardEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alphaPulse",
    )
    Column(
        modifier = Modifier.padding(horizontal = OemSpacing.md),
        verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(OemSpacing.md),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
                    Box(
                        modifier = Modifier
                            .size(width = 160.dp, height = 14.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceBright),
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 240.dp, height = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceBright),
                    )
                }
            }
        }
    }
}
