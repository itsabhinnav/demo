package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.getValue
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
 * Bottom voice plate — face personality sits in the center with mood-driven
 * listening / working / speaking wave animation behind and around it.
 */
@Composable
fun AssistantVoicePlate(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
) {
    val glow by animateColorAsState(
        targetValue = mood.glowColor,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "plate_glow",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = PlateShape,
        color = Color(0xE6121824),
        shadowElevation = 16.dp,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A2233).copy(alpha = 0.95f),
                            Color(0xFF0C121C).copy(alpha = 0.98f),
                        ),
                    ),
                )
                .padding(
                    start = CarDesignTokens.SectionPadding,
                    end = CarDesignTokens.SectionPadding,
                    top = 20.dp,
                    bottom = 28.dp,
                ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.22f)),
                )
                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        glow.copy(alpha = 0.28f * mood.glowIntensity),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )
                    VoiceWaveform(
                        mood = mood,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        color = glow,
                    )
                    AssistantFace(
                        mood = mood,
                        modifier = Modifier
                            .size(148.dp)
                            .align(Alignment.Center),
                        faceColor = Color(0xFFF5F8FF),
                    )
                }

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
}

/** Voice-plate facing label. */
val AssistantMood.voiceLabel: String
    get() = label
