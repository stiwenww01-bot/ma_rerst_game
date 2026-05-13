package com.varp.blockpuzzlesaga.domain.model

data class Piece(
    val type: PieceType,
    val cells: Set<CellCoord>,
    val colorIndex: Int = 0
) {
    init {
        require(cells.isNotEmpty()) { "Piece must contain at least one cell." }
        require(cells.all { it.x >= 0 && it.y >= 0 }) { "Piece coordinates must be normalized." }
    }

    val size: Int = cells.size

    fun rotatedClockwise(): Piece {
        val rotated = cells.map { CellCoord(x = it.y, y = -it.x) }
        return copy(cells = normalize(rotated))
    }

    companion object {
        fun of(type: PieceType, vararg cells: Pair<Int, Int>, colorIndex: Int = 0): Piece {
            return Piece(
                type = type,
                cells = normalize(cells.map { CellCoord(it.first, it.second) }),
                colorIndex = colorIndex
            )
        }

        fun normalize(cells: Iterable<CellCoord>): Set<CellCoord> {
            val list = cells.toList()
            require(list.isNotEmpty()) { "Cells must not be empty." }
            val minX = list.minOf { it.x }
            val minY = list.minOf { it.y }
            return list.map { CellCoord(it.x - minX, it.y - minY) }.toSet()
        }
    }
}
