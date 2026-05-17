package com.varp.blockpuzzlesaga.ui.screens.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpaceFactsTest {
    @Test
    fun spaceFactsContainImportedUniqueShortFacts() {
        val facts = buildSpaceFacts()

        assertTrue(facts.size >= 100)
        assertEquals(facts.size, facts.toSet().size)
        assertTrue(facts.all { it.length <= 70 })
    }
}
