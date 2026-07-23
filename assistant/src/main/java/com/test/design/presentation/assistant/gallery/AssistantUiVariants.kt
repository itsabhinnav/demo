package com.test.design.presentation.assistant.gallery

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.design.presentation.assistant.AssistantFace
import com.test.design.presentation.assistant.AssistantMood
import com.test.design.presentation.assistant.AssistantPresence
import com.test.design.presentation.assistant.DroidAssistantFace
import com.test.design.presentation.assistant.DroidFaceGlyph
import com.test.design.presentation.assistant.DroidGlyph
import com.test.design.presentation.assistant.EporoAssistantFace
import com.test.design.presentation.assistant.FusionAssistantFace
import com.test.design.presentation.assistant.FusionEyesAssistantFace
import com.test.design.presentation.assistant.FusionGlowAssistantFace
import com.test.design.presentation.assistant.ImmersiveEyesFace
import com.test.design.presentation.assistant.ImmersiveGlowEyesFace
import com.test.design.presentation.assistant.LiveInputText
import com.test.design.presentation.assistant.VoiceWaveform
import com.test.design.presentation.assistant.overlay.AssistantState
import com.test.design.presentation.assistant.overlay.CarAssistantFace
import com.test.design.presentation.assistant.toDroidFaceGlyph
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders one opaque-stage assistant chrome style for the given mood.
 */
@Composable
fun AssistantUiVariant(
    style: AssistantUiStyle,
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    prompt: String = "How can I help?",
) {
    when (style) {
        AssistantUiStyle.VoicePlate -> VoicePlateUi(mood, prompt, modifier)
        AssistantUiStyle.FaceOnly -> FaceOnlyUi(mood, modifier)
        AssistantUiStyle.WaveformCenter -> WaveformCenterUi(mood, modifier)
        AssistantUiStyle.OrbGlow -> OrbGlowUi(mood, modifier)
        AssistantUiStyle.CapsuleFace -> CapsuleFaceUi(mood, modifier)
        AssistantUiStyle.StatusBar -> StatusBarUi(mood, prompt, modifier)
        AssistantUiStyle.SideRail -> SideRailUi(mood, prompt, modifier)
        AssistantUiStyle.EqualizerBars -> EqualizerBarsUi(mood, modifier)
        AssistantUiStyle.ListeningRings -> ListeningRingsUi(mood, modifier)
        AssistantUiStyle.CornerBubble -> CornerBubbleUi(mood, prompt, modifier)
        AssistantUiStyle.WaveFaceCombo -> WaveFaceComboUi(mood, prompt, modifier)
        AssistantUiStyle.AmbientPill -> AmbientPillUi(mood, prompt, modifier)
        AssistantUiStyle.ImmersiveEyes -> ImmersiveEyesUi(mood, prompt, modifier)
        AssistantUiStyle.ImmersiveGlow -> ImmersiveGlowUi(mood, prompt, modifier)
        AssistantUiStyle.DroidFace -> DroidFaceUi(mood, modifier)
        AssistantUiStyle.EporoFace -> EporoFaceUi(mood, prompt, modifier)
        AssistantUiStyle.FusionFace -> FusionFaceUi(mood, prompt, modifier)
        AssistantUiStyle.FusionGlowFace -> FusionGlowFaceUi(mood, prompt, modifier)
        AssistantUiStyle.FusionEyesFace -> FusionEyesFaceUi(mood, prompt, modifier)
    }
}

@Composable
private fun FusionFaceUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1C22),
                        Color(0xFF0B0C10),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FusionAssistantFace(
                mood = mood,
                modifier = Modifier.width(148.dp),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = mood.label,
                color = mood.glowColor,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(6.dp))
            LiveInputText(
                text = prompt,
                color = Color(0xFFF1F3F4),
                live = mood == AssistantMood.Listening,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 28.dp),
            )
        }
    }
}

@Composable
private fun FusionGlowFaceUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1C22),
                        Color(0xFF0B0C10),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FusionGlowAssistantFace(
                mood = mood,
                modifier = Modifier.width(148.dp),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = mood.label,
                color = mood.glowColor,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(6.dp))
            LiveInputText(
                text = prompt,
                color = Color(0xFFF1F3F4),
                live = mood == AssistantMood.Listening,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 28.dp),
            )
        }
    }
}

@Composable
private fun FusionEyesFaceUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1C22),
                        Color(0xFF0B0C10),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FusionEyesAssistantFace(
                mood = mood,
                modifier = Modifier.width(148.dp),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = mood.label,
                color = mood.glowColor,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(6.dp))
            LiveInputText(
                text = prompt,
                color = Color(0xFFF1F3F4),
                live = mood == AssistantMood.Listening,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 28.dp),
            )
        }
    }
}

