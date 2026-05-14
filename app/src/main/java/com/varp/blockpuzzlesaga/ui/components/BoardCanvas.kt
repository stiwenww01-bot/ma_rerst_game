package com.varp.blockpuzzlesaga.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
    clearingCells: Set<CellCoord>,
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
                color = colors.piecePalette[placedCell.colorIndex.mod(colors.piecePalette.size)]
            )
        }

        dragPreview?.cells?.forEach { coord ->
            if (coord.x in 0 until Board.SIZE && coord.y in 0 until Board.SIZE) {
                drawCell(
                    coord = coord,
                    cellSize = cellSize,
                    color = if (dragPreview.isValid) {
                        colors.piecePalette[dragPreview.colorIndex.mod(colors.piecePalette.size)].copy(alpha = 0.78f)
                    } else {
                        colors.previewInvalid
                    }
                )
            }
        }

        clearingCells.forEach { coord ->
            if (coord.x in 0 until Board.SIZE && coord.y in 0 until Board.SIZE) {
                drawClearingCell(
                    coord = coord,
                    cellSize = cellSize,
                    color = colors.clearHighlight
                )
            }
        }

        drawRect(
            color = colors.boardLine.copy(alpha = 0.35f),
            size = Size(boardSize, boardSize),
            style = Stroke(width = 10f)
        )
        for (index in 0..Board.SIZE) {
            val width = if (index % 3 == 0) 3f else 1f
            val offset = index * cellSize
            drawLine(
                color = colors.boardLine.copy(alpha = if (index % 3 == 0) 0.95f else 0.48f),
                start = Offset(offset, 0f),
                end = Offset(offset, boardSize),
                strokeWidth = width
            )
            drawLine(
                color = colors.boardLine.copy(alpha = if (index % 3 == 0) 0.95f else 0.48f),
                start = Offset(0f, offset),
                end = Offset(boardSize, offset),
                strokeWidth = width
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawClearingCell(
    coord: CellCoord,
    cellSize: Float,
    color: Color
) {
    val inset = cellSize * 0.03f
    drawRoundRect(
        color = color.copy(alpha = 0.35f),
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.18f, cellSize * 0.18f)
    )
    drawRoundRect(
        color = color.copy(alpha = 0.95f),
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.18f, cellSize * 0.18f),
        style = Stroke(width = 5f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = cellSize * 0.11f,
        center = Offset(coord.x * cellSize + cellSize * 0.5f, coord.y * cellSize + cellSize * 0.5f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCell(
    coord: CellCoord,
    cellSize: Float,
    color: androidx.compose.ui.graphics.Color
) {
    val inset = cellSize * 0.08f
    drawRoundRect(
        color = color.copy(alpha = color.alpha * 0.96f),
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.12f, cellSize * 0.12f)
    )
    drawRoundRect(
        color = color.copy(alpha = color.alpha * 0.65f),
        topLeft = Offset(coord.x * cellSize + inset * 0.2f, coord.y * cellSize + inset * 0.2f),
        size = Size(cellSize - inset * 0.4f, cellSize - inset * 0.4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.16f, cellSize * 0.16f),
        style = Stroke(width = 7f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = color.alpha * 0.58f),
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.12f, cellSize * 0.12f),
        style = Stroke(width = 2.2f)
    )
}

private fun IntSize.minDimension(): Int = minOf(width, height)
