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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.design.core.DrivingUxState
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

/**
 * Immersive assistant: starts as a non-blocking corner bubble while listening, then morphs
 * to a full-screen translucent stage (centered ring face + transcript) when ready to respond.
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
    onFeedback: (Boolean) -> Unit = {},
    onPresentationChanged: (AssistantPresentation) -> Unit = {},
    onBubbleBoundsInRoot: ((left: Int, top: Int, right: Int, bottom: Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val wake = rememberAssistantWakeFeedback()
    val drivingUx = LocalDrivingUxState.current
    val highContrast = LocalAssistantHighContrast.current
    val vehicleViewModel: VehicleViewModel = activityViewModel()
    val vehicleState by vehicleViewModel.state.collectAsStateWithLifecycle()
    val brandAccent = MaterialTheme.colorScheme.primary

    var visible by remember { mutableStateOf(!awaitHotword) }
    var session by remember { mutableIntStateOf(if (!awaitHotword) 1 else 0) }
    var presentation by remember { mutableStateOf(AssistantPresentation.Compact) }
    var mood by remember {
        mutableStateOf(if (!awaitHotword) AssistantMood.Listening else initialMood)
    }
    var transcript by remember { mutableStateOf("") }
    var speaker by remember { mutableStateOf(DialogueSpeaker.System) }
    var gazeX by remember { mutableStateOf<Float?>(-0.42f) }
    var gazeY by remember { mutableStateOf<Float?>(0.05f) }
    var mouthAmplitude by remember { mutableStateOf<Float?>(null) }
    var gesture by remember { mutableStateOf(FaceGesture.None) }
    var clusterHandOff by remember { mutableStateOf(false) }
    var liveSttActive by remember { mutableStateOf(false) }
    var sttBuffer by remember { mutableStateOf("") }
    var weatherAmbient by remember { mutableStateOf<WeatherAmbientKind?>(null) }
    var showThumbs by remember { mutableStateOf(false) }
    var thumbsTick by remember { mutableIntStateOf(0) }

    fun summon() {
        session += 1
        presentation = AssistantPresentation.Compact
        mood = AssistantMood.Listening
        transcript = ""
        speaker = DialogueSpeaker.System
        gesture = FaceGesture.None
        mouthAmplitude = null
        clusterHandOff = false
        liveSttActive = false
        sttBuffer = ""
        weatherAmbient = null
        showThumbs = false
        val gaze = gazeForSpeaker(DialogueSpeaker.User)
        gazeX = gaze.first
        gazeY = gaze.second
        visible = true
    }

    LaunchedEffect(presentation, visible) {
        if (visible) {
            onPresentationChanged(presentation)
        } else {
            onPresentationChanged(AssistantPresentation.Compact)
        }
    }

    ImmersiveHotwordBridge(onSummon = { summon() })

    // Live STT / RMS while the overlay is listening.
    LaunchedEffect(visible, session, enableLiveSpeech) {
        if (!visible || !enableLiveSpeech) return@LaunchedEffect
        assistantSpeechEvents(context).collectLatest { event ->
            if (!visible) return@collectLatest
            when (event) {
                AssistantSpeechEvent.Hotword -> {
                    if (!visible) summon()
                }
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
                        sttBuffer = event.text
                        transcript = event.text
                        gesture = faceGestureForText(event.text)
                        // Keyword fatigue → Drowsy/Tired mood sink (sensor-ready).
                        mood = fatigueMoodForText(event.text) ?: AssistantMood.Listening
                        weatherAmbientForText(event.text)?.let { weatherAmbient = it }
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

    // Drive conversation once per summon — do not re-key on drivingUx / vehicleState
    // or mid-session vehicle ticks restart the script in an endless loop.
    LaunchedEffect(visible, session) {
        if (!visible) return@LaunchedEffect
        val contextBeats = buildDriveContextBeats(drivingUx, vehicleState)
        val sessionScript = contextBeats + script
        val handOff = shouldHandOffToCluster(drivingUx)

        for (beat in sessionScript) {
            if (!isActive || !visible) break

            if (shouldExpandToImmersive(beat) &&
                presentation != AssistantPresentation.Immersive
            ) {
                presentation = AssistantPresentation.Immersive
                // Let the morph settle before speaking / showing immersive chrome.
                delay(420)
            }

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

            // Hide thumbs when the driver speaks again or we re-enter listening.
            if (beat.speaker == DialogueSpeaker.User ||
                beat.mood == AssistantMood.Listening
            ) {
                showThumbs = false
            }
            mood = beat.mood
            speaker = beat.speaker
            // Weather ambient from beat tag, or keyword on user lines.
            when {
                beat.weatherAmbient != null -> weatherAmbient = beat.weatherAmbient
                beat.speaker == DialogueSpeaker.User ->
                    weatherAmbientForText(beat.text)?.let { weatherAmbient = it }
            }
            // Keyword fatigue on scripted user lines (demo path).
            if (beat.speaker == DialogueSpeaker.User) {
                fatigueMoodForText(beat.text)?.let { mood = it }
            }
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
                // Post-answer thumbs — hidden in Restricted glance budget.
                if (isAnswerMood(beat.mood) && drivingUx != DrivingUxState.Restricted) {
                    showThumbs = true
                    thumbsTick += 1
                }
            } else {
                transcript = beat.text
                delay(beat.holdMs)
            }

            // Fade weather after the spoken weather answer settles.
            if (beat.weatherAmbient != null && isAnswerMood(beat.mood)) {
                delay(400)
                weatherAmbient = null
            }

            gesture = FaceGesture.None
        }

        if (visible && handOff) {
            clusterHandOff = true
            mood = AssistantMood.Idle
            speaker = DialogueSpeaker.System
            transcript = "Mirrored to cluster · tap to dismiss"
            showThumbs = false
            weatherAmbient = null
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
            // One demo cycle complete — dismiss (no auto-replay).
            visible = false
        }
    }

    // Auto-dismiss thumbs after ~4s or when listening resumes.
    LaunchedEffect(showThumbs, thumbsTick, mood) {
        if (!showThumbs) return@LaunchedEffect
        if (mood == AssistantMood.Listening) {
            showThumbs = false
            return@LaunchedEffect
        }
        delay(4_000)
        showThumbs = false
    }

    // Clear nod after thumbs-up feedback.
    LaunchedEffect(gesture) {
        if (gesture == FaceGesture.Nod || gesture == FaceGesture.Shake) {
            delay(700)
            if (gesture == FaceGesture.Nod || gesture == FaceGesture.Shake) {
                gesture = FaceGesture.None
            }
        }
    }

    val overlayAlpha = remember { Animatable(0f) }
    val faceRise = remember { Animatable(1f) } // 1 = below screen, 0 = settled
    val faceScale = remember { Animatable(0.88f) }
    val faceAlpha = remember { Animatable(0f) }
    val transcriptAlpha = remember { Animatable(0f) }
    // Avoid calling onDismiss on first composition when awaitHotword keeps us hidden.
    var hasPresented by remember { mutableStateOf(false) }
    var immersiveEnteredSession by remember { mutableIntStateOf(-1) }

    LaunchedEffect(visible, session) {
        if (visible) {
            hasPresented = true
            wake.play()
            overlayAlpha.snapTo(0f)
            overlayAlpha.animateTo(1f, tween(320, easing = FastOutSlowInEasing))
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
            faceAlpha.snapTo(0f)
            transcriptAlpha.snapTo(0f)
            immersiveEnteredSession = -1
            // Collapse host (clears Modifier.blur) after tap dismiss or session end.
            onDismiss()
        }
    }

    // Immersive face entrance — once per session when expanding from the corner bubble.
    LaunchedEffect(visible, session, presentation) {
        if (!visible || presentation != AssistantPresentation.Immersive) return@LaunchedEffect
        if (immersiveEnteredSession == session) return@LaunchedEffect
        immersiveEnteredSession = session
        faceRise.snapTo(1f)
        faceScale.snapTo(0.86f)
        faceAlpha.snapTo(0f)
        transcriptAlpha.snapTo(0f)
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
    }

    val brandGlow = rememberAssistantBrandGlow(mood, brandAccent).copy(alpha = 0.55f)
    val showOverlay = visible || overlayAlpha.value > 0.02f
    val isImmersive = presentation == AssistantPresentation.Immersive
    val bubblePrompt = when {
        transcript.isNotBlank() -> transcript
        mood == AssistantMood.Listening -> "Listening…"
        else -> "How can I help?"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (!visible) {
                    // Hidden / awaiting hotword — tap anywhere to summon.
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            onRequestHotwordListen?.invoke()
                            summon()
                        },
                    )
                } else if (isImmersive) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { visible = false },
                    )
                } else {
                    // Compact: pass-through — only the bubble handles taps.
                    Modifier
                },
            ),
    ) {
        if (showOverlay) {
            AnimatedContent(
                targetState = presentation,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = overlayAlpha.value.coerceIn(0f, 1f) },
                transitionSpec = {
                    (
                        fadeIn(tween(420, easing = FastOutSlowInEasing)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(460, easing = FastOutSlowInEasing),
                            )
                        ) togetherWith (
                        fadeOut(tween(280, easing = FastOutSlowInEasing)) +
                            scaleOut(
                                targetScale = 0.94f,
                                animationSpec = tween(280, easing = FastOutSlowInEasing),
                            )
                        )
                },
                label = "assistant_presentation",
            ) { phase ->
                when (phase) {
                    AssistantPresentation.Compact -> {
                        AssistantCornerBubble(
                            mood = mood,
                            prompt = bubblePrompt,
                            brandGlow = brandGlow,
                            modifier = Modifier.fillMaxSize(),
                            onClick = { visible = false },
                            onBoundsInRoot = onBubbleBoundsInRoot,
                        )
                    }
                    AssistantPresentation.Immersive -> {
                        Box(Modifier.fillMaxSize()) {
                            // Transparent top → very dark bottom; glow only along bottom edge.
                            Box(Modifier.fillMaxSize()) {
                                ImmersiveBackdrop()
                                WeatherAmbientOverlay(
                                    kind = weatherAmbient,
                                    modifier = Modifier.fillMaxSize(),
                                    tint = Color(0xFFB3E5FC),
                                )
                                ImmersiveBorderGlow(glowColor = brandGlow)
                            }

                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .windowInsetsPadding(WindowInsets.safeDrawing)
                                    .floatingSystemChromePadding()
                                    .padding(horizontal = 32.dp, vertical = 16.dp),
                            ) {
                                // Assistant chrome occupies ~1/4 of available height at the bottom.
                                val bandHeight = maxHeight * 0.25f
                                val faceSize = (bandHeight * 0.55f).coerceIn(72.dp, 120.dp)
                                val density = LocalDensity.current
                                val risePx = with(density) {
                                    (faceSize * 0.35f).toPx()
                                }

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(bandHeight),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
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
                                    ImmersiveTranscript(
                                        text = transcript,
                                        speaker = speaker,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .graphicsLayer {
                                                alpha = transcriptAlpha.value.coerceIn(0f, 1f)
                                            }
                                            .padding(top = 28.dp, bottom = 12.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Vertical stage: transparent top → dark bottom, with a deeper pool behind face/text. */
