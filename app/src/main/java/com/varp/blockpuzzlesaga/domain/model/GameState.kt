package com.varp.blockpuzzlesaga.domain.model

import com.varp.blockpuzzlesaga.domain.logic.ComboTracker
import com.varp.blockpuzzlesaga.domain.logic.LineChecker
import com.varp.blockpuzzlesaga.domain.logic.PieceGenerator
import com.varp.blockpuzzlesaga.domain.logic.RotationManager
import com.varp.blockpuzzlesaga.domain.logic.ScoreCalculator
import com.varp.blockpuzzlesaga.domain.logic.TrackedPlacement

data class GameState(
    val board: Board = Board(),
    val availablePieces: List<Piece?> = PieceGenerator().generateTray(),
    val score: Int = 0,
    val comboTracker: ComboTracker = ComboTracker(),
    val rotationManager: RotationManager = RotationManager(),
    val gameOver: Boolean = false
) {
    fun placePiece(index: Int, originX: Int, originY: Int, generator: PieceGenerator = PieceGenerator()): MoveResult {
        val piece = availablePieces.getOrNull(index)
            ?: return MoveResult.Invalid(this)
        if (!board.canPlace(piece, originX, originY)) return MoveResult.Invalid(this)

        val placement = board.place(piece, originX, originY)
        val comboUpdate = comboTracker.record(
            TrackedPlacement(
                pieceType = piece.type,
                placementId = placement.placementId,
                cells = placement.cells
            )
        )
        val boardAfterCollapse = comboUpdate.collapse
            ?.let { placement.board.clearPlacements(it.placementIds) }
            ?: placement.board
        val clearResult = LineChecker.clearCompletedGroups(boardAfterCollapse)
        val spinBonusAwarded = clearResult.groups.any { group ->
            val groupColors = group.cells.mapNotNull { boardAfterCollapse.cells[it]?.colorIndex }.toSet()
            groupColors.size == 1 && groupColors.isNotEmpty()
        }
        val addedScore = ScoreCalculator.placementScore(piece.size) +
            ScoreCalculator.clearScore(clearResult.groups.size) +
            ScoreCalculator.collapseScore(comboUpdate.collapse?.cells?.size ?: 0)
        val nextPieces = availablePieces.toMutableList().also { it[index] = null }
        val refreshedPieces = if (nextPieces.all { it == null }) {
            generator.generateTray(score + addedScore)
        } else {
            nextPieces
        }
        val nextRotationManager = rotationManager.releasePiece(index).let { manager ->
            if (spinBonusAwarded) manager.addBonusRotation() else manager
        }
        val nextState = copy(
            board = clearResult.board,
            availablePieces = refreshedPieces,
            score = score + addedScore,
            comboTracker = comboUpdate.tracker,
            rotationManager = nextRotationManager,
            gameOver = false
        ).withGameOverFlag()

        return MoveResult.Placed(
            state = nextState,
            boardBeforeClear = placement.board,
            placedCells = placement.cells,
            clearedCells = clearResult.clearedCells,
            collapsedCells = comboUpdate.collapse?.cells.orEmpty(),
            addedScore = addedScore,
            spinBonusAwarded = spinBonusAwarded,
            clearGroupCount = clearResult.groups.size
        )
    }

    fun rotatePiece(index: Int): GameState {
        val piece = availablePieces.getOrNull(index) ?: return this
        val result = rotationManager.rotate(piece, index)
        if (!result.rotated) return this

        val updatedPieces = availablePieces.toMutableList().also { it[index] = result.piece }
        return copy(availablePieces = updatedPieces, rotationManager = result.manager)
    }

    fun isGameOver(): Boolean {
        val pieces = availablePieces.withIndex().filter { it.value != null }
        return pieces.isNotEmpty() && pieces.none { (index, piece) ->
            canPlaceAvailablePiece(index, requireNotNull(piece))
        }
    }

    private fun canPlaceAvailablePiece(index: Int, piece: Piece): Boolean {
        if (board.canPlaceAny(piece)) return true
        if (!rotationManager.canRotate(index)) return false

        return rotatedVariants(piece).any(board::canPlaceAny)
    }

    private fun rotatedVariants(piece: Piece): Set<Piece> {
        val first = piece.rotatedClockwise()
        val second = first.rotatedClockwise()
        val third = second.rotatedClockwise()
        return setOf(first, second, third)
    }

    private fun withGameOverFlag(): GameState = copy(gameOver = isGameOver())
}

sealed class MoveResult {
    abstract val state: GameState

    data class Placed(
        override val state: GameState,
        val boardBeforeClear: Board,
        val placedCells: Set<CellCoord>,
        val clearedCells: Set<CellCoord>,
        val collapsedCells: Set<CellCoord>,
        val addedScore: Int,
        val spinBonusAwarded: Boolean,
        val clearGroupCount: Int
    ) : MoveResult()

    data class Invalid(
        override val state: GameState
    ) : MoveResult()
}