@Composable
private fun EporoFaceUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1C22),
                        Color(0xFF0B0C10),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            EporoAssistantFace(
                mood = mood,
                modifier = Modifier.width(148.dp),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = mood.label,
                color = mood.glowColor,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(6.dp))
            LiveInputText(
                text = prompt,
                color = Color(0xFFF1F3F4),
                live = mood == AssistantMood.Listening,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 28.dp),
            )
        }
    }
}

@Composable
private fun DroidFaceUi(mood: AssistantMood, modifier: Modifier) {
    val selected = mood.toDroidFaceGlyph()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(56.dp))
        DroidAssistantFace(
            glyph = selected,
            shellMood = mood,
            modifier = Modifier.size(120.dp),
        )
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 72.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(DroidFaceGlyph.entries) { glyph ->
                val isSelected = glyph == selected
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .border(
                                        width = 2.dp,
                                        color = DroidGlyph,
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                    .padding(2.dp)
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    DroidAssistantFace(
                        glyph = glyph,
                        modifier = Modifier.fillMaxSize(0.92f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ImmersiveEyesUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101820),
                        Color(0xFF0A0C10),
                        Color(0xFF050608),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ImmersiveEyesFace(
                mood = mood,
                modifier = Modifier.size(228.dp),
            )
            Spacer(Modifier.height(28.dp))
            LiveInputText(
                text = prompt,
                color = Color(0xFFF8F9FA),
                live = mood == AssistantMood.Listening,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun ImmersiveGlowUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101820),
                        Color(0xFF0A0C10),
                        Color(0xFF050608),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ImmersiveGlowEyesFace(
                mood = mood,
                modifier = Modifier.size(228.dp),
            )
            Spacer(Modifier.height(28.dp))
            LiveInputText(
                text = prompt,
                color = Color(0xFFF8F9FA),
                live = mood == AssistantMood.Listening,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun VoicePlateUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(200.dp),
            corner = 28.dp,
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AssistantFace(mood = mood, modifier = Modifier.size(88.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = mood.label,
                        color = mood.glowColor,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = prompt,
                        color = AssistantUiChrome.OnGlass,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(10.dp))
                    VoiceWaveform(
                        mood = mood,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FaceOnlyUi(mood: AssistantMood, modifier: Modifier) {
    // No outer glass disc — just the floating persona with a hairline silver rim.
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AssistantFace(
            mood = mood,
            modifier = Modifier.size(168.dp),
            faceFillRatio = 0.97f,
        )
    }
}

@Composable
private fun WaveformCenterUi(mood: AssistantMood, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(120.dp),
            corner = 24.dp,
        ) {
            VoiceWaveform(
                mood = mood,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun OrbGlowUi(mood: AssistantMood, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(Modifier.size(240.dp), contentAlignment = Alignment.Center) {
            AssistantPresence(mood = mood, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(AssistantUiChrome.Glass)
                    .borderOrb(),
                contentAlignment = Alignment.Center,
            ) {
                AssistantFace(mood = mood, modifier = Modifier.size(120.dp))
            }
        }
    }
}

private fun Modifier.borderOrb(): Modifier =
    this.then(
        Modifier.border(
            width = 1.dp,
            color = AssistantUiChrome.GlassEdge,
            shape = CircleShape,
        ),
    )

@Composable
private fun CapsuleFaceUi(mood: AssistantMood, modifier: Modifier) {
    val state = when (mood) {
        AssistantMood.Listening -> AssistantState.LISTENING
        AssistantMood.Thinking -> AssistantState.THINKING
        AssistantMood.Speaking -> AssistantState.SPEAKING
        AssistantMood.Sad -> AssistantState.ERROR
        else -> AssistantState.IDLE
    }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        CarAssistantFace(
            state = state,
            audioAmplitude = if (mood == AssistantMood.Speaking) 0.55f else 0.15f,
            modifier = Modifier.padding(bottom = 32.dp),
        )
    }
}

@Composable
private fun StatusBarUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(56.dp),
            corner = 16.dp,
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(mood.glowColor),
                )
                Text(
                    text = prompt,
                    color = AssistantUiChrome.OnGlass,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                AmplitudeMeter(mood = mood, modifier = Modifier.width(72.dp).height(18.dp))
            }
        }
    }
}

@Composable
private fun SideRailUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        GlassSurface(
            modifier = Modifier
                .width(320.dp)
                .fillMaxHeight()
                .padding(12.dp),
            corner = 28.dp,
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Assistant",
                    color = AssistantUiChrome.OnGlassMuted,
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(Modifier.height(20.dp))
                AssistantFace(mood = mood, modifier = Modifier.size(120.dp))
                Spacer(Modifier.height(16.dp))
                Text(
                    text = prompt,
                    color = AssistantUiChrome.OnGlass,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.weight(1f))
                VoiceWaveform(
                    mood = mood,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                )
            }
        }
    }
}

