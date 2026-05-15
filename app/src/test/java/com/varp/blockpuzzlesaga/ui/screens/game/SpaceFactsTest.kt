package com.varp.blockpuzzlesaga.ui.screens.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpaceFactsTest {
    @Test
    fun spaceFactsContainFiveHundredUniqueShortFacts() {
        val facts = buildSpaceFacts()

        assertEquals(500, facts.size)
        assertEquals(facts.size, facts.toSet().size)
        assertTrue(facts.all { it.length <= 90 })
    }
}
