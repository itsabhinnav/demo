package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Full-screen immersive assistant: bottom→up black gradient, centered elliptical eyes,
 * single current transcript line, subtle blue border glow. Fades in on hotword or tap.
 */
@Composable
fun ImmersiveAssistantOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
    awaitHotword: Boolean = true,
    onRequestHotwordListen: (() -> Unit)? = null,
    script: List<DialogueBeat> = ImmersiveDialogueScript,
) {
    var visible by remember { mutableStateOf(!awaitHotword) }
    var session by remember { mutableStateOf(0) }
    var mood by remember { mutableStateOf(initialMood) }
    var transcript by remember { mutableStateOf("") }
    var speaker by remember { mutableStateOf(DialogueSpeaker.System) }

    fun summon() {
        session += 1
        mood = AssistantMood.Listening
        transcript = ""
        speaker = DialogueSpeaker.System
        visible = true
    }

    ImmersiveHotwordBridge(onSummon = { summon() })

    // Drive conversation: one line at a time; eyes follow mood / phase.
    LaunchedEffect(visible, session) {
        if (!visible) return@LaunchedEffect
        for (beat in script) {
            if (!isActive || !visible) break
            mood = beat.mood
            speaker = beat.speaker
            transcript = beat.text
            delay(beat.holdMs)
        }
        if (visible) {
            delay(600)
            visible = false
            delay(380)
            onDismiss()
        }
    }

    val overlayAlpha = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        overlayAlpha.animateTo(
            if (visible) 1f else 0f,
            tween(if (visible) 520 else 360, easing = FastOutSlowInEasing),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (visible) {
                        visible = false
                    } else {
                        onRequestHotwordListen?.invoke()
                        summon()
                    }
                },
            ),
    ) {
        AnimatedVisibility(
            visible = visible || overlayAlpha.value > 0.02f,
            enter = fadeIn(tween(480, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(340)),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = overlayAlpha.value.coerceIn(0f, 1f) },
            ) {
                ImmersiveBackdrop()
                ImmersiveBorderGlow()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 48.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ImmersiveEyesFace(
                            mood = mood,
                            modifier = Modifier.size(280.dp),
                        )
                    }

                    ImmersiveTranscript(
                        text = transcript,
                        speaker = speaker,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 28.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ImmersiveBackdrop() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0x33101820),
                        0.35f to Color(0x88101820),
                        0.65f to Color(0xCC0A0C10),
                        1.0f to Color(0xF2050608),
                    ),
                ),
            ),
    )
}

@Composable
private fun ImmersiveBorderGlow() {
    val blue = Color(0xFF8AB4F8)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val stroke = 3.dp.toPx()
        val inset = 10.dp.toPx()
        val w = size.width
        val h = size.height
        // Soft outer bloom
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    blue.copy(alpha = 0.08f),
                    Color.Transparent,
                    blue.copy(alpha = 0.12f),
                ),
            ),
            size = size,
        )
        // Edge glow — top
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(blue.copy(alpha = 0.45f), Color.Transparent),
                startY = inset,
                endY = inset + 48.dp.toPx(),
            ),
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2f, 48.dp.toPx()),
        )
        // Edge glow — bottom
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, blue.copy(alpha = 0.55f)),
                startY = h - inset - 64.dp.toPx(),
                endY = h - inset,
            ),
            topLeft = Offset(inset, h - inset - 64.dp.toPx()),
            size = Size(w - inset * 2f, 64.dp.toPx()),
        )
        // Side glows
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(blue.copy(alpha = 0.4f), Color.Transparent),
                startX = inset,
                endX = inset + 40.dp.toPx(),
            ),
            topLeft = Offset(inset, inset),
            size = Size(40.dp.toPx(), h - inset * 2f),
        )
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, blue.copy(alpha = 0.4f)),
                startX = w - inset - 40.dp.toPx(),
                endX = w - inset,
            ),
            topLeft = Offset(w - inset - 40.dp.toPx(), inset),
            size = Size(40.dp.toPx(), h - inset * 2f),
        )
        // Hairline frame
        drawRect(
            color = blue.copy(alpha = 0.35f),
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2f, stroke),
        )
        drawRect(
            color = blue.copy(alpha = 0.45f),
            topLeft = Offset(inset, h - inset - stroke),
            size = Size(w - inset * 2f, stroke),
        )
        drawRect(
            color = blue.copy(alpha = 0.3f),
            topLeft = Offset(inset, inset),
            size = Size(stroke, h - inset * 2f),
        )
        drawRect(
            color = blue.copy(alpha = 0.3f),
            topLeft = Offset(w - inset - stroke, inset),
            size = Size(stroke, h - inset * 2f),
        )
    }
}

