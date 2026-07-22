package com.test.design.presentation.assistant

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveInputTextTest {

    @Test
    fun tokensKeepWhitespaceWithFollowingWord() {
        val tokens = liveInputTokens("Hey — find a coffee")
        assertEquals(listOf("Hey", " —", " find", " a", " coffee"), tokens)
        assertEquals("Hey — find a coffee", tokens.joinToString(""))
    }

    @Test
    fun emptyTextHasNoTokens() {
        assertTrue(liveInputTokens("").isEmpty())
    }

    @Test
    fun sharedPrefixCountsStableStem() {
        val previous = liveInputTokens("find a cafe")
        val next = liveInputTokens("find a coffee shop")
        assertEquals(2, liveInputSharedPrefixCount(previous, next))
    }

    @Test
    fun sharedPrefixIsZeroWhenDivergent() {
        val previous = liveInputTokens("Hello there")
        val next = liveInputTokens("On it — thinking")
        assertEquals(0, liveInputSharedPrefixCount(previous, next))
    }
}
