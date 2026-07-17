package com.test.design

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Temporarily disabled — system bars stay visible for debugging Scalable UI / overlays.
 * Restore hide + BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE when re-enabling immersive.
 */
fun Activity.hideSystemBarsImmersive() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        show(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    }
}
