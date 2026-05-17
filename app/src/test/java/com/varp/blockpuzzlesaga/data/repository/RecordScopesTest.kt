package com.varp.blockpuzzlesaga.data.repository

import com.varp.blockpuzzlesaga.data.db.RecordEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class RecordScopesTest {
    @Test
    fun summaryUsesOnlyCurrentPeriodRecords() {
        val today = LocalDate.of(2026, 5, 16)
        val yesterday = today.minusDays(1)
        val records = listOf(
            RecordEntity(RecordScopes.OVERALL, 2_000, 1),
            RecordEntity(RecordScopes.today(yesterday), 1_800, 1),
            RecordEntity(RecordScopes.today(today), 900, 2),
            RecordEntity(RecordScopes.week(today), 1_200, 2),
            RecordEntity(RecordScopes.month(today), 1_300, 2),
            RecordEntity(RecordScopes.year(today), 1_400, 2)
        )

        val summary = RecordScopes.summary(records, today)

        assertEquals(2_000, summary.overall)
        assertEquals(1_400, summary.year)
        assertEquals(1_300, summary.month)
        assertEquals(1_200, summary.week)
        assertEquals(900, summary.today)
    }

    @Test
    fun summaryShowsZeroWhenTodayHasNoRecordYet() {
        val today = LocalDate.of(2026, 5, 16)
        val records = listOf(
            RecordEntity(RecordScopes.OVERALL, 2_000, 1),
            RecordEntity(RecordScopes.today(today.minusDays(1)), 1_800, 1)
        )

        val summary = RecordScopes.summary(records, today)

        assertEquals(2_000, summary.overall)
        assertEquals(0, summary.today)
    }
}
