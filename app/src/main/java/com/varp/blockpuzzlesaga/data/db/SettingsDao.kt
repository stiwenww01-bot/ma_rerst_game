package com.varp.blockpuzzlesaga.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SettingsDao {
    @Upsert
    suspend fun upsert(settings: SettingsEntity)

    @Query("SELECT * FROM settings WHERE id = :id LIMIT 1")
    suspend fun find(id: Int = SettingsEntity.DEFAULT_ID): SettingsEntity?
}
