package com.varp.blockpuzzlesaga.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Upsert
    suspend fun upsert(settings: SettingsEntity)

    @Query("SELECT * FROM settings WHERE id = :id LIMIT 1")
    suspend fun find(id: Int = SettingsEntity.DEFAULT_ID): SettingsEntity?

    @Query("SELECT * FROM settings WHERE id = :id LIMIT 1")
    fun observe(id: Int = SettingsEntity.DEFAULT_ID): Flow<SettingsEntity?>
}
