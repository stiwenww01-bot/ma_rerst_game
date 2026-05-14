package com.varp.blockpuzzlesaga.domain

import com.varp.blockpuzzlesaga.domain.logic.ComboTracker
import com.varp.blockpuzzlesaga.domain.logic.CompletedGroup
import com.varp.blockpuzzlesaga.domain.logic.LineChecker
import com.varp.blockpuzzlesaga.domain.logic.PieceGenerator
import com.varp.blockpuzzlesaga.domain.logic.RotationManager
import com.varp.blockpuzzlesaga.domain.logic.ScoreCalculator
import com.varp.blockpuzzlesaga.domain.logic.TrackedPlacement
import com.varp.blockpuzzlesaga.domain.model.Board
import com.varp.blockpuzzlesaga.domain.model.CellCoord
import com.varp.blockpuzzlesaga.domain.model.GameState
import com.varp.blockpuzzlesaga.domain.model.MoveResult
import com.varp.blockpuzzlesaga.domain.model.Piece
import com.varp.blockpuzzlesaga.domain.model.PieceType
import com.varp.blockpuzzlesaga.domain.model.PlacedCell
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameLogicTest {
    @Test
    fun pieceFactoryNormalizesCoordinates() {
        val piece = Piece.of(PieceType.L_SMALL, 4 to 5, 4 to 6, 5 to 6)

        assertEquals(setOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 1)), piece.cells)
    }

    @Test
    fun rotationTurnsPieceClockwiseAndNormalizesCoordinates() {
        val rotated = PieceGenerator.lSmall.rotatedClockwise()

        assertEquals(setOf(CellCoord(0, 1), CellCoord(1, 0), CellCoord(1, 1)), rotated.cells)
    }

    @Test
    fun fourRotationsReturnOriginalShape() {
        val original = PieceGenerator.t
        val rotated = original
            .rotatedClockwise()
            .rotatedClockwise()
            .rotatedClockwise()
            .rotatedClockwise()

        assertEquals(original.cells, rotated.cells)
    }

    @Test
    fun boardAllowsValidPlacementOnEmptyBoard() {
        assertTrue(Board().canPlace(PieceGenerator.square2, 3, 4))
    }

    @Test
    fun boardRejectsNegativePlacement() {
        assertFalse(Board().canPlace(PieceGenerator.square2, -1, 0))
    }

    @Test
    fun boardRejectsOverflowPlacement() {
        assertFalse(Board().canPlace(PieceGenerator.line5, 5, 8))
    }

    @Test
    fun boardRejectsOverlappingPlacement() {
        val board = Board().place(PieceGenerator.square2, 0, 0).board

        assertFalse(board.canPlace(PieceGenerator.line3, 0, 1))
    }

    @Test
    fun boardPlaceFillsCellsAndIncrementsPlacementId() {
        val first = Board().place(PieceGenerator.line2, 0, 0)
        val second = first.board.place(PieceGenerator.single, 4, 4)

        assertEquals(2, first.cells.size)
        assertEquals(1L, first.placementId)
        assertEquals(2L, second.placementId)
        assertNotNull(first.board.cells[CellCoord(1, 0)])
    }

    @Test
    fun lineCheckerFindsCompletedRow() {
        val board = filledBoard((0 until Board.SIZE).map { CellCoord(it, 2) })

        val groups = LineChecker.findCompletedGroups(board)

        assertTrue(groups.any { it is CompletedGroup.Row && it.y == 2 })
    }

    @Test
    fun lineCheckerFindsCompletedColumn() {
        val board = filledBoard((0 until Board.SIZE).map { CellCoord(4, it) })

        val groups = LineChecker.findCompletedGroups(board)

        assertTrue(groups.any { it is CompletedGroup.Column && it.x == 4 })
    }

    @Test
    fun lineCheckerFindsCompletedThreeByThreeBox() {
        val cells = (3..5).flatMap { y -> (6..8).map { x -> CellCoord(x, y) } }
        val board = filledBoard(cells)

        val groups = LineChecker.findCompletedGroups(board)

        assertTrue(groups.any { it is CompletedGroup.Box && it.boxX == 2 && it.boxY == 1 })
    }

    @Test
    fun clearCompletedGroupsRemovesUnionOfCells() {
        val row = (0 until Board.SIZE).map { CellCoord(it, 0) }
        val column = (0 until Board.SIZE).map { CellCoord(0, it) }
        val board = filledBoard(row + column)

        val result = LineChecker.clearCompletedGroups(board)

        assertEquals(17, result.clearedCells.size)
        assertTrue(result.board.cells.isEmpty())
    }

    @Test
    fun clearCompletedGroupsLeavesPartialBoardUntouched() {
        val board = filledBoard(listOf(CellCoord(0, 0), CellCoord(1, 0)))

        val result = LineChecker.clearCompletedGroups(board)

        assertTrue(result.groups.isEmpty())
        assertEquals(board.cells, result.board.cells)
    }

    @Test
    fun placementScoreEqualsPieceSize() {
        assertEquals(5, ScoreCalculator.placementScore(PieceGenerator.plus.size))
    }

    @Test
    fun clearScoreForOneGroupIsOneHundred() {
        assertEquals(100, ScoreCalculator.clearScore(1))
    }

    @Test
    fun clearScoreForTwoGroupsIncludesFiftyBonus() {
        assertEquals(250, ScoreCalculator.clearScore(2))
    }

    @Test
    fun clearScoreForThreeGroupsIncludesOneHundredFiftyBonus() {
        assertEquals(450, ScoreCalculator.clearScore(3))
    }

    @Test
    fun clearScoreForFourGroupsIncludesFourHundredBonus() {
        assertEquals(800, ScoreCalculator.clearScore(4))
    }

    @Test
    fun comboTrackerCountsSamePiecesInARow() {
        val first = ComboTracker().record(tracked(PieceType.LINE_2, 1))
        val second = first.tracker.record(tracked(PieceType.LINE_2, 2))

        assertEquals(2, second.tracker.count)
        assertNull(second.collapse)
    }

    @Test
    fun comboTrackerResetsWhenDifferentPieceAppears() {
        val tracker = ComboTracker()
            .record(tracked(PieceType.LINE_2, 1)).tracker
            .record(tracked(PieceType.LINE_2, 2)).tracker

        val update = tracker.record(tracked(PieceType.SQUARE_2, 3))

        assertEquals(PieceType.SQUARE_2, update.tracker.currentType)
        assertEquals(1, update.tracker.count)
    }

    @Test
    fun comboTrackerCollapsesThreeIdenticalPiecesAndResets() {
        val tracker = ComboTracker()
            .record(tracked(PieceType.LINE_2, 1)).tracker
            .record(tracked(PieceType.LINE_2, 2)).tracker

        val update = tracker.record(tracked(PieceType.LINE_2, 3))

        assertNotNull(update.collapse)
        assertEquals(0, update.tracker.count)
        assertEquals(setOf(1L, 2L, 3L), update.collapse?.placementIds)
    }

    @Test
    fun collapseEventContainsAllOccupiedCells() {
        val update = ComboTracker()
            .record(tracked(PieceType.SINGLE, 1, CellCoord(0, 0))).tracker
            .record(tracked(PieceType.SINGLE, 2, CellCoord(1, 0))).tracker
            .record(tracked(PieceType.SINGLE, 3, CellCoord(2, 0)))

        assertEquals(setOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0)), update.collapse?.cells)
    }

    @Test
    fun rotationManagerStartsWithThreeRotations() {
        assertEquals(3, RotationManager().remainingRotations)
    }

    @Test
    fun rotationManagerRotatesAndDecrementsCounter() {
        val result = RotationManager().rotate(PieceGenerator.l)

        assertTrue(result.rotated)
        assertEquals(2, result.manager.remainingRotations)
        assertNotEquals(PieceGenerator.l.cells, result.piece.cells)
    }

    @Test
    fun rotationManagerDoesNothingWhenCounterIsZero() {
        val result = RotationManager(remainingRotations = 0).rotate(PieceGenerator.l)

        assertFalse(result.rotated)
        assertEquals(PieceGenerator.l, result.piece)
        assertEquals(0, result.manager.remainingRotations)
    }

    @Test
    fun pieceGeneratorReturnsThreePieces() {
        val tray = PieceGenerator(Random(1)).generateTray()

        assertEquals(3, tray.size)
        assertTrue(tray.all { it.cells.isNotEmpty() })
    }

    @Test
    fun pieceGeneratorContainsAtLeastTwentyFiveTemplates() {
        assertTrue(PieceGenerator(Random(1)).allTemplates().size >= 25)
    }

    @Test
    fun pieceGeneratorDoesNotOfferThreeByThreeSquare() {
        assertFalse(PieceGenerator(Random(1)).allTemplates().any { it.type == PieceType.SQUARE_3 })
    }

    @Test
    fun pieceGeneratorIncludesSmallPieceInTrayForBalance() {
        val tray = PieceGenerator(Random(2)).generateTray()

        assertTrue(tray.any { it.size <= 3 })
    }

    @Test
    fun gameStateRejectsInvalidMoveWithoutChangingState() {
        val state = GameState(availablePieces = listOf(PieceGenerator.line5, null, null))

        val result = state.placePiece(0, 7, 0)

        assertTrue(result is MoveResult.Invalid)
        assertSame(state, result.state)
    }

    @Test
    fun gameStatePlacesPieceAndAddsPlacementScore() {
        val state = GameState(availablePieces = listOf(PieceGenerator.line2, PieceGenerator.single, null))

        val result = state.placePiece(0, 0, 0) as MoveResult.Placed

        assertEquals(2, result.addedScore)
        assertEquals(2, result.state.score)
        assertEquals(2, result.state.board.cells.size)
    }

    @Test
    fun gameStateRefreshesTrayWhenAllThreePiecesAreUsed() {
        val state = GameState(availablePieces = listOf(PieceGenerator.single, null, null))

        val result = state.placePiece(0, 0, 0) as MoveResult.Placed

        assertEquals(3, result.state.availablePieces.size)
        assertTrue(result.state.availablePieces.all { it != null })
    }

    @Test
    fun gameStateClearsLineAndAddsClearScore() {
        val existing = (0..7).map { CellCoord(it, 0) }
        val state = GameState(
            board = filledBoard(existing),
            availablePieces = listOf(PieceGenerator.single, PieceGenerator.line2, null)
        )

        val result = state.placePiece(0, 8, 0) as MoveResult.Placed

        assertEquals(101, result.addedScore)
        assertTrue(CellCoord(8, 0) in result.clearedCells)
        assertTrue(CellCoord(8, 0) in result.boardBeforeClear.cells)
        assertTrue(result.state.board.cells.isEmpty())
    }

    @Test
    fun gameStateCollapsesThreeIdenticalPlacements() {
        val state = GameState(
            availablePieces = listOf(PieceGenerator.single, PieceGenerator.single, PieceGenerator.single)
        )

        val first = (state.placePiece(0, 0, 0) as MoveResult.Placed).state
        val second = (first.placePiece(1, 1, 0) as MoveResult.Placed).state
        val third = second.placePiece(2, 2, 0) as MoveResult.Placed

        assertEquals(setOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(2, 0)), third.collapsedCells)
        assertTrue(third.state.board.cells.isEmpty())
        assertEquals(9, third.state.score)
    }

    @Test
    fun gameStateRotatePieceUpdatesPieceAndCounter() {
        val state = GameState(availablePieces = listOf(PieceGenerator.l, null, null))

        val rotated = state.rotatePiece(0)

        assertEquals(2, rotated.rotationManager.remainingRotations)
        assertNotEquals(state.availablePieces[0]?.cells, rotated.availablePieces[0]?.cells)
    }

    @Test
    fun gameStateReportsGameOverWhenNoAvailablePieceFits() {
        val state = GameState(
            board = filledBoard(allBoardCells()),
            availablePieces = listOf(PieceGenerator.single, PieceGenerator.line2, null)
        )

        assertTrue(state.isGameOver())
    }

    @Test
    fun gameStateDoesNotReportGameOverWhenAPieceFits() {
        val occupied = allBoardCells() - CellCoord(8, 8)
        val state = GameState(
            board = filledBoard(occupied),
            availablePieces = listOf(PieceGenerator.single, PieceGenerator.line2, null)
        )

        assertFalse(state.isGameOver())
    }

    private fun tracked(
        type: PieceType,
        id: Long,
        vararg cells: CellCoord = arrayOf(CellCoord(id.toInt(), 0))
    ): TrackedPlacement {
        return TrackedPlacement(type, id, cells.toSet())
    }

    private fun filledBoard(coords: Iterable<CellCoord>): Board {
        val cells = coords.associateWith {
            PlacedCell(pieceType = PieceType.SINGLE, placementId = 1L, colorIndex = 0)
        }
        return Board(cells = cells)
    }

    private fun allBoardCells(): Set<CellCoord> {
        return (0 until Board.SIZE).flatMap { y ->
            (0 until Board.SIZE).map { x -> CellCoord(x, y) }
        }.toSet()
    }
}
