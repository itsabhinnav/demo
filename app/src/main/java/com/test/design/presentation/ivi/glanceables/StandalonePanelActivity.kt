package com.test.design.presentation.ivi.glanceables

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.test.design.presentation.ivi.map.MapIntents

/**
 * Full-screen IVI panel launched independently (adb / assistant / Scalable UI).
 *
 * Panels use their own task affinity, so [finish] alone would leave an empty stack.
 * Back always returns to [com.test.design.MainActivity] driving home — same pattern as
 * [com.test.design.presentation.ivi.map.MapActivity].
 */
abstract class StandalonePanelActivity : GlanceableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateHomeAndFinish()
                }
            },
        )
        super.onCreate(savedInstanceState)
    }

    /** UI back affordance and system back both land on driving home. */
    protected fun navigateHomeAndFinish() {
        startActivity(MapIntents.openMain(this))
        finish()
    }
}
