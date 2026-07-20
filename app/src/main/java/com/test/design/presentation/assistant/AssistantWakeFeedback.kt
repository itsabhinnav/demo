package com.test.design.presentation.assistant

import android.content.Context
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

/**
 * Soft haptics for assistant presence (open / dismiss).
 *
 * Earcon [ToneGenerator] paths were removed — they open AUDIO_OUTPUT_FLAG_FAST
 * tracks that destabilize AAOS (AVD + aosp_tangorpro_car) right as immersive
 * morph / “speaking” begins.
 */
class AssistantWakeFeedback(
    private val context: Context,
    private val composeHaptic: HapticFeedback?,
) {
    fun play() {
        playHaptic(confirm = true)
    }

    fun playDismiss() {
        playHaptic(confirm = false)
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
}

@Composable
fun rememberAssistantWakeFeedback(): AssistantWakeFeedback {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    return remember(context, haptics) {
        AssistantWakeFeedback(context, haptics)
    }
}
