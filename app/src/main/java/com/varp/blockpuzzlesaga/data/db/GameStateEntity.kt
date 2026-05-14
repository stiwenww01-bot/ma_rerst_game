package com.varp.blockpuzzlesaga.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = DEFAULT_ID,
    val boardJson: String,
    val availablePiecesJson: String,
    val comboTrackerJson: String,
    val rotatedPieceIndicesJson: String = "[]",
    val score: Int,
    val remainingRotations: Int,
    val gameOver: Boolean,
    val updatedAtMillis: Long
) {
    companion object {
        const val DEFAULT_ID = 1
    }
}
