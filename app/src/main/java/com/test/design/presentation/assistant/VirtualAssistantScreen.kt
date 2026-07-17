package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.CarDesignTokens

/**
 * Transparent overlay — dialogue simulation + side persona rail.
 */
@Composable
fun VirtualAssistantOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
) {
    var mood by rememberSaveable { mutableStateOf(initialMood) }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        ) {
            MoodToggleRow(
                selected = mood,
                onSelect = { mood = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 10.dp),
                compact = true,
            )
            AssistantDialogueStage(
                mood = mood,
                onMoodChange = { mood = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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
    var mood by rememberSaveable { mutableStateOf(initialMood) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CarDesignTokens.SectionPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        WidgetScreenHeader(
            widget = DashboardWidget.VirtualAssistant,
            onBack = onBack,
            animatedVisibilityScope = animatedVisibilityScope,
        )
        AssistantDialogueStage(
            mood = mood,
            onMoodChange = { mood = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
        MoodToggleRow(
            selected = mood,
            onSelect = { mood = it },
            modifier = Modifier.fillMaxWidth(),
        )
    }
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
    AssistantDialogueStage(
        mood = mood,
        onMoodChange = onMoodChange,
        modifier = modifier.height(420.dp),
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
                    text = "Dialogue sim",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.7f),
                )
                VoiceWaveform(
                    mood = mood,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                )
            }
        }
    }
}
