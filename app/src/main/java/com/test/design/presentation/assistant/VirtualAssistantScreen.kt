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
 * Scalable UI–style entry: light scrim + assistant panel sliding in from the right.
 * Dialogues play inline in the panel (no bottom voice plate / waveform).
 */
@Composable
fun VirtualAssistantOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
    fromEnd: Boolean = true,
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
            kotlinx.coroutines.delay(320)
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
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(200)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
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
                .padding(vertical = 8.dp, horizontal = 8.dp),
        ) {
            MoodToggleRow(
                selected = mood,
                onSelect = { mood = it },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                compact = true,
            )

            AnimatedVisibility(
                visible = panelVisible,
                enter = slideInHorizontally(
                    animationSpec = tween(380, easing = FastOutSlowInEasing),
                    initialOffsetX = { full -> if (fromEnd) full else -full },
                ) + fadeIn(tween(280)),
                exit = slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { full -> if (fromEnd) full else -full },
                ) + fadeOut(tween(220)),
            ) {
                AssistantSidePanel(
                    mood = mood,
                    onMoodChange = { mood = it },
                    onDismiss = { dismiss() },
                    autoPlay = true,
                    modifier = Modifier
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
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
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(mood.glowColor),
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (compact) Color(0xCC1A2233) else Color.Transparent,
                    labelColor = if (compact) {
                        Color.White.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    selectedContainerColor = mood.glowColor.copy(alpha = 0.28f),
                    selectedLabelColor = if (compact) {
                        Color.White
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = { chips() },
        )
    } else {
        androidx.compose.foundation.layout.FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
        color = Color(0xFF0A0C12),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AssistantFace(
                mood = mood,
                modifier = Modifier.size(72.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tap · side panel slides in",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.7f),
                )
                AssistantPresence(
                    mood = mood,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                )
            }
        }
    }
}