@Composable
private fun EqualizerBarsUi(mood: AssistantMood, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassSurface(
            modifier = Modifier
                .width(280.dp)
                .height(140.dp),
            corner = 24.dp,
        ) {
            EqualizerCanvas(
                mood = mood,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            )
        }
    }
}

@Composable
private fun ListeningRingsUi(mood: AssistantMood, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(AssistantUiChrome.Glass),
            contentAlignment = Alignment.Center,
        ) {
            ListeningRingsCanvas(mood = mood, modifier = Modifier.fillMaxSize())
            AssistantFace(mood = mood, modifier = Modifier.size(96.dp))
        }
    }
}

@Composable
private fun CornerBubbleUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    com.test.design.presentation.assistant.AssistantCornerBubble(
        mood = mood,
        prompt = prompt,
        modifier = modifier,
    )
}

@Composable
private fun WaveFaceComboUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(260.dp),
            corner = 32.dp,
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AssistantFace(mood = mood, modifier = Modifier.size(110.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = prompt,
                    color = AssistantUiChrome.OnGlass,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                VoiceWaveform(
                    mood = mood,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                )
            }
        }
    }
}

@Composable
private fun AmbientPillUi(mood: AssistantMood, prompt: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        GlassSurface(
            modifier = Modifier.padding(bottom = 28.dp),
            corner = 40.dp,
        ) {
            Row(
                Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(mood.glowColor),
                )
                Text(
                    text = prompt,
                    color = AssistantUiChrome.OnGlass,
                    style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.2.sp),
                )
                AmplitudeMeter(mood = mood, modifier = Modifier.width(48.dp).height(14.dp))
            }
        }
    }
}

@Composable
private fun AmplitudeMeter(mood: AssistantMood, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "meter")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "p",
    )
    Canvas(modifier) {
        val bars = 5
        val gap = size.width / (bars * 2f)
        val w = gap * 0.9f
        for (i in 0 until bars) {
            val h = size.height * (0.35f + 0.55f * ((sin(phase + i * 0.9f) + 1f) * 0.5f))
            val x = i * (w + gap) + gap * 0.5f
            drawRoundRect(
                color = mood.glowColor.copy(alpha = 0.85f),
                topLeft = Offset(x, size.height - h),
                size = Size(w, h),
                cornerRadius = CornerRadius(w / 2f, w / 2f),
            )
        }
    }
}

@Composable
private fun EqualizerCanvas(mood: AssistantMood, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "eq")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing)),
        label = "eq_p",
    )
    Canvas(modifier) {
        val bars = 16
        val gap = size.width * 0.02f
        val w = (size.width - gap * (bars + 1)) / bars
        for (i in 0 until bars) {
            val n = ((sin(phase * 1.2f + i * 0.45f) + 1f) * 0.5f)
            val h = size.height * (0.2f + 0.75f * n)
            val x = gap + i * (w + gap)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(mood.glowColor, AssistantUiChrome.Accent),
                ),
                topLeft = Offset(x, size.height - h),
                size = Size(w, h),
                cornerRadius = CornerRadius(w / 2f, w / 2f),
            )
        }
    }
}

@Composable
private fun ListeningRingsCanvas(mood: AssistantMood, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "rings")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing),
            RepeatMode.Restart,
        ),
        label = "ring_p",
    )
    Canvas(modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val base = size.minDimension * 0.18f
        for (i in 0..3) {
            val t = ((phase + i * 0.22f) % 1f)
            val r = base * (1f + t * 2.4f)
            drawCircle(
                color = mood.glowColor.copy(alpha = (1f - t) * 0.45f),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = base * 0.12f * (1f - t * 0.5f), cap = StrokeCap.Round),
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                listOf(mood.glowColor.copy(alpha = 0.25f), Color.Transparent),
                center = Offset(cx, cy),
                radius = base * 2.2f,
            ),
            radius = base * 2.2f,
            center = Offset(cx, cy),
        )
    }
}
