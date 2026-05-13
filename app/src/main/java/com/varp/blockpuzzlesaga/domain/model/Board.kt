package com.varp.blockpuzzlesaga.domain.model

data class Board(
    val cells: Map<CellCoord, PlacedCell> = emptyMap(),
    val nextPlacementId: Long = 1L
) {
    fun canPlace(piece: Piece, originX: Int, originY: Int): Boolean {
        return piece.cells.all { cell ->
            val target = CellCoord(originX + cell.x, originY + cell.y)
            target.x in 0 until SIZE &&
                target.y in 0 until SIZE &&
                cells[target] == null
        }
    }

    fun canPlaceAny(piece: Piece): Boolean {
        return (0 until SIZE).any { y ->
            (0 until SIZE).any { x -> canPlace(piece, x, y) }
        }
    }

    fun place(piece: Piece, originX: Int, originY: Int): Placement {
        require(canPlace(piece, originX, originY)) { "Piece cannot be placed at $originX,$originY." }

        val placementCells = piece.cells
            .map { CellCoord(originX + it.x, originY + it.y) }
            .toSet()
        val placedCell = PlacedCell(
            pieceType = piece.type,
            placementId = nextPlacementId,
            colorIndex = piece.colorIndex
        )
        val updatedCells = cells + placementCells.associateWith { placedCell }

        return Placement(
            board = copy(cells = updatedCells, nextPlacementId = nextPlacementId + 1),
            placementId = nextPlacementId,
            pieceType = piece.type,
            cells = placementCells
        )
    }

    fun clear(cellsToClear: Set<CellCoord>): Board {
        return copy(cells = cells - cellsToClear)
    }

    fun clearPlacements(placementIds: Set<Long>): Board {
        return copy(cells = cells.filterValues { it.placementId !in placementIds })
    }

    fun isFilled(coord: CellCoord): Boolean = cells[coord] != null

    companion object {
        const val SIZE = 9
    }
}

data class Placement(
    val board: Board,
    val placementId: Long,
    val pieceType: PieceType,
    val cells: Set<CellCoord>
)
