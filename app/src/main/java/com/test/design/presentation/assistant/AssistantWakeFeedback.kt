package com.test.design.presentation.assistant

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Subtle wake feedback — soft chime + short haptic — when the assistant summons.
 */
class AssistantWakeFeedback(
    private val context: Context,
    private val composeHaptic: androidx.compose.ui.hapticfeedback.HapticFeedback?,
) {
    fun play() {
        playHaptic()
        playChime()
    }

    private fun playHaptic() {
        try {
            composeHaptic?.performHapticFeedback(HapticFeedbackType.Confirm)
        } catch (_: Exception) {
        }
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= 31) {
                val mgr = context.getSystemService(VibratorManager::class.java)
                mgr?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            }
            if (vibrator == null || !vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(36L, VibrationEffect.DEFAULT_AMPLITUDE),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(36L)
            }
        } catch (_: Exception) {
        }
    }

    private fun playChime() {
        var tone: ToneGenerator? = null
        try {
            tone = ToneGenerator(AudioManager.STREAM_MUSIC, 55)
            tone.startTone(ToneGenerator.TONE_PROP_ACK, 140)
        } catch (_: Exception) {
        } finally {
            // Release after the tone window so it isn't cut off immediately.
            try {
                android.os.Handler(context.mainLooper).postDelayed({
                    try {
                        tone?.release()
                    } catch (_: Exception) {
                    }
                }, 220L)
            } catch (_: Exception) {
                try {
                    tone?.release()
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
