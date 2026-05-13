package com.varp.blockpuzzlesaga.data.repository

import com.varp.blockpuzzlesaga.data.db.GameStateDao
import com.varp.blockpuzzlesaga.data.serialization.GameStateJson
import com.varp.blockpuzzlesaga.domain.model.GameState

class GameRepository(
    private val gameStateDao: GameStateDao,
    private val gameStateJson: GameStateJson = GameStateJson()
) {
    suspend fun saveGame(state: GameState, updatedAtMillis: Long = System.currentTimeMillis()) {
        gameStateDao.upsert(gameStateJson.toEntity(state, updatedAtMillis))
    }

    suspend fun loadGame(): GameState? {
        return gameStateDao.find()?.let(gameStateJson::fromEntity)
    }

    suspend fun clearSavedGame() {
        gameStateDao.delete()
    }
}