@Composable
fun ImmersiveBackdrop(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x00000000),
                            0.48f to Color(0x00000000),
                            0.66f to Color(0x6610141C),
                            0.80f to Color(0xCC0A0C10),
                            1.0f to Color(0xF2050608),
                        ),
                    ),
                ),
        )
        // Extra darken where face + transcript sit (bottom-center).
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xF2000000),
                        0.40f to Color(0xB3000000),
                        0.72f to Color(0x66000000),
                        1.0f to Color.Transparent,
                    ),
                    center = Offset(w * 0.5f, h * 0.86f),
                    radius = minOf(w * 0.48f, h * 0.42f),
                ),
            )
        }
    }
}

/**
 * Soft bottom bloom only — no hard edge.
 * Peaks at bottom-center, fades to 0 left/right and upward.
 */
@Composable
fun ImmersiveBorderGlow(
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF8AB4F8),
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val blue = glowColor
        // Tall soft oval sitting on the bottom edge so the hard diameter is off-screen.
        val glowW = w * 0.72f
        val glowH = 56.dp.toPx()
        drawOval(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to blue.copy(alpha = 0.45f),
                    0.35f to blue.copy(alpha = 0.18f),
                    0.70f to blue.copy(alpha = 0.05f),
                    1.0f to Color.Transparent,
                ),
                center = Offset(cx, h),
                radius = glowW * 0.55f,
            ),
            topLeft = Offset(cx - glowW * 0.5f, h - glowH),
            size = Size(glowW, glowH * 2f),
        )
        // Wider, fainter halo for side fade.
        drawOval(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to blue.copy(alpha = 0.16f),
                    0.55f to blue.copy(alpha = 0.04f),
                    1.0f to Color.Transparent,
                ),
                center = Offset(cx, h + 4.dp.toPx()),
                radius = w * 0.42f,
            ),
            topLeft = Offset(w * 0.05f, h - glowH * 1.35f),
            size = Size(w * 0.90f, glowH * 2.4f),
        )
    }
}

