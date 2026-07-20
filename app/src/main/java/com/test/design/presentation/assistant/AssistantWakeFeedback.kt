package com.test.design.presentation.assistant

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Soft earcons + haptics for assistant presence (open / dismiss).
 */
class AssistantWakeFeedback(
    private val context: Context,
    private val composeHaptic: HapticFeedback?,
) {
    /** Rising two-tone chime when the assistant opens or is summoned. */
    fun play() {
        playHaptic(confirm = true)
        playToneSequence(
            tones = intArrayOf(
                ToneGenerator.TONE_PROP_BEEP,
                ToneGenerator.TONE_PROP_ACK,
            ),
            durationsMs = intArrayOf(90, 160),
            gapMs = 40L,
            volume = 70,
        )
    }

    /** Soft fall-off when the session dismisses. */
    fun playDismiss() {
        playHaptic(confirm = false)
        playToneSequence(
            tones = intArrayOf(ToneGenerator.TONE_PROP_NACK),
            durationsMs = intArrayOf(110),
            gapMs = 0L,
            volume = 45,
        )
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

    private fun playToneSequence(
        tones: IntArray,
        durationsMs: IntArray,
        gapMs: Long,
        volume: Int,
    ) {
        var tone: ToneGenerator? = null
        try {
            tone = ToneGenerator(AudioManager.STREAM_MUSIC, volume.coerceIn(0, 100))
            val handler = Handler(Looper.getMainLooper())
            var delay = 0L
            for (i in tones.indices) {
                val t = tones[i]
                val dur = durationsMs.getOrElse(i) { 120 }
                val gen = tone
                handler.postDelayed({
                    try {
                        gen?.startTone(t, dur)
                    } catch (_: Exception) {
                    }
                }, delay)
                delay += dur + gapMs
            }
            handler.postDelayed({
                try {
                    tone?.release()
                } catch (_: Exception) {
                }
            }, delay + 80L)
        } catch (_: Exception) {
            try {
                tone?.release()
            } catch (_: Exception) {
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
