package com.test.design.component.motion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OemMotionSchemeTest {

    @Test
    fun expressivePhysics_defaultSpatial_isSpringBased() {
        val physics = OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial)
        assertTrue(physics.stiffness > 0f)
        assertTrue(physics.dampingRatio > 0f)
    }

    @Test
    fun standardAndExpressive_defaultSpatial_differInStiffnessOrDamping() {
        val standard = OemMotionScheme.standardPhysics(OemMotionScheme.SpringToken.DefaultSpatial)
        val expressive = OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial)
        assertTrue(
            standard.stiffness != expressive.stiffness ||
                standard.dampingRatio != expressive.dampingRatio,
        )
    }

    @Test
    fun springSpec_roundTripsPhysicsValues() {
        val physics = OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial)
        val withVelocity = physics.copy(initialVelocity = 900f)
        assertEquals(900f, withVelocity.initialVelocity, 0.001f)
        assertTrue(withVelocity.label().contains("v₀=900"))
    }
}
