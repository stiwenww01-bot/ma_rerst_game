package com.varp.blockpuzzlesaga.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import com.varp.blockpuzzlesaga.domain.model.Board
import com.varp.blockpuzzlesaga.domain.model.CellCoord
import com.varp.blockpuzzlesaga.ui.screens.game.DragPreview
import com.varp.blockpuzzlesaga.ui.theme.LocalGameColors

data class BoardBounds(
    val left: Float,
    val top: Float,
    val size: Float
)

@Composable
fun BoardCanvas(
    board: Board,
    dragPreview: DragPreview?,
    onBoundsChanged: (BoardBounds) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInRoot()
                onBoundsChanged(
                    BoardBounds(
                        left = position.x,
                        top = position.y,
                        size = coordinates.size.minDimension().toFloat()
                    )
                )
            }
    ) {
        val boardSize = size.minDimension
        val cellSize = boardSize / Board.SIZE

        drawRect(
            color = colors.boardBackground,
            size = Size(boardSize, boardSize)
        )

        board.cells.forEach { (coord, placedCell) ->
            drawCell(
                coord = coord,
                cellSize = cellSize,
                color = if (placedCell.colorIndex % 2 == 0) colors.boardBlock else colors.boardBlockAlt
            )
        }

        dragPreview?.cells?.forEach { coord ->
            if (coord.x in 0 until Board.SIZE && coord.y in 0 until Board.SIZE) {
                drawCell(
                    coord = coord,
                    cellSize = cellSize,
                    color = if (dragPreview.isValid) colors.previewValid else colors.previewInvalid
                )
            }
        }

        for (index in 0..Board.SIZE) {
            val width = if (index % 3 == 0) 3f else 1f
            val offset = index * cellSize
            drawLine(
                color = colors.boardLine,
                start = Offset(offset, 0f),
                end = Offset(offset, boardSize),
                strokeWidth = width
            )
            drawLine(
                color = colors.boardLine,
                start = Offset(0f, offset),
                end = Offset(boardSize, offset),
                strokeWidth = width
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCell(
    coord: CellCoord,
    cellSize: Float,
    color: androidx.compose.ui.graphics.Color
) {
    val inset = cellSize * 0.08f
    drawRoundRect(
        color = color,
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.12f, cellSize * 0.12f)
    )
    drawRoundRect(
        color = color.copy(alpha = 0.35f),
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.12f, cellSize * 0.12f),
        style = Stroke(width = 1.5f)
    )
}

private fun IntSize.minDimension(): Int = minOf(width, height)
