package com.varp.blockpuzzlesaga.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface StatisticsDao {
    @Upsert
    suspend fun upsert(statistics: StatisticsEntity)

    @Query("SELECT * FROM statistics WHERE id = :id LIMIT 1")
    suspend fun find(id: Int = StatisticsEntity.DEFAULT_ID): StatisticsEntity?
}
