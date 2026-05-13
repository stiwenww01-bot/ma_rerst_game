package com.varp.blockpuzzlesaga.domain.logic

import com.varp.blockpuzzlesaga.domain.model.Board
import com.varp.blockpuzzlesaga.domain.model.CellCoord

object LineChecker {
    fun findCompletedGroups(board: Board): List<CompletedGroup> {
        val rows = (0 until Board.SIZE).mapNotNull { y ->
            val cells = (0 until Board.SIZE).map { x -> CellCoord(x, y) }.toSet()
            if (cells.all(board::isFilled)) CompletedGroup.Row(y, cells) else null
        }
        val columns = (0 until Board.SIZE).mapNotNull { x ->
            val cells = (0 until Board.SIZE).map { y -> CellCoord(x, y) }.toSet()
            if (cells.all(board::isFilled)) CompletedGroup.Column(x, cells) else null
        }
        val boxes = (0 until Board.SIZE step 3).flatMap { boxY ->
            (0 until Board.SIZE step 3).mapNotNull { boxX ->
                val cells = (boxY until boxY + 3).flatMap { y ->
                    (boxX until boxX + 3).map { x -> CellCoord(x, y) }
                }.toSet()
                if (cells.all(board::isFilled)) {
                    CompletedGroup.Box(boxX / 3, boxY / 3, cells)
                } else {
                    null
                }
            }
        }
        return rows + columns + boxes
    }

    fun clearCompletedGroups(board: Board): ClearResult {
        val groups = findCompletedGroups(board)
        val clearedCells = groups.flatMap { it.cells }.toSet()
        return ClearResult(
            board = board.clear(clearedCells),
            groups = groups,
            clearedCells = clearedCells
        )
    }
}

sealed class CompletedGroup(open val cells: Set<CellCoord>) {
    data class Row(val y: Int, override val cells: Set<CellCoord>) : CompletedGroup(cells)
    data class Column(val x: Int, override val cells: Set<CellCoord>) : CompletedGroup(cells)
    data class Box(val boxX: Int, val boxY: Int, override val cells: Set<CellCoord>) : CompletedGroup(cells)
}

data class ClearResult(
    val board: Board,
    val groups: List<CompletedGroup>,
    val clearedCells: Set<CellCoord>
)
