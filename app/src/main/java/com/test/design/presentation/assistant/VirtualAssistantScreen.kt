package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.CarDesignTokens

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
    var mood by rememberSaveable { mutableStateOf(initialMood) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CarDesignTokens.SectionPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        WidgetScreenHeader(
            title = "Assistant",
            onBack = onBack,
        )
        VirtualAssistantStage(
            mood = mood,
            onMoodChange = { mood = it },
            modifier = Modifier.fillMaxWidth(),
        )
    }
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
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B1220),
                            Color(0xFF121A2A),
                            Color(0xFF0A1018),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Soft ambient wash matching active mood glow
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                mood.glowColor.copy(alpha = 0.22f * mood.glowIntensity),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            AssistantFace(
                mood = mood,
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .padding(horizontal = 48.dp),
                faceColor = Color(0xFFF5F8FF),
            )
        }

        AnimatedContent(
            targetState = mood,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "mood_caption",
        ) { current ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = current.label,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = current.caption,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodToggleRow(
    selected: AssistantMood,
    onSelect: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AssistantMood.entries.forEach { mood ->
            FilterChip(
                selected = selected == mood,
                onClick = { onSelect(mood) },
                label = { Text(mood.label) },
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
                    selectedContainerColor = mood.glowColor.copy(alpha = 0.22f),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = mood.glowColor,
                ),
            )
        }
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
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            AssistantFace(
                mood = mood,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                faceColor = Color(0xFFF5F8FF),
            )
        }
    }
}
