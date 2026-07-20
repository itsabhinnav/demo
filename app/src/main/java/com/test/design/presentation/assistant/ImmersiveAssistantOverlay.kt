package com.test.design.presentation.assistant

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.design.core.LocalDrivingUxState
import com.test.design.presentation.activityViewModel
import com.test.design.presentation.ivi.dashboard.components.floatingSystemChromePadding
import com.test.design.presentation.ivi.glanceables.DrivingStatusGlanceActivity
import com.test.design.presentation.ivi.vehicle.VehicleViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Blur radius applied to host UI under the assistant overlay (face/text stay sharp). */
val AssistantBackdropBlur = 36.dp

/**
 * Full-screen immersive assistant: blurred + translucent host content, centered ring face,
 * single transcript line, hairline edge glow. Fades in on hotword or tap.
 *
 * Features: gaze-to-speaker, STT streaming, TTS lip-sync, drive-context prompts,
 * cluster hand-off, OEM brand tint, high-contrast eyes, wake haptic/chime, nod/shake.
 */
@Composable
fun ImmersiveAssistantOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
    awaitHotword: Boolean = true,
    onRequestHotwordListen: (() -> Unit)? = null,
    script: List<DialogueBeat> = ImmersiveDialogueScript,
    enableLiveSpeech: Boolean = true,
    enableTts: Boolean = true,
) {
    val context = LocalContext.current
    val wake = rememberAssistantWakeFeedback()
    val drivingUx = LocalDrivingUxState.current
    val highContrast = LocalAssistantHighContrast.current
    val vehicleViewModel: VehicleViewModel = activityViewModel()
    val vehicleState by vehicleViewModel.state.collectAsStateWithLifecycle()
    val brandAccent = MaterialTheme.colorScheme.primary

    var visible by remember { mutableStateOf(!awaitHotword) }
    var session by remember { mutableStateOf(0) }
    var mood by remember { mutableStateOf(initialMood) }
    var transcript by remember { mutableStateOf("") }
    var speaker by remember { mutableStateOf(DialogueSpeaker.System) }
    var gazeX by remember { mutableStateOf<Float?>(-0.42f) }
    var gazeY by remember { mutableStateOf<Float?>(0.05f) }
    var mouthAmplitude by remember { mutableStateOf<Float?>(null) }
    var gesture by remember { mutableStateOf(FaceGesture.None) }
    var clusterHandOff by remember { mutableStateOf(false) }
    var liveSttActive by remember { mutableStateOf(false) }
    var sttBuffer by remember { mutableStateOf("") }

    fun summon() {
        session += 1
        mood = AssistantMood.Listening
        transcript = ""
        speaker = DialogueSpeaker.System
        gesture = FaceGesture.None
        mouthAmplitude = null
        clusterHandOff = false
        liveSttActive = false
        sttBuffer = ""
        val gaze = gazeForSpeaker(DialogueSpeaker.User)
        gazeX = gaze.first
        gazeY = gaze.second
        visible = true
    }

    ImmersiveHotwordBridge(onSummon = { summon() })

    // Live STT / RMS while the overlay is listening.
    LaunchedEffect(visible, session, enableLiveSpeech) {
        if (!visible || !enableLiveSpeech) return@LaunchedEffect
        assistantSpeechEvents(context).collectLatest { event ->
            if (!visible) return@collectLatest
            when (event) {
                AssistantSpeechEvent.Hotword -> Unit
                is AssistantSpeechEvent.Partial -> {
                    if (liveSttActive || speaker == DialogueSpeaker.User ||
                        mood == AssistantMood.Listening
                    ) {
                        liveSttActive = true
                        speaker = DialogueSpeaker.User
                        mood = AssistantMood.Listening
                        sttBuffer = event.text
                        transcript = event.text
                        val gaze = gazeForSpeaker(DialogueSpeaker.User)
                        gazeX = gaze.first
                        gazeY = gaze.second
                    }
                }
                is AssistantSpeechEvent.Final -> {
                    if (liveSttActive || mood == AssistantMood.Listening) {
                        liveSttActive = true
                        speaker = DialogueSpeaker.User
                        mood = AssistantMood.Listening
                        sttBuffer = event.text
                        transcript = event.text
                        gesture = faceGestureForText(event.text)
                    }
                }
                is AssistantSpeechEvent.Rms -> {
                    if (mood == AssistantMood.Listening) {
                        // Micro gaze toward louder mic energy (cabin left bias).
                        gazeX = -0.25f - event.normalized * 0.25f
                        gazeY = -0.02f + event.normalized * 0.04f
                    }
                }
            }
        }
    }

    // Drive conversation: context beats → script; TTS + lip-sync on assistant lines.
    LaunchedEffect(visible, session, drivingUx, vehicleState) {
        if (!visible) return@LaunchedEffect
        val contextBeats = buildDriveContextBeats(drivingUx, vehicleState)
        val sessionScript = contextBeats + script
        val handOff = shouldHandOffToCluster(drivingUx)

        for (beat in sessionScript) {
            if (!isActive || !visible) break

            // If live STT already captured a user line matching this beat, skip typed replay.
            if (beat.speaker == DialogueSpeaker.User &&
                liveSttActive &&
                sttBuffer.isNotBlank() &&
                sttBuffer.contains(beat.text.take(12), ignoreCase = true)
            ) {
                gesture = faceGestureForText(sttBuffer)
                delay(beat.holdMs.coerceAtMost(1200))
                gesture = FaceGesture.None
                continue
            }

            mood = beat.mood
            speaker = beat.speaker
            // Let Searching / Reading / Bored run built-in look loops; otherwise gaze speaker.
            if (beat.mood == AssistantMood.Searching ||
                beat.mood == AssistantMood.Reading ||
                beat.mood == AssistantMood.Bored
            ) {
                gazeX = null
                gazeY = null
            } else {
                val gaze = gazeForSpeaker(beat.speaker)
                gazeX = gaze.first
                gazeY = gaze.second
            }
            gesture = if (beat.speaker == DialogueSpeaker.User) {
                faceGestureForText(beat.text)
            } else {
                FaceGesture.None
            }

            if (beat.speaker == DialogueSpeaker.User && !liveSttActive) {
                // Simulated streaming STT — reveal text progressively.
                transcript = ""
                for (i in 1..beat.text.length) {
                    if (!isActive || !visible) break
                    transcript = beat.text.substring(0, i)
                    delay((beat.holdMs / beat.text.length.coerceAtLeast(1)).coerceIn(18L, 55L))
                }
                delay(200)
            } else if (shouldSpeakBeat(beat)) {
                transcript = beat.text
                // Speak every assistant line; mouth lip-sync tracks TTS when enabled.
                if (enableTts) {
                    assistantUtteranceLipSync(context, beat.text, beat.holdMs).collect { amp ->
                        mouthAmplitude = amp
                    }
                } else {
                    simulatedLipSync(beat.holdMs).collect { amp ->
                        mouthAmplitude = amp
                    }
                }
                mouthAmplitude = null
            } else {
                transcript = beat.text
                delay(beat.holdMs)
            }

            gesture = FaceGesture.None
        }

        if (visible && handOff) {
            clusterHandOff = true
            mood = AssistantMood.Idle
            speaker = DialogueSpeaker.System
            transcript = "Mirrored to cluster · tap to dismiss"
            runCatching {
                context.startActivity(
                    Intent(context, DrivingStatusGlanceActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                    ),
                )
            }
            delay(2200)
        }

        if (visible) {
            delay(500)
            // Exit animation + host teardown happen in the visibility effect below.
            visible = false
        }
    }

    val overlayAlpha = remember { Animatable(0f) }
    val faceRise = remember { Animatable(1f) } // 1 = below screen, 0 = settled
    val faceScale = remember { Animatable(0.88f) }
    val faceAlpha = remember { Animatable(0f) }
    val transcriptAlpha = remember { Animatable(0f) }
    // Avoid calling onDismiss on first composition when awaitHotword keeps us hidden.
    var hasPresented by remember { mutableStateOf(false) }

    LaunchedEffect(visible, session) {
        if (visible) {
            hasPresented = true
            wake.play()
            // Soft translucent stage in, then face rises from bottom.
            faceRise.snapTo(1f)
            faceScale.snapTo(0.86f)
            faceAlpha.snapTo(0f)
            transcriptAlpha.snapTo(0f)
            overlayAlpha.snapTo(0f)

            launch {
                overlayAlpha.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
            }
            delay(60)
            launch {
                faceAlpha.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
            }
            launch {
                faceScale.animateTo(
                    1f,
                    spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
                )
            }
            faceRise.animateTo(
                0f,
                spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
            )
            delay(120)
            transcriptAlpha.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
        } else if (hasPresented) {
            wake.playDismiss()
            launch {
                transcriptAlpha.animateTo(0f, tween(180))
            }
            launch {
                faceAlpha.animateTo(0f, tween(280, easing = FastOutSlowInEasing))
            }
            launch {
                faceRise.animateTo(
                    0.35f,
                    tween(320, easing = FastOutSlowInEasing),
                )
            }
            overlayAlpha.animateTo(0f, tween(360, easing = FastOutSlowInEasing))
            faceRise.snapTo(1f)
            faceScale.snapTo(0.88f)
            // Collapse host (clears Modifier.blur) after tap dismiss or session end.
            onDismiss()
        }
    }

    val brandGlow = rememberAssistantBrandGlow(mood, brandAccent).copy(alpha = 0.55f)
    val faceSize = if (clusterHandOff) 140.dp else 220.dp
    val showOverlay = visible || overlayAlpha.value > 0.02f

    Box(
        modifier = modifier
            .fillMaxSize()
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
        if (showOverlay) {
            // Translucent scrim + thin edge glow over blurred host UI.
            // Face + transcript stay in a sharp layer above (not blurred).
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = overlayAlpha.value.coerceIn(0f, 1f) },
            ) {
                ImmersiveBackdrop()
                ImmersiveBorderGlow(glowColor = brandGlow)
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    // Clear floating top/bottom system bars drawn above this overlay.
                    .floatingSystemChromePadding()
                    .padding(horizontal = 48.dp, vertical = 32.dp)
                    .graphicsLayer { alpha = overlayAlpha.value.coerceIn(0f, 1f) },
            ) {
                val density = LocalDensity.current
                val risePx = with(density) {
                    (maxHeight * 0.55f).toPx().coerceAtLeast(220.dp.toPx())
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (clusterHandOff) {
                        Text(
                            text = "Cluster hand-off",
                            color = brandGlow.copy(alpha = 0.95f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ImmersiveEyesFace(
                            mood = mood,
                            modifier = Modifier
                                .size(faceSize)
                                .offset {
                                    IntOffset(
                                        0,
                                        (faceRise.value * risePx).roundToInt(),
                                    )
                                }
                                .graphicsLayer {
                                    val s = faceScale.value
                                    scaleX = s
                                    scaleY = s
                                    alpha = faceAlpha.value.coerceIn(0f, 1f)
                                },
                            gazeX = gazeX,
                            gazeY = gazeY,
                            mouthAmplitude = mouthAmplitude,
                            brandGlow = brandGlow,
                            highContrast = highContrast,
                            gesture = gesture,
                        )
                    }

                    ImmersiveTranscript(
                        text = transcript,
                        speaker = speaker,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = transcriptAlpha.value.coerceIn(0f, 1f)
                            }
                            .padding(bottom = 36.dp),
                    )
                }
            }
        }
    }
}

