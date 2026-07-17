package com.test.design.core.cluster

import com.test.design.core.DrivingUxState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClusterUiStateTest {

    @Test
    fun parkedShowsZeroAndParkGear() {
        val cluster = ClusterUiState.fromDrivingUx(DrivingUxState.Parked)
        assertEquals(0, cluster.speedMph)
        assertEquals("P", cluster.gear)
        assertTrue(cluster.isSimulated)
    }

    @Test
    fun drivingShowsHighwaySpeed() {
        val cluster = ClusterUiState.fromDrivingUx(DrivingUxState.Driving)
        assertEquals(54, cluster.speedMph)
        assertEquals("D", cluster.gear)
    }

    @Test
    fun restrictedShowsReducedSpeed() {
        val cluster = ClusterUiState.fromDrivingUx(DrivingUxState.Restricted)
        assertEquals(32, cluster.speedMph)
        assertEquals("D", cluster.gear)
    }
}
