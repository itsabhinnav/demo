package com.test.design.presentation.ivi.climate

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * HVAC feedback: vibrator ticks when the device has a motor, plus a short speaker
 * thump so Pixel Tablet (no LRA — `vibratorIds = []`) still has a perceptible click.
 */
class ClimateHaptics(
    context: Context,
    private val composeHaptic: HapticFeedback,
) {
    private val vibrator: Vibrator? = deviceVibrator(context)
    private val hasMotor: Boolean = vibrator?.hasVibrator() == true
    private val tickTrack: AudioTrack? = buildClickTrack(durationMs = 18, bodyHz = 92.0, gain = 0.42)
    private val confirmTrack: AudioTrack? = buildClickTrack(durationMs = 28, bodyHz = 72.0, gain = 0.52)

    init {
        Log.i(TAG, "motor=$hasMotor click=${tickTrack != null}")
    }

    fun tick() {
        performCompose(HapticFeedbackType.SegmentTick)
        vibrate(VibrationEffect.EFFECT_TICK, 18L)
        play(tickTrack)
    }

    fun confirm() {
        performCompose(HapticFeedbackType.Confirm)
        vibrate(VibrationEffect.EFFECT_CLICK, 28L)
        play(confirmTrack)
    }

    fun toggle(on: Boolean) {
        performCompose(if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
        vibrate(
            if (on) VibrationEffect.EFFECT_HEAVY_CLICK else VibrationEffect.EFFECT_CLICK,
            if (on) 32L else 22L,
        )
        play(if (on) confirmTrack else tickTrack)
    }

    fun release() {
        releaseTrack(tickTrack)
        releaseTrack(confirmTrack)
    }

    private fun performCompose(type: HapticFeedbackType) {
        try {
            composeHaptic.performHapticFeedback(type)
        } catch (_: Exception) {
        }
    }

    private fun vibrate(predefined: Int, fallbackMs: Long) {
        val v = vibrator ?: return
        if (!hasMotor) return
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                v.vibrate(VibrationEffect.createPredefined(predefined))
            } else if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(fallbackMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(fallbackMs)
            }
        } catch (_: Exception) {
        }
    }

    private fun play(track: AudioTrack?) {
        if (track == null) return
        try {
            if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                track.stop()
            }
            track.reloadStaticData()
            track.play()
        } catch (_: Exception) {
        }
    }

    companion object {
        private const val TAG = "ClimateHaptics"
    }
}

private fun deviceVibrator(context: Context): Vibrator? = try {
    if (Build.VERSION.SDK_INT >= 31) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Vibrator::class.java)
    }
} catch (_: Exception) {
    null
}

private fun buildClickTrack(durationMs: Int, bodyHz: Double, gain: Double): AudioTrack? {
    return try {
        val n = SAMPLE_RATE * durationMs / 1_000
        val pcm = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val attack = if (t < 0.0012) t / 0.0012 else 1.0
            val body = 0.92 * sin(2.0 * PI * bodyHz * t) * exp(-55.0 * t)
            val click = 0.28 * sin(2.0 * PI * 1850.0 * t) * exp(-240.0 * t)
            val sample = attack * (body + click) * gain
            pcm[i] = (sample * Short.MAX_VALUE)
                .toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
        val attrs = AudioAttributes.Builder()
            // Pixel Tablet SYSTEM stream is muted; ASSISTANT is the audible UI path.
            .setUsage(AudioAttributes.USAGE_ASSISTANT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val format = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        AudioTrack.Builder()
            .setAudioAttributes(attrs)
            .setAudioFormat(format)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(n * 2)
            .build()
            .apply {
                setVolume(0.85f)
                write(pcm, 0, n)
            }
    } catch (_: Exception) {
        null
    }
}

private const val SAMPLE_RATE = 44_100

private fun releaseTrack(track: AudioTrack?) {
    if (track == null) return
    try {
        track.stop()
    } catch (_: Exception) {
    }
    try {
        track.release()
    } catch (_: Exception) {
    }
}

@Composable
fun rememberClimateHaptics(): ClimateHaptics {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val player = remember(context, haptic) { ClimateHaptics(context, haptic) }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    return player
}
