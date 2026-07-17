package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens

private val PlateShape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)

/**
 * Bottom voice plate — squircle face + shared Siri-style waveform (energy by mood).
 */
@Composable
fun AssistantVoicePlate(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = PlateShape,
        color = Color(0xE60A0C12),
        shadowElevation = 16.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF12151E),
                            Color(0xFF080A10),
                        ),
                    ),
                )
                .padding(
                    start = CarDesignTokens.SectionPadding,
                    end = CarDesignTokens.SectionPadding,
                    top = 18.dp,
                    bottom = 26.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
            )
            Spacer(Modifier.height(16.dp))

            AssistantFace(
                mood = mood,
                modifier = Modifier.size(168.dp),
            )

            Spacer(Modifier.height(8.dp))

            VoiceWaveform(
                mood = mood,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(horizontal = 8.dp),
            )

            Spacer(Modifier.height(12.dp))

            AnimatedContent(
                targetState = mood,
                transitionSpec = {
                    fadeIn(tween(280)) togetherWith fadeOut(tween(180))
                },
                label = "plate_caption",
            ) { current ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = current.voiceLabel,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = current.caption,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/** Voice-plate facing label. */
val AssistantMood.voiceLabel: String
    get() = label