@Composable
private fun ImmersiveTranscript(
    text: String,
    speaker: DialogueSpeaker,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = text to speaker,
            transitionSpec = {
                (fadeIn(tween(280)) + slideInVertically(tween(320)) { it / 4 }) togetherWith
                    (fadeOut(tween(180)) + slideOutVertically(tween(220)) { -it / 5 })
            },
            label = "immersive_transcript",
        ) { (line, who) ->
            if (line.isBlank()) {
                Box(modifier = Modifier.padding(8.dp))
            } else {
                val whoLabel = when (who) {
                    DialogueSpeaker.User -> "You"
                    DialogueSpeaker.Assistant -> "Assistant"
                    DialogueSpeaker.System -> " "
                }
                val whoLabelColor = when (who) {
                    DialogueSpeaker.User -> Color(0xFF90CAF9)
                    DialogueSpeaker.Assistant -> Color(0xFFE8EAED)
                    DialogueSpeaker.System -> Color(0xFF9AA0A6)
                }
                val whoBodyColor = when (who) {
                    DialogueSpeaker.User -> Color(0xFFD2E3FC)
                    DialogueSpeaker.Assistant -> Color(0xFFF8F9FA)
                    DialogueSpeaker.System -> Color(0xFFBDC1C6)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = whoLabel,
                        color = whoLabelColor.copy(alpha = 0.85f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Text(
                        text = line,
                        color = whoBodyColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    )
                }
            }
        }
    }
}

/** Hotword → summon bridge shared with [VirtualAssistantActivity]. */
private val immersiveSummonHandlers = mutableListOf<() -> Unit>()

@Composable
private fun ImmersiveHotwordBridge(onSummon: () -> Unit) {
    DisposableEffect(onSummon) {
        immersiveSummonHandlers += onSummon
        onDispose { immersiveSummonHandlers -= onSummon }
    }
}

/** Called when hotword is detected; also notifies the legacy NOMI overlay handlers. */
fun notifyImmersiveAssistantHotword() {
    immersiveSummonHandlers.toList().forEach { it.invoke() }
    notifyAssistantHotword()
}

/**
 * Conversation script for the immersive eyes UI — listen → think → search → speak,
 * plus emotion beats (happy / sad / excited / bored / drowsy / tired).
 */
val ImmersiveDialogueScript: List<DialogueBeat> = listOf(
    DialogueBeat(
        speaker = DialogueSpeaker.System,
        text = "Listening…",
        mood = AssistantMood.Listening,
        holdMs = 1600,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.User,
        text = "Hey — find a coffee stop nearby",
        mood = AssistantMood.Listening,
        holdMs = 2600,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "On it — thinking through nearby options…",
        mood = AssistantMood.Thinking,
        holdMs = 2400,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Searching cafés along your route…",
        mood = AssistantMood.Searching,
        holdMs = 2600,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Bluebird Roasters is 6 minutes away. Want that stop?",
        mood = AssistantMood.Speaking,
        holdMs = 3000,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.User,
        text = "Yes!",
        mood = AssistantMood.Listening,
        holdMs = 1400,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Done — stop added. You're going to love their cold brew!",
        mood = AssistantMood.Happy,
        holdMs = 2800,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Oh wait — they close in ten minutes. Sorry about that.",
        mood = AssistantMood.Sad,
        holdMs = 2800,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Harbor Light is open late — I've got a better option!",
        mood = AssistantMood.Excited,
        holdMs = 2800,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.System,
        text = "Quiet stretch ahead…",
        mood = AssistantMood.Bored,
        holdMs = 2000,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.System,
        text = "Late night mode",
        mood = AssistantMood.Drowsy,
        holdMs = 2000,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "I'll keep watch while you drive. Rest when you can.",
        mood = AssistantMood.Tired,
        holdMs = 2800,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Route updated. Safe travels.",
        mood = AssistantMood.Speaking,
        holdMs = 2400,
    ),
)
