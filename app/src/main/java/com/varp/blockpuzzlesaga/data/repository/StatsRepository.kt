package com.varp.blockpuzzlesaga.data.repository

import com.varp.blockpuzzlesaga.data.db.StatisticsDao
import com.varp.blockpuzzlesaga.data.db.StatisticsEntity

class StatsRepository(
    private val statisticsDao: StatisticsDao
) {
    suspend fun getStatistics(): StatisticsEntity {
        return statisticsDao.find() ?: StatisticsEntity()
    }

    suspend fun saveStatistics(statistics: StatisticsEntity) {
        statisticsDao.upsert(statistics)
    }

    suspend fun recordFinishedGame(playTimeMillis: Long, comboChain: Int) {
        val current = getStatistics()
        statisticsDao.upsert(
            current.copy(
                gamesPlayed = current.gamesPlayed + 1,
                totalPlayTimeMillis = current.totalPlayTimeMillis + playTimeMillis,
                bestComboChain = maxOf(current.bestComboChain, comboChain)
            )
        )
    }
}
