package com.test.design.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeepLinkParserTest {

    @Test
    fun parseDemoPath_valid_returnsId() {
        assertEquals("flow-builder", DeepLinkParser.parseDemoPath("oemdesign", "demo", listOf("flow-builder")))
    }

    @Test
    fun parseDemoPath_wrongScheme_returnsNull() {
        assertNull(DeepLinkParser.parseDemoPath("https", "demo", listOf("flow-builder")))
    }

    @Test
    fun parseDemoPath_wrongHost_returnsNull() {
        assertNull(DeepLinkParser.parseDemoPath("oemdesign", "home", listOf("flow-builder")))
    }

    @Test
    fun parseDemoPath_emptySegment_returnsNull() {
        assertNull(DeepLinkParser.parseDemoPath("oemdesign", "demo", listOf("")))
    }
}
