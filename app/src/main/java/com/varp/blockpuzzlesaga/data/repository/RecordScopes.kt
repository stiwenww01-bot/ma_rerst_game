package com.varp.blockpuzzlesaga.data.repository

import com.varp.blockpuzzlesaga.data.db.RecordEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

object RecordScopes {
    const val OVERALL = "overall"

    fun today(date: LocalDate = LocalDate.now()): String = "day:${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}"

    fun week(date: LocalDate = LocalDate.now()): String {
        val fields = WeekFields.ISO
        val year = date.get(fields.weekBasedYear())
        val week = date.get(fields.weekOfWeekBasedYear())
        return "week:%04d-W%02d".format(year, week)
    }

    fun month(date: LocalDate = LocalDate.now()): String = "month:%04d-%02d".format(date.year, date.monthValue)

    fun year(date: LocalDate = LocalDate.now()): String = "year:%04d".format(date.year)

    fun dateFromMillis(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
    }

    fun summary(records: List<RecordEntity>, date: LocalDate = LocalDate.now()): RecordSummary {
        return RecordSummary(
            overall = records.firstScore(OVERALL) ?: records.maxOfOrNull { it.score } ?: 0,
            year = records.firstScore(year(date)) ?: 0,
            month = records.firstScore(month(date)) ?: 0,
            week = records.firstScore(week(date)) ?: 0,
            today = records.firstScore(today(date)) ?: 0
        )
    }

    private fun List<RecordEntity>.firstScore(scope: String): Int? {
        return firstOrNull { it.scope == scope }?.score
    }
}

data class RecordSummary(
    val overall: Int,
    val year: Int,
    val month: Int,
    val week: Int,
    val today: Int
)
