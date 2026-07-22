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
import kotlin.math.exp
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
     * Two-strike instrumental bell (ding–dong up, dong–ding down).
     * Additive inharmonic partials + exponential ring; off main thread.
     */
    private fun playSoftChime(ascending: Boolean) {
        thread(name = "assistant-chime", isDaemon = true) {
            var track: AudioTrack? = null
            try {
                val sampleRate = 44_100
                val durationMs = 520
                val n = sampleRate * durationMs / 1_000
                val pcm = ShortArray(n)
                // Handbell / door-chime pitches: C5 ↔ E5.
                val note0Hz = if (ascending) 523.25 else 659.25
                val note1Hz = if (ascending) 659.25 else 523.25
                val strike1At = 0.0
                val strike2At = 0.155
                // amp, freq ratio (inharmonic), decay rate — tubular-bell-ish.
                val partials = arrayOf(
                    doubleArrayOf(1.00, 1.00, 3.2),
                    doubleArrayOf(0.52, 2.01, 4.8),
                    doubleArrayOf(0.34, 2.76, 6.2),
                    doubleArrayOf(0.20, 4.07, 8.0),
                    doubleArrayOf(0.10, 5.43, 10.5),
                )
                val master = 0.22 // quiet cabin level before track volume
                for (i in 0 until n) {
                    val t = i.toDouble() / sampleRate
                    var sample = 0.0
                    sample += bellStrike(t, strike1At, note0Hz, partials)
                    sample += bellStrike(t, strike2At, note1Hz, partials) * 0.92
                    pcm[i] = (sample * master * Short.MAX_VALUE)
                        .toInt()
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
                track.setVolume(0.55f)
                track.write(pcm, 0, n)
                track.play()
                Thread.sleep((durationMs + 80).toLong())
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

    /** One struck partial stack: sharp attack, exponential metallic ring. */
    private fun bellStrike(
        t: Double,
        strikeAt: Double,
        fundamentalHz: Double,
        partials: Array<DoubleArray>,
    ): Double {
        val age = t - strikeAt
        if (age < 0.0) return 0.0
        // ~4 ms attack so the hit reads as a mallet, not a beep.
        val attack = if (age < 0.004) age / 0.004 else 1.0
        var sum = 0.0
        for (p in partials) {
            val amp = p[0]
            val ratio = p[1]
            val decay = p[2]
            val env = attack * exp(-decay * age)
            sum += amp * env * sin(2.0 * PI * fundamentalHz * ratio * t)
        }
        return sum
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
