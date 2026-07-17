package com.test.design.presentation.assistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Continuous-ish listening for “hey assistant” (and close variants).
 * Emits Unit each time a hotword is detected.
 */
internal fun hotwordDetections(context: Context): Flow<Unit> = callbackFlow {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        awaitClose { }
        return@callbackFlow
    }

    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    var listening = true

    fun startListening() {
        if (!listening) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }
        try {
            recognizer.startListening(intent)
        } catch (_: Exception) {
            // Mic / service unavailable — stay silent; UI has tap fallback.
        }
    }

    fun matchesHotword(text: String): Boolean {
        val t = text.lowercase()
            .replace(',', ' ')
            .replace('.', ' ')
            .replace('!', ' ')
            .replace('?', ' ')
            .trim()
        return t.contains("hey assistant") ||
            t.contains("hey assistent") ||
            t.contains("hi assistant") ||
            t.contains("ok assistant") ||
            t.contains("okay assistant") ||
            t == "assistant" ||
            t.startsWith("hey assist")
    }

    fun onHeard(texts: List<String>?) {
        if (texts.isNullOrEmpty()) return
        if (texts.any { matchesHotword(it) }) {
            trySend(Unit)
        }
    }

    recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit

        override fun onError(error: Int) {
            // Restart after brief pause so we keep listening for the hotword.
            if (listening) {
                recognizer.cancel()
                startListening()
            }
        }

        override fun onResults(results: Bundle?) {
            val texts = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            onHeard(texts)
            if (listening) startListening()
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val texts = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            onHeard(texts)
        }
    })

    startListening()

    awaitClose {
        listening = false
        try {
            recognizer.cancel()
            recognizer.destroy()
        } catch (_: Exception) {
        }
    }
}
