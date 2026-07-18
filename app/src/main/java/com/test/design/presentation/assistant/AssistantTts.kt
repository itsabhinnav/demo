package com.test.design.presentation.assistant

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume
import kotlin.math.sin
import kotlin.random.Random

/**
 * One-shot assistant utterance: plays TTS when available and emits lip-sync
 * amplitude (0..1) for [holdMs] so the mouth stays in sync with on-screen text.
 */
internal fun assistantUtteranceLipSync(
    context: Context,
    text: String,
    holdMs: Long,
): Flow<Float> = flow {
    val engine = acquireTts(context)
    val utteranceId = UUID.randomUUID().toString()
    var speaking = true

    engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            speaking = true
        }

        override fun onDone(utteranceId: String?) {
            speaking = false
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
            speaking = false
        }

        override fun onError(utteranceId: String?, errorCode: Int) {
            speaking = false
        }
    })

    try {
        engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    } catch (_: Exception) {
        speaking = false
    }

    val steps = (holdMs / 70L).coerceAtLeast(4)
    repeat(steps.toInt()) { i ->
        val t = i * 0.4f
        // Richer envelope while TTS reports speaking; quieter trail-off otherwise.
        val base = if (speaking) 0.55f else 0.22f
        val envelope = base + 0.35f * sin(t.toDouble()).toFloat() + Random.nextFloat() * 0.12f
        emit(envelope.coerceIn(0.05f, 1f))
        delay(70)
    }
    emit(0f)

    try {
        engine?.stop()
    } catch (_: Exception) {
    }
}

/** Simulated lip-sync only (no TTS) — useful for unit tests / silent demos. */
internal fun simulatedLipSync(holdMs: Long = 2400L): Flow<Float> = flow {
    val steps = (holdMs / 70L).coerceAtLeast(4)
    repeat(steps.toInt()) { i ->
        val t = i * 0.35f
        val envelope = 0.55f + 0.35f * sin(t.toDouble()).toFloat() + Random.nextFloat() * 0.1f
        emit(envelope.coerceIn(0.1f, 1f))
        delay(70)
    }
    emit(0f)
}

private val sharedTts = AtomicReference<TextToSpeech?>(null)

private suspend fun acquireTts(context: Context): TextToSpeech? {
    sharedTts.get()?.let { return it }
    return suspendCancellableCoroutine { cont ->
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.getDefault()
                sharedTts.compareAndSet(null, engine)
                if (cont.isActive) cont.resume(engine)
            } else if (cont.isActive) {
                cont.resume(null)
            }
        }
        cont.invokeOnCancellation {
            // Keep shared engine alive across utterances.
        }
    }
}