@Composable
private fun ImmersiveTranscript(
    text: String,
    speaker: DialogueSpeaker,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = text to speaker,
        transitionSpec = {
            (fadeIn(tween(280)) + slideInVertically(tween(320)) { it / 4 }) togetherWith
                (fadeOut(tween(180)) + slideOutVertically(tween(220)) { -it / 5 })
        },
        label = "immersive_transcript",
        modifier = modifier,
    ) { (line, who) ->
        if (line.isBlank()) {
            Box(modifier = Modifier.height(28.dp))
        } else {
            val bodyColor = when (who) {
                DialogueSpeaker.User -> Color(0xFFD2E3FC)
                DialogueSpeaker.Assistant -> Color(0xFFF8F9FA)
                DialogueSpeaker.System -> Color(0xFFBDC1C6)
            }
            Text(
                text = line,
                color = bodyColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
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
 * Full emotion walk — listening → think → read → search → speak,
 * weather snow/rain ambience, happy / sad / excited / bored / drowsy / tired.
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
        text = "Reading reviews for Bluebird Roasters…",
        mood = AssistantMood.Reading,
        holdMs = 2400,
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
        speaker = DialogueSpeaker.User,
        text = "Will it snow tonight?",
        mood = AssistantMood.Listening,
        holdMs = 2200,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Thinking through the overnight forecast…",
        mood = AssistantMood.Thinking,
        holdMs = 2000,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Reading the radar along your route…",
        mood = AssistantMood.Reading,
        holdMs = 2200,
        weatherAmbient = WeatherAmbientKind.Snow,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "Light snow after midnight — roads should stay clear until then.",
        mood = AssistantMood.Speaking,
        holdMs = 3200,
        weatherAmbient = WeatherAmbientKind.Snow,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.User,
        text = "And will it rain tomorrow?",
        mood = AssistantMood.Listening,
        holdMs = 2000,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.Assistant,
        text = "A soft drizzle around midday — nothing heavy.",
        mood = AssistantMood.Speaking,
        holdMs = 2800,
        weatherAmbient = WeatherAmbientKind.Rain,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.System,
        text = "Quiet stretch ahead…",
        mood = AssistantMood.Bored,
        holdMs = 2000,
    ),
    DialogueBeat(
        speaker = DialogueSpeaker.User,
        text = "I'm feeling a bit tired",
        mood = AssistantMood.Listening,
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
