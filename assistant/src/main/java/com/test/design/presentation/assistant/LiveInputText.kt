package com.test.design.presentation.assistant

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Tokenize [text] into reveal units for Google Assistant Live–style streaming.
 * Whitespace stays attached to the following word so layout doesn't jump.
 */
internal fun liveInputTokens(text: String): List<String> {
    if (text.isEmpty()) return emptyList()
    val out = ArrayList<String>()
    val matcher = Regex("""\s*\S+""").findAll(text)
    var cursor = 0
    for (match in matcher) {
        if (match.range.first > cursor) {
            out += text.substring(cursor, match.range.first)
        }
        out += match.value
        cursor = match.range.last + 1
    }
    if (cursor < text.length) {
        out += text.substring(cursor)
    }
    return out
}

/** Shared leading token count between two token lists. */
internal fun liveInputSharedPrefixCount(previous: List<String>, next: List<String>): Int {
    val limit = minOf(previous.size, next.size)
    var i = 0
    while (i < limit && previous[i] == next[i]) i++
    return i
}

/**
 * Live-input transcript: stable prefix, soft fade on new words, optional
 * breathing shimmer on the trailing token while speech is still forming —
 * the same feel as Google Assistant / Gemini Live captions.
 */
@Composable
fun LiveInputText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    live: Boolean = false,
    fontSize: TextUnit = 26.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = 1,
) {
    val tokens = remember(text) { liveInputTokens(text) }
    var settledCount by remember { mutableIntStateOf(0) }
    var committedText by remember { mutableStateOf("") }
    val incomingAlpha = remember { Animatable(1f) }

    LaunchedEffect(text) {
        val nextTokens = liveInputTokens(text)
        val prevTokens = liveInputTokens(committedText)
        val shared = liveInputSharedPrefixCount(prevTokens, nextTokens)

        when {
            text.isBlank() -> {
                settledCount = 0
                committedText = ""
                incomingAlpha.snapTo(1f)
            }

            // Prefix-stable growth (live STT partials) — fade only new tokens.
            shared == prevTokens.size && nextTokens.size >= prevTokens.size &&
                committedText.isNotEmpty() -> {
                var i = shared
                settledCount = shared
                while (i < nextTokens.size) {
                    incomingAlpha.snapTo(0.14f)
                    settledCount = i
                    incomingAlpha.animateTo(
                        1f,
                        tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    )
                    i += 1
                    settledCount = i
                    committedText = nextTokens.take(i).joinToString("")
                }
                committedText = text
            }

            // Mid-utterance correction that keeps a shared stem.
            shared > 0 && committedText.isNotEmpty() -> {
                settledCount = shared
                committedText = nextTokens.take(shared).joinToString("")
                var i = shared
                while (i < nextTokens.size) {
                    incomingAlpha.snapTo(0.14f)
                    settledCount = i
                    incomingAlpha.animateTo(
                        1f,
                        tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    )
                    i += 1
                    settledCount = i
                    committedText = nextTokens.take(i).joinToString("")
                }
                committedText = text
            }

            // Fresh line — soft word cascade like Live replies.
            else -> {
                settledCount = 0
                committedText = ""
                incomingAlpha.snapTo(0.1f)
                if (nextTokens.isEmpty()) {
                    incomingAlpha.snapTo(1f)
                } else {
                    for (i in nextTokens.indices) {
                        incomingAlpha.snapTo(0.12f)
                        settledCount = i
                        incomingAlpha.animateTo(
                            1f,
                            tween(durationMillis = 170, easing = FastOutSlowInEasing),
                        )
                        settledCount = i + 1
                        committedText = nextTokens.take(i + 1).joinToString("")
                        if (i < nextTokens.lastIndex) delay(36)
                    }
                }
                committedText = text
            }
        }
    }

    val shimmer = if (live && tokens.isNotEmpty()) {
        val transition = rememberInfiniteTransition(label = "live_input_shimmer")
        val pulse by transition.animateFloat(
            initialValue = 0.55f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(880, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "live_trailing_pulse",
        )
        pulse
    } else {
        1f
    }

    if (text.isBlank()) {
        Box(modifier = modifier.height(34.dp))
        return
    }

    val annotated = buildAnnotatedString {
        tokens.forEachIndexed { index, token ->
            val base = when {
                index < settledCount -> 1f
                index == settledCount -> incomingAlpha.value
                else -> 0f
            }
            val trailing =
                if (live && index == tokens.lastIndex && index < settledCount) shimmer else 1f
            val a = (base * trailing).coerceIn(0f, 1f)
            withStyle(SpanStyle(color = color.copy(alpha = color.alpha * a))) {
                append(token)
            }
        }
    }

    Text(
        text = annotated,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        softWrap = maxLines > 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.fillMaxWidth(),
    )
}
