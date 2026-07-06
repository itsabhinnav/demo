package com.test.design.component.motion

import androidx.compose.animation.core.Spring
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OemMotionPhysicsConfigTest {

    @Test
    fun fromPreset_standard_hasSnappierFling() {
        val config = OemMotionPhysicsConfig.fromPreset(MotionSchemePreset.Standard)
        assertEquals(MotionSchemePreset.Standard, config.preset)
        assertEquals(1.2f, config.flingFrictionMultiplier, 0.001f)
        assertEquals(false, config.progressUseSpring)
    }

    @Test
    fun fromPreset_expressive_usesSpringProgress() {
        val config = OemMotionPhysicsConfig.fromPreset(MotionSchemePreset.Expressive)
        assertEquals(MotionSchemePreset.Expressive, config.preset)
        assertEquals(true, config.progressUseSpring)
    }

    @Test
    fun customMotionScheme_exposesAllSpatialAndEffectsSpecs() {
        val config = OemMotionPhysicsConfig(
            preset = MotionSchemePreset.Custom,
            spatialDampingRatio = Spring.DampingRatioMediumBouncy,
            spatialStiffness = Spring.StiffnessMedium,
            effectsDampingRatio = Spring.DampingRatioNoBouncy,
            effectsStiffness = Spring.StiffnessLow,
        )
        val scheme = config.toMotionScheme()
        assertNotNull(scheme.defaultSpatialSpec<Any>())
        assertNotNull(scheme.fastSpatialSpec<Any>())
        assertNotNull(scheme.slowSpatialSpec<Any>())
        assertNotNull(scheme.defaultEffectsSpec<Any>())
        assertNotNull(scheme.fastEffectsSpec<Any>())
        assertNotNull(scheme.slowEffectsSpec<Any>())
    }
}
