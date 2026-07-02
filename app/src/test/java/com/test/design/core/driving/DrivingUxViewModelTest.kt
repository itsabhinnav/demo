package com.test.design.core.driving

import androidx.lifecycle.SavedStateHandle
import com.test.design.component.core.DrivingUxState
import org.junit.Assert.assertEquals
import org.junit.Test

class DrivingUxViewModelTest {

    @Test
    fun update_persistsDrivingStateInSavedStateHandle() {
        val handle = SavedStateHandle()
        val viewModel = DrivingUxViewModel(handle)

        viewModel.update(DrivingUxState.Restricted)

        assertEquals(DrivingUxState.Restricted.name, handle["driving_ux_state"])
    }

    @Test
    fun init_restoresPersistedState() {
        val handle = SavedStateHandle(mapOf("driving_ux_state" to DrivingUxState.Driving.name))
        val viewModel = DrivingUxViewModel(handle)

        assertEquals(DrivingUxState.Driving, viewModel.drivingUxState.value)
    }
}
