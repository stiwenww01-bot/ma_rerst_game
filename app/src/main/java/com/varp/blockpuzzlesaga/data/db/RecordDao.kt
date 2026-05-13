package com.varp.blockpuzzlesaga.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RecordDao {
    @Upsert
    suspend fun upsert(record: RecordEntity)

    @Query("SELECT * FROM records WHERE scope = :scope LIMIT 1")
    suspend fun findByScope(scope: String): RecordEntity?

    @Query("SELECT * FROM records ORDER BY score DESC")
    suspend fun findAll(): List<RecordEntity>

    @Query("DELETE FROM records WHERE scope = :scope")
    suspend fun delete(scope: String)
}
