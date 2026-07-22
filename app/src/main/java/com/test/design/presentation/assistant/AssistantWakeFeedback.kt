package com.test.design.presentation.assistant

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlin.math.PI
import kotlin.math.sin
import kotlin.concurrent.thread

/**
 * Soft haptics + gentle earcons for assistant presence (slide up / slide down).
 *
 * Chimes use [AudioTrack] with [AudioAttributes.USAGE_ASSISTANCE_SONIFICATION]
 * (not [android.media.ToneGenerator], which opens AUDIO_OUTPUT_FLAG_FAST tracks
 * that destabilize AAOS AVDs).
 */
class AssistantWakeFeedback(
    private val context: Context,
    private val composeHaptic: HapticFeedback?,
) {
    fun play() {
        playHaptic(confirm = true)
        playSoftChime(ascending = true)
    }

    fun playDismiss() {
        playHaptic(confirm = false)
        playSoftChime(ascending = false)
    }

    private fun playHaptic(confirm: Boolean) {
        try {
            composeHaptic?.performHapticFeedback(
                if (confirm) HapticFeedbackType.Confirm else HapticFeedbackType.LongPress,
            )
        } catch (_: Exception) {
        }
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= 31) {
                context.getSystemService(VibratorManager::class.java)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            }
            if (vibrator == null || !vibrator.hasVibrator()) return
            val ms = if (confirm) 36L else 24L
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(ms)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Soft two-note chime — rising on summon, falling on dismiss.
     * Runs off the main thread; failures are swallowed.
     */
    private fun playSoftChime(ascending: Boolean) {
        thread(name = "assistant-chime", isDaemon = true) {
            var track: AudioTrack? = null
            try {
                val sampleRate = 22_050
                val durationMs = 220
                val n = sampleRate * durationMs / 1_000
                val pcm = ShortArray(n)
                // Soft pentatonic-ish tones: E5→G5 up, G5→E5 down.
                val f0 = if (ascending) 659.25f else 783.99f
                val f1 = if (ascending) 783.99f else 659.25f
                val amp = 0.18 // keep quiet in cabin
                for (i in 0 until n) {
                    val t = i.toDouble() / sampleRate
                    val u = i.toDouble() / (n - 1).coerceAtLeast(1)
                    val freq = f0 + (f1 - f0) * u
                    // Short attack / longer release envelope.
                    val env = when {
                        u < 0.08 -> u / 0.08
                        else -> (1.0 - (u - 0.08) / 0.92).coerceIn(0.0, 1.0)
                    }
                    val sample = sin(2.0 * PI * freq * t) * amp * env
                    pcm[i] = (sample * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        .toShort()
                }

                val attrs = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                val format = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
                track = AudioTrack.Builder()
                    .setAudioAttributes(attrs)
                    .setAudioFormat(format)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(n * 2)
                    .build()
                track.setVolume(0.45f)
                track.write(pcm, 0, n)
                track.play()
                Thread.sleep((durationMs + 60).toLong())
            } catch (_: Exception) {
            } finally {
                try {
                    track?.stop()
                } catch (_: Exception) {
                }
                try {
                    track?.release()
                } catch (_: Exception) {
                }
            }
        }
    }
}

@Composable
fun rememberAssistantWakeFeedback(): AssistantWakeFeedback {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    return remember(context, haptics) {
        AssistantWakeFeedback(context, haptics)
    }
}
