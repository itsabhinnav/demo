package com.test.design.presentation.demos.flow

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowDesignCodecTest {

    @Test
    fun roundTrip_preservesFlow() {
        val original = FlowDesignStore.defaultFlow()
        val decoded = FlowDesignCodec.decode(FlowDesignCodec.encode(original))

        assertEquals(original.title, decoded.title)
        assertEquals(original.screens.size, decoded.screens.size)
        assertEquals(original.screens.first().componentIds, decoded.screens.first().componentIds)
        assertEquals(FlowDesignStore.CURRENT_SCHEMA_VERSION, decoded.schemaVersion)
    }

    @Test
    fun decode_rejectsEmptyScreens() {
        val json = """{"schemaVersion":1,"title":"Empty","screens":[]}"""
        assertTrue(runCatching { FlowDesignCodec.decode(json) }.isFailure)
    }
}
