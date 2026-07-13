package com.test.design.presentation.ivi.map

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MapIntentsTest {

    @Test
    fun shouldRedirectToMap_forAppMapsCategory() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_MAPS)
        }
        assertTrue(MapIntents.shouldRedirectToMap(intent))
    }

    @Test
    fun shouldRedirectToMap_forOpenMapAction() {
        assertTrue(MapIntents.shouldRedirectToMap(Intent(ACTION_OPEN_MAP)))
    }

    @Test
    fun shouldRedirectToMap_forGeoView() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.77,-122.42"))
        assertTrue(MapIntents.shouldRedirectToMap(intent))
    }

    @Test
    fun shouldNotRedirect_designLauncherMain() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            addCategory("android.intent.category.APP_AUTOMOTIVE")
        }
        assertFalse(MapIntents.shouldRedirectToMap(intent))
    }

    @Test
    fun parseLaunchConfig_mainExpandsNavigation() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        assertTrue(MapIntents.parseLaunchConfig(intent).expandNavigation)
    }

    @Test
    fun openMapFrom_targetsMapActivity() {
        val context = RuntimeEnvironment.getApplication()
        val source = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_MAPS)
        }
        val forwarded = MapIntents.openMapFrom(source, context)
        assertEquals(MapActivity::class.java.name, forwarded.component?.className)
        assertTrue(forwarded.getBooleanExtra(EXTRA_EXPAND_NAVIGATION, false))
    }
}
