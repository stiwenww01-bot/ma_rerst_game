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
import androidx.compose.ui.graphics.drawscope.translate
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
    drawBoardChrome: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInRoot()
                val minDimension = coordinates.size.minDimension().toFloat()
                onBoundsChanged(
                    BoardBounds(
                        left = position.x + (coordinates.size.width - minDimension) / 2f,
                        top = position.y + (coordinates.size.height - minDimension) / 2f,
                        size = minDimension
                    )
                )
            }
    ) {
        val boardSize = size.minDimension
        val cellSize = boardSize / Board.SIZE
        val boardLeft = (size.width - boardSize) / 2f
        val boardTop = (size.height - boardSize) / 2f

        translate(left = boardLeft, top = boardTop) {
            if (drawBoardChrome) {
                drawRect(
                    color = colors.boardBackground,
                    size = Size(boardSize, boardSize)
                )
            }

            board.cells.forEach { (coord, placedCell) ->
                drawCell(
                    coord = coord,
                    cellSize = cellSize,
                    color = colors.piecePalette[placedCell.colorIndex.mod(colors.piecePalette.size)]
                )
            }

            dragPreview?.cells?.forEach { coord ->
                if (coord.x in 0 until Board.SIZE && coord.y in 0 until Board.SIZE) {
                    drawPlacementPreviewCell(
                        coord = coord,
                        cellSize = cellSize,
                        color = if (dragPreview.isValid) {
                            colors.piecePalette[dragPreview.colorIndex.mod(colors.piecePalette.size)]
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

            if (drawBoardChrome) {
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
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPlacementPreviewCell(
    coord: CellCoord,
    cellSize: Float,
    color: Color
) {
    val inset = cellSize * 0.08f
    val topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset)
    val size = Size(cellSize - inset * 2, cellSize - inset * 2)
    val radius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.14f, cellSize * 0.14f)
    drawRoundRect(
        color = color.copy(alpha = 0.2f),
        topLeft = topLeft,
        size = size,
        cornerRadius = radius
    )
    drawRoundRect(
        color = color.copy(alpha = 1f),
        topLeft = topLeft,
        size = size,
        cornerRadius = radius,
        style = Stroke(width = 4.5f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.58f),
        topLeft = Offset(topLeft.x + inset * 0.45f, topLeft.y + inset * 0.45f),
        size = Size(size.width - inset * 0.9f, size.height - inset * 0.9f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.1f, cellSize * 0.1f),
        style = Stroke(width = 1.6f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawClearingCell(
    coord: CellCoord,
    cellSize: Float,
    color: Color
) {
    val inset = cellSize * 0.03f
    val borderStroke = 6f
    val borderInset = maxOf(inset, borderStroke / 2f + 0.5f)
    drawRoundRect(
        color = color.copy(alpha = 0.52f),
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.18f, cellSize * 0.18f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.26f),
        topLeft = Offset(coord.x * cellSize + inset * 2.2f, coord.y * cellSize + inset * 2.2f),
        size = Size(cellSize - inset * 4.4f, cellSize - inset * 4.4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.14f, cellSize * 0.14f)
    )
    drawRoundRect(
        color = color.copy(alpha = 1f),
        topLeft = Offset(coord.x * cellSize + borderInset, coord.y * cellSize + borderInset),
        size = Size(cellSize - borderInset * 2, cellSize - borderInset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.2f, cellSize * 0.2f),
        style = Stroke(width = borderStroke)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCell(
    coord: CellCoord,
    cellSize: Float,
    color: androidx.compose.ui.graphics.Color
) {
    val inset = cellSize * 0.08f
    val glowStroke = 7f
    val glowInset = maxOf(inset * 0.75f, glowStroke / 2f + 0.5f)
    drawRoundRect(
        color = color.copy(alpha = color.alpha * 0.96f),
        topLeft = Offset(coord.x * cellSize + inset, coord.y * cellSize + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.12f, cellSize * 0.12f)
    )
    drawRoundRect(
        color = color.copy(alpha = color.alpha * 0.65f),
        topLeft = Offset(coord.x * cellSize + glowInset, coord.y * cellSize + glowInset),
        size = Size(cellSize - glowInset * 2, cellSize - glowInset * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.16f, cellSize * 0.16f),
        style = Stroke(width = glowStroke)
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
