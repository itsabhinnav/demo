package com.test.design.presentation.ivi.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * ADB entry points for in-app floating system bars:
 *
 * ```
 * adb shell am broadcast -a com.test.design.action.HIDE_SYSTEM_BARS \
 *   -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver
 * adb shell am broadcast -a com.test.design.action.SHOW_SYSTEM_BARS \
 *   -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver
 * adb shell am broadcast -a com.test.design.action.TOGGLE_SYSTEM_BARS \
 *   -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver
 * adb shell am broadcast -a com.test.design.action.SET_SYSTEM_BARS \
 *   -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver \
 *   --ez visible false
 * ```
 */
class FloatingSystemBarsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            ACTION_SHOW -> FloatingSystemBarsVisibility.show()
            ACTION_HIDE -> FloatingSystemBarsVisibility.hide()
            ACTION_TOGGLE -> FloatingSystemBarsVisibility.toggle()
            ACTION_SET -> {
                if (intent.hasExtra(EXTRA_VISIBLE)) {
                    FloatingSystemBarsVisibility.setVisible(
                        intent.getBooleanExtra(EXTRA_VISIBLE, true),
                    )
                }
            }
        }
    }

    companion object {
        const val ACTION_SHOW = "com.test.design.action.SHOW_SYSTEM_BARS"
        const val ACTION_HIDE = "com.test.design.action.HIDE_SYSTEM_BARS"
        const val ACTION_TOGGLE = "com.test.design.action.TOGGLE_SYSTEM_BARS"
        const val ACTION_SET = "com.test.design.action.SET_SYSTEM_BARS"
        const val EXTRA_VISIBLE = "visible"
    }
}
