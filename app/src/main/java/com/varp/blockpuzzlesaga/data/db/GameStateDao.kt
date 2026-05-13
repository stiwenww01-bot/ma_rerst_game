package com.varp.blockpuzzlesaga.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GameStateDao {
    @Upsert
    suspend fun upsert(gameState: GameStateEntity)

    @Query("SELECT * FROM game_state WHERE id = :id LIMIT 1")
    suspend fun find(id: Int = GameStateEntity.DEFAULT_ID): GameStateEntity?

    @Query("DELETE FROM game_state WHERE id = :id")
    suspend fun delete(id: Int = GameStateEntity.DEFAULT_ID)
}
