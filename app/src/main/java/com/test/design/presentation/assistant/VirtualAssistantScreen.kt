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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.unit.dp

/**
 * Transparent overlay — only the trailing panel is drawn.
 * Outside taps dismiss; no scrim, no mood chips.
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
        // Invisible hit target — keeps the rest of the window clear
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { dismiss() },
                ),
        )

        AnimatedVisibility(
            visible = panelVisible,
            modifier = Modifier
                .align(if (fromEnd) Alignment.CenterEnd else Alignment.CenterStart)
                .fillMaxHeight()
                .padding(
                    top = AssistantTokens.PanelInset,
                    bottom = AssistantTokens.PanelInset,
                    start = if (fromEnd) 0.dp else AssistantTokens.PanelInset,
                    end = if (fromEnd) AssistantTokens.PanelInset else 0.dp,
                ),
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
