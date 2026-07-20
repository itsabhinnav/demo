package com.test.design.presentation.assistant

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * On-device spoken fallback when no TextToSpeech engine is installed (common on AOSP AAOS).
 * Soft formant-ish voice over STREAM_MUSIC — same path as the wake chime.
 */
internal fun synthesizeAssistantSpeech(
    text: String,
    holdMs: Long,
): Flow<Float> = flow {
    val sampleRate = 22_050
    val syllables = tokenizeSyllables(text)
    if (syllables.isEmpty()) {
        delay(holdMs.coerceAtLeast(400L))
        emit(0f)
        return@flow
    }

    val minMs = (syllables.size * 140L).coerceAtLeast(800L)
    val totalMs = holdMs.coerceAtLeast(minMs)
    val perSyllableMs = (totalMs / syllables.size).coerceIn(110L, 280L)

    val attrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()
    val format = AudioFormat.Builder()
        .setSampleRate(sampleRate)
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
        .build()
    val minBuf = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
    ).coerceAtLeast(sampleRate / 10)

    val track = AudioTrack.Builder()
        .setAudioAttributes(attrs)
        .setAudioFormat(format)
        .setBufferSizeInBytes(minBuf * 2)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .setSessionId(0)
        .build()

    try {
        track.play()
        var phase1 = 0.0
        var phase2 = 0.0
        var phase3 = 0.0
        syllables.forEachIndexed { index, syllable ->
            if (!coroutineContext.isActive) return@forEachIndexed
            val voiced = syllable.any { it.isLetterOrDigit() }
            val f0 = 175.0 + (index % 5) * 4.5 + Random.nextDouble(-3.0, 3.0)
            val formantA = if (voiced) 520.0 + (syllable.hashCode() % 7) * 18.0 else 0.0
            val formantB = if (voiced) 1450.0 + (syllable.length % 5) * 40.0 else 0.0
            val samples = ((perSyllableMs / 1000.0) * sampleRate).toInt().coerceAtLeast(800)
            val chunk = ShortArray(256)
            var written = 0
            while (written < samples && coroutineContext.isActive) {
                val n = minOf(chunk.size, samples - written)
                for (i in 0 until n) {
                    val t = (written + i).toDouble() / sampleRate
                    val env = syllableEnvelope(written + i, samples)
                    val amp = if (voiced) {
                        val s1 = sin(phase1)
                        val s2 = 0.45 * sin(phase2)
                        val s3 = 0.22 * sin(phase3)
                        (s1 + s2 + s3) * env * 0.52
                    } else {
                        // Soft breath / pause
                        Random.nextDouble(-0.02, 0.02) * env
                    }
                    chunk[i] = (amp * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        .toShort()
                    phase1 += 2.0 * PI * f0 / sampleRate
                    phase2 += 2.0 * PI * formantA / sampleRate
                    phase3 += 2.0 * PI * formantB / sampleRate
                }
                withContext(Dispatchers.IO) {
                    track.write(chunk, 0, n)
                }
                written += n
                // Lip-sync amplitude from envelope peak in this chunk
                val peak = chunk.take(n).maxOf { kotlin.math.abs(it.toInt()) } / Short.MAX_VALUE.toFloat()
                emit(peak.coerceIn(0.08f, 1f))
            }
            // Tiny gap between syllables
            delay(18)
            emit(0.12f)
        }
        emit(0f)
    } finally {
        try {
            track.stop()
        } catch (_: Exception) {
        }
        try {
            track.release()
        } catch (_: Exception) {
        }
    }
}

private fun syllableEnvelope(i: Int, total: Int): Double {
    val x = i.toDouble() / total.coerceAtLeast(1)
    val attack = (x / 0.12).coerceIn(0.0, 1.0)
    val release = ((1.0 - x) / 0.22).coerceIn(0.0, 1.0)
    return attack * release
}

private fun tokenizeSyllables(text: String): List<String> {
    val words = text
        .lowercase()
        .replace(Regex("[^a-z0-9\\s']"), " ")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
    if (words.isEmpty()) return emptyList()
    val out = ArrayList<String>()
    for (word in words) {
        val chunks = word.chunked(2)
        if (chunks.isEmpty()) out += word else out += chunks
        out += " "
    }
    return out
}