/** Semi-transparent tint so blurred host content reads through. */
@Composable
private fun ImmersiveBackdrop() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xA610141C),
                        0.4f to Color(0xB80A0C10),
                        0.75f to Color(0xCC050608),
                        1.0f to Color(0xD9000000),
                    ),
                ),
            ),
    )
}

/** Hairline edge glow that soft-blends into the blurred backdrop. */
@Composable
private fun ImmersiveBorderGlow(glowColor: Color = Color(0xFF8AB4F8)) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val stroke = 1.dp.toPx()
        val inset = 6.dp.toPx()
        val w = size.width
        val h = size.height
        val blue = glowColor
        val edge = 28.dp.toPx()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    blue.copy(alpha = 0.04f),
                    Color.Transparent,
                    blue.copy(alpha = 0.05f),
                ),
            ),
            size = size,
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(blue.copy(alpha = 0.18f), Color.Transparent),
                startY = inset,
                endY = inset + edge,
            ),
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2f, edge),
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, blue.copy(alpha = 0.22f)),
                startY = h - inset - edge,
                endY = h - inset,
            ),
            topLeft = Offset(inset, h - inset - edge),
            size = Size(w - inset * 2f, edge),
        )
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(blue.copy(alpha = 0.14f), Color.Transparent),
                startX = inset,
                endX = inset + edge,
            ),
            topLeft = Offset(inset, inset),
            size = Size(edge, h - inset * 2f),
        )
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, blue.copy(alpha = 0.14f)),
                startX = w - inset - edge,
                endX = w - inset,
            ),
            topLeft = Offset(w - inset - edge, inset),
            size = Size(edge, h - inset * 2f),
        )
        drawRect(
            color = blue.copy(alpha = 0.16f),
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2f, stroke),
        )
        drawRect(
            color = blue.copy(alpha = 0.2f),
            topLeft = Offset(inset, h - inset - stroke),
            size = Size(w - inset * 2f, stroke),
        )
        drawRect(
            color = blue.copy(alpha = 0.12f),
            topLeft = Offset(inset, inset),
            size = Size(stroke, h - inset * 2f),
        )
        drawRect(
            color = blue.copy(alpha = 0.12f),
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
 * Calm single-persona conversation — listen → think → answer → confirm.
 * Moods stay near Listening / Thinking / Speaking so the face doesn't flip characters.
 */
val ImmersiveDialogueScript: List<DialogueBeat> = listOf(
    DialogueBeat(
        speaker = DialogueSpeaker.System,
        text = "Listening…",
        mood = AssistantMood.Listening,
        holdMs = 1400,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.User,
        text = "Hey — find a coffee stop nearby",
        mood = AssistantMood.Listening,
        holdMs = 2400,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Sure — looking for cafés along your route.",
        mood = AssistantMood.Thinking,
        holdMs = 2600,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Bluebird Roasters is 6 minutes away. Want that stop?",
        mood = AssistantMood.Speaking,
        holdMs = 3200,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.User,
        text = "Yes!",
        mood = AssistantMood.Listening,
        holdMs = 1200,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Done — stop added. You're all set.",
        mood = AssistantMood.Speaking,
        holdMs = 2800,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "They close in about ten minutes — Harbor Light stays open later if you prefer.",
        mood = AssistantMood.Speaking,
        holdMs = 3400,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Route updated. Safe travels.",
        mood = AssistantMood.Speaking,
        holdMs = 2400,
    ),
)
