package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Production overlay: soft scrim + polished trailing panel.
 * Mood chips stay available for demo, tucked below the panel edge.
 */
@Composable
fun VirtualAssistantOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
    fromEnd: Boolean = true,
    showDemoMoodChips: Boolean = true,
) {
    var mood by rememberSaveable { mutableStateOf(initialMood) }
    var panelVisible by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        panelVisible = true
    }

    fun dismiss() {
        if (closing) return
        closing = true
        panelVisible = false
    }

    LaunchedEffect(closing, panelVisible) {
        if (closing && !panelVisible) {
            kotlinx.coroutines.delay(AssistantTokens.ExitMs.toLong() + 40)
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        AnimatedVisibility(
            visible = panelVisible,
            enter = fadeIn(tween(AssistantTokens.EnterMs - 100)),
            exit = fadeOut(tween(AssistantTokens.ExitMs)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AssistantTokens.Scrim)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { dismiss() },
                    ),
            )
        }

        Column(
            modifier = Modifier
                .align(if (fromEnd) Alignment.CenterEnd else Alignment.CenterStart)
                .fillMaxHeight()
                .padding(
                    top = AssistantTokens.PanelInset,
                    bottom = AssistantTokens.PanelInset,
                    start = if (fromEnd) 0.dp else AssistantTokens.PanelInset,
                    end = if (fromEnd) AssistantTokens.PanelInset else 0.dp,
                ),
            horizontalAlignment = if (fromEnd) Alignment.End else Alignment.Start,
        ) {
            AnimatedVisibility(
                visible = panelVisible,
                modifier = Modifier.weight(1f, fill = false),
                enter = slideInHorizontally(
                    animationSpec = tween(
                        AssistantTokens.EnterMs,
                        easing = FastOutSlowInEasing,
                    ),
                    initialOffsetX = { full -> if (fromEnd) full else -full },
                ) + fadeIn(tween(AssistantTokens.EnterMs - 80)),
                exit = slideOutHorizontally(
                    animationSpec = tween(
                        AssistantTokens.ExitMs,
                        easing = FastOutSlowInEasing,
                    ),
                    targetOffsetX = { full -> if (fromEnd) full else -full },
                ) + fadeOut(tween(AssistantTokens.ExitMs - 40)),
            ) {
                AssistantSidePanel(
                    mood = mood,
                    onMoodChange = { mood = it },
                    onDismiss = { dismiss() },
                    autoPlay = true,
                    modifier = Modifier
                        .fillMaxHeight(0.98f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                )
            }

            if (showDemoMoodChips) {
                MoodToggleRow(
                    selected = mood,
                    onSelect = { mood = it },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    compact = true,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.VirtualAssistantScreen(
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
) {
    VirtualAssistantOverlay(
        onDismiss = onBack,
        modifier = modifier.fillMaxSize(),
        initialMood = initialMood,
    )
}

@Composable
fun VirtualAssistantScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
) {
    VirtualAssistantOverlay(
        onDismiss = onBack,
        modifier = modifier,
        initialMood = initialMood,
    )
}

@Composable
fun VirtualAssistantStage(
    mood: AssistantMood,
    onMoodChange: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
) {
    AssistantSidePanel(
        mood = mood,
        onMoodChange = onMoodChange,
        autoPlay = true,
        modifier = modifier,
    )
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun MoodToggleRow(
    selected: AssistantMood,
    onSelect: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val chips: @Composable () -> Unit = {
        AssistantMood.entries.forEach { mood ->
            FilterChip(
                selected = selected == mood,
                onClick = { onSelect(mood) },
                label = { Text(mood.voiceLabel) },
                leadingIcon = if (selected == mood) {
                    {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(mood.glowColor),
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (compact) {
                        Color(0x99141820)
                    } else {
                        Color.Transparent
                    },
                    labelColor = if (compact) {
                        AssistantTokens.OnSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    selectedContainerColor = mood.glowColor.copy(alpha = 0.18f),
                    selectedLabelColor = if (compact) {
                        AssistantTokens.OnSurface
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    selectedLeadingIconColor = mood.glowColor,
                ),
            )
        }
    }

    if (compact) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = { chips() },
        )
    } else {
        androidx.compose.foundation.layout.FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = { chips() },
        )
    }
}

@Composable
fun AssistantWidgetPreview(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = AssistantTokens.SurfaceBottom,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AssistantFace(
                mood = mood,
                modifier = Modifier.size(64.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Assistant",
                    style = MaterialTheme.typography.titleSmall,
                    color = AssistantTokens.OnSurface,
                )
                Text(
                    text = "Opens as a side panel",
                    style = MaterialTheme.typography.bodySmall,
                    color = AssistantTokens.OnSurfaceMuted,
                )
            }
        }
    }
}
