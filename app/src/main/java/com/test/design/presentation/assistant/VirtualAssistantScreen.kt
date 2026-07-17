package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Transparent NOMI overlay — no panel, no text.
 * Orb appears on hotword (or tap) with a random entrance.
 */
@Composable
fun VirtualAssistantOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialMood: AssistantMood = AssistantMood.Idle,
    /** When true, starts listening / waiting for hotword instead of showing immediately. */
    awaitHotword: Boolean = true,
    onRequestHotwordListen: (() -> Unit)? = null,
) {
    var visible by remember { mutableStateOf(!awaitHotword) }
    var entrance by remember { mutableStateOf(OrbEntrance.random()) }
    var mood by rememberSaveable { mutableStateOf(initialMood) }
    var session by remember { mutableStateOf(0) }

    fun summon(nextMood: AssistantMood = randomSummonMood()) {
        entrance = OrbEntrance.random()
        mood = nextMood
        session += 1
        visible = true
    }

    // Auto-cycle expressions while visible
    LaunchedEffect(visible, session) {
        if (!visible) return@LaunchedEffect
        delay(900)
        while (isActive && visible) {
            mood = randomSummonMood()
            delay(Random.nextLong(1600, 2800))
        }
    }

    // Auto-dismiss after a beat so it feels alive, not sticky
    LaunchedEffect(visible, session) {
        if (!visible) return@LaunchedEffect
        delay(Random.nextLong(5200, 7800))
        visible = false
        delay(420)
        onDismiss()
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
                        // Tap fallback when mic / hotword isn't available
                        onRequestHotwordListen?.invoke()
                        summon()
                    }
                },
            ),
    ) {
        // Expose summon for Activity hotword callbacks via composition local-less pattern:
        // Activity drives [awaitHotword] false after detection by recreating — instead we
        // use a remembered callback holder.
        HotwordSummonBridge(onSummon = { summon() })

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(280)),
            modifier = Modifier.fillMaxSize(),
        ) {
            NomiOrbStage(
                mood = mood,
                entrance = entrance,
                session = session,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Allows [VirtualAssistantActivity] to summon without recomposing the whole tree awkwardly. */
private val summonHandlers = mutableListOf<() -> Unit>()

@Composable
private fun HotwordSummonBridge(onSummon: () -> Unit) {
    DisposableEffect(onSummon) {
        summonHandlers += onSummon
        onDispose { summonHandlers -= onSummon }
    }
}

/** Called from the Activity when a hotword is detected. */
fun notifyAssistantHotword() {
    summonHandlers.toList().forEach { it.invoke() }
}

private fun randomSummonMood(): AssistantMood = listOf(
    AssistantMood.Idle,
    AssistantMood.Listening,
    AssistantMood.Happy,
    AssistantMood.Speaking,
    AssistantMood.Thinking,
    AssistantMood.Searching,
).random()

@Composable
private fun NomiOrbStage(
    mood: AssistantMood,
    entrance: OrbEntrance,
    session: Int,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val orbDp = 220.dp
        val orbPx = with(density) { orbDp.toPx() }
        val w = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val h = constraints.maxHeight.toFloat().coerceAtLeast(1f)

        val offsetX = remember { Animatable(0f) }
        val offsetY = remember { Animatable(0f) }
        val scaleX = remember { Animatable(0.6f) }
        val scaleY = remember { Animatable(0.6f) }
        val alpha = remember { Animatable(0f) }
        val rot = remember { Animatable(0f) }
        var auraOn by remember { mutableStateOf(false) }

        LaunchedEffect(session, entrance, w, h, orbPx) {
            val restX = (w - orbPx) * 0.5f
            val restY = (h - orbPx) * 0.38f
            val springy = spring<Float>(
                dampingRatio = 0.55f,
                stiffness = Spring.StiffnessMediumLow,
            )
            val soft = tween<Float>(480, easing = FastOutSlowInEasing)
            auraOn = false

            suspend fun settle() {
                scaleX.animateTo(1f, springy)
                scaleY.animateTo(1f, springy)
                rot.animateTo(0f, soft)
                auraOn = true
            }

            when (entrance) {
                OrbEntrance.PeekBottom -> {
                    offsetX.snapTo(restX)
                    offsetY.snapTo(h - orbPx * 0.22f)
                    scaleX.snapTo(1.08f)
                    scaleY.snapTo(0.82f)
                    rot.snapTo(0f)
                    alpha.snapTo(1f)
                    delay(180)
                    offsetY.animateTo(h - orbPx * 0.68f, soft)
                    scaleX.animateTo(0.94f, soft)
                    scaleY.animateTo(1.06f, soft)
                    delay(380)
                    offsetY.animateTo(restY, springy)
                    settle()
                }
                OrbEntrance.PeekLeft -> {
                    offsetX.snapTo(-orbPx * 0.62f)
                    offsetY.snapTo(restY)
                    scaleX.snapTo(0.78f)
                    scaleY.snapTo(1.1f)
                    rot.snapTo(-12f)
                    alpha.snapTo(1f)
                    delay(160)
                    offsetX.animateTo(orbPx * 0.04f, soft)
                    delay(320)
                    offsetX.animateTo(restX, springy)
                    settle()
                }
                OrbEntrance.PeekRight -> {
                    offsetX.snapTo(w - orbPx * 0.38f)
                    offsetY.snapTo(restY)
                    scaleX.snapTo(0.78f)
                    scaleY.snapTo(1.1f)
                    rot.snapTo(12f)
                    alpha.snapTo(1f)
                    delay(160)
                    offsetX.animateTo(w - orbPx * 1.02f, soft)
                    delay(320)
                    offsetX.animateTo(restX, springy)
                    settle()
                }
                OrbEntrance.PeekTop -> {
                    offsetX.snapTo(restX)
                    offsetY.snapTo(-orbPx * 0.62f)
                    scaleX.snapTo(1.06f)
                    scaleY.snapTo(0.84f)
                    rot.snapTo(0f)
                    alpha.snapTo(1f)
                    delay(160)
                    offsetY.animateTo(orbPx * 0.02f, soft)
                    delay(320)
                    offsetY.animateTo(restY, springy)
                    settle()
                }
                OrbEntrance.Fall -> {
                    offsetX.snapTo(restX + Random.nextFloat() * 100f - 50f)
                    offsetY.snapTo(-orbPx * 1.4f)
                    scaleX.snapTo(0.88f)
                    scaleY.snapTo(1.12f)
                    rot.snapTo(Random.nextFloat() * 24f - 12f)
                    alpha.snapTo(1f)
                    offsetY.animateTo(restY + 48f, tween(520, easing = FastOutSlowInEasing))
                    scaleX.snapTo(1.18f)
                    scaleY.snapTo(0.78f)
                    offsetY.animateTo(restY - 22f, springy)
                    offsetY.animateTo(restY, springy)
                    offsetX.animateTo(restX, soft)
                    settle()
                }
                OrbEntrance.Bounce -> {
                    offsetX.snapTo(restX)
                    offsetY.snapTo(h + orbPx * 0.2f)
                    scaleX.snapTo(0.75f)
                    scaleY.snapTo(0.75f)
                    rot.snapTo(0f)
                    alpha.snapTo(1f)
                    offsetY.animateTo(restY - 56f, tween(400))
                    scaleX.animateTo(0.9f, soft)
                    scaleY.animateTo(1.12f, soft)
                    offsetY.animateTo(restY + 28f, springy)
                    scaleX.snapTo(1.16f)
                    scaleY.snapTo(0.8f)
                    offsetY.animateTo(restY - 12f, springy)
                    offsetY.animateTo(restY, springy)
                    offsetX.animateTo(restX + 56f, soft)
                    offsetX.animateTo(restX - 32f, soft)
                    offsetX.animateTo(restX, springy)
                    settle()
                }
                OrbEntrance.Pop -> {
                    offsetX.snapTo(restX)
                    offsetY.snapTo(restY)
                    scaleX.snapTo(0.12f)
                    scaleY.snapTo(0.12f)
                    rot.snapTo(-8f)
                    alpha.snapTo(0f)
                    alpha.animateTo(1f, tween(140))
                    scaleX.animateTo(1.22f, springy)
                    scaleY.animateTo(1.22f, springy)
                    rot.animateTo(6f, soft)
                    settle()
                }
            }

            // Alive idle: gentle drift + occasional hop
            while (isActive) {
                val nx = restX + Random.nextFloat() * 72f - 36f
                val ny = restY + Random.nextFloat() * 48f - 24f
                offsetX.animateTo(nx, tween(1600, easing = FastOutSlowInEasing))
                offsetY.animateTo(ny, tween(1600, easing = FastOutSlowInEasing))
                if (Random.nextFloat() < 0.28f) {
                    scaleY.animateTo(0.9f, tween(90))
                    scaleX.animateTo(1.08f, tween(90))
                    offsetY.animateTo(ny - 28f, springy)
                    scaleX.animateTo(1f, springy)
                    scaleY.animateTo(1f, springy)
                    offsetY.animateTo(ny, springy)
                }
                delay(Random.nextLong(180, 520))
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .size(orbDp)
                .graphicsLayer {
                    this.scaleX = scaleX.value
                    this.scaleY = scaleY.value
                    this.alpha = alpha.value
                    rotationZ = rot.value
                    shadowElevation = 18f
                },
        ) {
            NomiArrivalAura(
                mood = mood,
                active = auraOn,
                modifier = Modifier.fillMaxSize(),
            )
            AssistantFace(
                mood = mood,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
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
        awaitHotword = false,
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
        awaitHotword = false,
    )
}

@Composable
fun VirtualAssistantStage(
    mood: AssistantMood,
    onMoodChange: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AssistantFace(mood = mood, modifier = Modifier.size(168.dp))
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
                    text = "Say “Hey assistant”",
                    style = MaterialTheme.typography.bodySmall,
                    color = AssistantTokens.OnSurfaceMuted,
                )
            }
        }
    }
}
