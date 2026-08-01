package com.test.design.presentation.ivi.glanceables

import android.app.Activity
import android.content.Context
import android.content.Intent

/** Intent action to open the full media player panel. */
const val ACTION_OPEN_MEDIA = "com.test.design.action.OPEN_MEDIA"

/** Intent action to open the full climate control panel. */
const val ACTION_OPEN_CLIMATE = "com.test.design.action.OPEN_CLIMATE"

/** Intent action to open the full vehicle info panel. */
const val ACTION_OPEN_VEHICLE = "com.test.design.action.OPEN_VEHICLE"

/**
 * Launch helpers for standalone IVI panel screens.
 *
 * Prefer these from car assistants / Scalable UI actions so screens open
 * without depending on [com.test.design.MainActivity] navigation state.
 */
object PanelIntents {

    fun openMedia(context: Context): Intent =
        panelIntent(context, MediaPanelActivity::class.java, ACTION_OPEN_MEDIA)

    fun openClimate(context: Context): Intent =
        panelIntent(context, ClimatePanelActivity::class.java, ACTION_OPEN_CLIMATE)

    fun openVehicle(context: Context): Intent =
        panelIntent(context, VehiclePanelActivity::class.java, ACTION_OPEN_VEHICLE)

    private fun panelIntent(
        context: Context,
        activityClass: Class<out Activity>,
        action: String,
    ): Intent = Intent(context, activityClass).apply {
        this.action = action
        if (context !is Activity) {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}
