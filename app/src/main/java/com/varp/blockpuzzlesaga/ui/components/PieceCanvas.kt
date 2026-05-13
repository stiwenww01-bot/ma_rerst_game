package com.varp.blockpuzzlesaga.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.varp.blockpuzzlesaga.domain.model.Piece
import com.varp.blockpuzzlesaga.ui.theme.LocalGameColors

@Composable
fun PieceCanvas(
    piece: Piece,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
    ) {
        val maxX = piece.cells.maxOf { it.x } + 1
        val maxY = piece.cells.maxOf { it.y } + 1
        val grid = maxOf(maxX, maxY, 3)
        val cellSize = size.minDimension / grid
        val left = (size.width - cellSize * maxX) / 2f
        val top = (size.height - cellSize * maxY) / 2f
        val color = colors.piecePalette[piece.colorIndex.mod(colors.piecePalette.size)]

        piece.cells.forEach { cell ->
            val inset = cellSize * 0.08f
            drawRoundRect(
                color = color,
                topLeft = Offset(left + cell.x * cellSize + inset, top + cell.y * cellSize + inset),
                size = Size(cellSize - inset * 2, cellSize - inset * 2),
                cornerRadius = CornerRadius(cellSize * 0.14f, cellSize * 0.14f)
            )
        }
    }
}
