package com.varp.blockpuzzlesaga.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statistics")
data class StatisticsEntity(
    @PrimaryKey val id: Int = DEFAULT_ID,
    val gamesPlayed: Int = 0,
    val totalPlayTimeMillis: Long = 0L,
    val bestComboChain: Int = 0
) {
    companion object {
        const val DEFAULT_ID = 1
    }
}
