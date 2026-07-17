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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
 * Transparent overlay host for [VirtualAssistantActivity] — dim scrim + bottom voice plate.
 */
@Composable
fun VirtualAssistantOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Listening,
) {
    var mood by rememberSaveable { mutableStateOf(initialMood) }

    Box(modifier = modifier.fillMaxSize()) {
        // Scrim — tap outside plate to dismiss; keeps map/UI visible underneath.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            MoodToggleRow(
                selected = mood,
                onSelect = { mood = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .horizontalScroll(rememberScrollState()),
                compact = true,
            )
            // Consume clicks on the plate so they don't dismiss.
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
            ) {
                AssistantVoicePlate(
                    mood = mood,
                    modifier = Modifier.fillMaxWidth(),
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
    var mood by rememberSaveable { mutableStateOf(initialMood) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CarDesignTokens.SectionPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        WidgetScreenHeader(
            widget = DashboardWidget.VirtualAssistant,
            onBack = onBack,
            animatedVisibilityScope = animatedVisibilityScope,
        )
        VirtualAssistantStage(
            mood = mood,
            onMoodChange = { mood = it },
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AssistantVoicePlate(
            mood = mood,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge),
        )
        Text(
            text = "Mood",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        )
        MoodToggleRow(
            selected = mood,
            onSelect = onMoodChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
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
        color = Color(0xFF0E1522),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            VoiceWaveform(
                mood = mood,
                modifier = Modifier.fillMaxSize(),
                color = mood.glowColor,
            )
            AssistantFace(
                mood = mood,
                modifier = Modifier.size(96.dp),
                faceColor = Color(0xFFF5F8FF),
            )
        }
    }
}
