package com.varp.blockpuzzlesaga.ui.screens.game

import com.varp.blockpuzzlesaga.data.db.RecordEntity
import com.varp.blockpuzzlesaga.domain.model.Board
import com.varp.blockpuzzlesaga.domain.model.CellCoord
import com.varp.blockpuzzlesaga.domain.model.GameState

data class GameUiState(
    val gameState: GameState = GameState(),
    val records: List<RecordEntity> = emptyList(),
    val selectedPieceIndex: Int? = null,
    val dragPreview: DragPreview? = null,
    val boardOverride: Board? = null,
    val clearingCells: Set<CellCoord> = emptySet(),
    val spaceFact: String? = null,
    val spinBonusText: String? = null,
    val isResolvingClear: Boolean = false,
    val isLoading: Boolean = true
)

data class DragPreview(
    val pieceIndex: Int,
    val origin: CellCoord,
    val cells: Set<CellCoord>,
    val isValid: Boolean,
    val colorIndex: Int
)
