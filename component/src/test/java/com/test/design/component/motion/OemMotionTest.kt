package com.test.design.component.motion

import com.test.design.component.core.DrivingUxState
import org.junit.Assert.assertEquals
import org.junit.Test

class OemMotionTest {

    @Test
    fun durationMs_whenDriving_returnsZero() {
        assertEquals(0, OemMotion.durationMs(DrivingUxState.Driving, opening = true, OemMotion.OpenDurationMs))
    }

    @Test
    fun durationMs_whenParked_capsAtPolicyMaximum() {
        assertEquals(250, OemMotion.durationMs(DrivingUxState.Parked, opening = true, OemMotion.OpenDurationMs))
        assertEquals(200, OemMotion.durationMs(DrivingUxState.Parked, opening = false, OemMotion.CloseDurationMs))
    }
}
