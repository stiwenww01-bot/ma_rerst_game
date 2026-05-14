package com.varp.blockpuzzlesaga.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.varp.blockpuzzlesaga.domain.model.Piece
import com.varp.blockpuzzlesaga.ui.screens.game.cellFromRootPosition

@Composable
fun PieceTray(
    pieces: List<Piece?>,
    boardBounds: BoardBounds?,
    selectedPieceIndex: Int?,
    enabled: Boolean,
    onSelectPiece: (Int) -> Unit,
    onPreview: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onDrop: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onCancelDrag: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        pieces.forEachIndexed { index, piece ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                if (piece != null) {
                    DraggablePiece(
                        piece = piece,
                        pieceIndex = index,
                        boardBounds = boardBounds,
                        isSelected = selectedPieceIndex == index,
                        enabled = enabled,
                        onSelectPiece = onSelectPiece,
                        onPreview = onPreview,
                        onDrop = onDrop,
                        onCancelDrag = onCancelDrag
                    )
                }
            }
        }
    }
}

@Composable
private fun DraggablePiece(
    piece: Piece,
    pieceIndex: Int,
    boardBounds: BoardBounds?,
    isSelected: Boolean,
    enabled: Boolean,
    onSelectPiece: (Int) -> Unit,
    onPreview: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onDrop: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onCancelDrag: () -> Unit
) {
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var lastCell by remember { mutableStateOf<com.varp.blockpuzzlesaga.domain.model.CellCoord?>(null) }
    var anchorCell by remember { mutableStateOf(com.varp.blockpuzzlesaga.domain.model.CellCoord(0, 0)) }
    var dragOffset by remember(piece) { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .sizeIn(minWidth = 72.dp, minHeight = 72.dp)
            .fillMaxWidth(if (isSelected) 0.92f else 0.82f)
            .aspectRatio(1f)
            .zIndex(if (isDragging) 10f else 0f)
            .alpha(if (!enabled) 0.35f else 1f)
            .clickable(enabled = enabled) { onSelectPiece(pieceIndex) }
            .onGloballyPositioned { coordinates = it }
            .pointerInput(piece, boardBounds, enabled) {
                if (!enabled) return@pointerInput
                val dragLiftOffset = 48.dp.toPx()
                detectDragGestures(
                    onDragStart = { startPosition ->
                        isDragging = true
                        dragOffset = Offset(0f, -dragLiftOffset)
                        anchorCell = pieceAnchorCell(piece, startPosition, coordinates)
                        onSelectPiece(pieceIndex)
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = Offset.Zero
                        lastCell = null
                        onCancelDrag()
                    },
                    onDragEnd = {
                        onDrop(pieceIndex, lastCell)
                        isDragging = false
                        dragOffset = Offset.Zero
                        lastCell = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                        val rootPosition = coordinates?.localToRoot(change.position)?.let { root ->
                            Offset(root.x, root.y - dragLiftOffset)
                        }
                        val cell = rootPosition?.let { root ->
                            boardBounds?.let { bounds ->
                                cellFromRootPosition(
                                    rootX = root.x,
                                    rootY = root.y,
                                    boardLeft = bounds.left,
                                    boardTop = bounds.top,
                                    boardSize = bounds.size
                                )?.let { boardCell ->
                                    com.varp.blockpuzzlesaga.domain.model.CellCoord(
                                        x = boardCell.x - anchorCell.x,
                                        y = boardCell.y - anchorCell.y
                                    )
                                }
                            }
                        }
                        lastCell = cell
                        onPreview(pieceIndex, cell)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        PieceCanvas(
            piece = piece,
            modifier = Modifier
                .graphicsLayer {
                    translationX = dragOffset.x
                    translationY = dragOffset.y
                    scaleX = if (isDragging) 1.04f else 1f
                    scaleY = if (isDragging) 1.04f else 1f
                    shadowElevation = if (isDragging) 16.dp.toPx() else 0f
                }
        )
    }
}

private fun pieceAnchorCell(
    piece: Piece,
    position: Offset,
    coordinates: LayoutCoordinates?
): com.varp.blockpuzzlesaga.domain.model.CellCoord {
    val size = coordinates?.size ?: return com.varp.blockpuzzlesaga.domain.model.CellCoord(0, 0)
    val maxX = piece.cells.maxOf { it.x } + 1
    val maxY = piece.cells.maxOf { it.y } + 1
    val grid = maxOf(maxX, maxY, 3)
    val cellSize = minOf(size.width, size.height).toFloat() / grid
    val left = (size.width - cellSize * maxX) / 2f
    val top = (size.height - cellSize * maxY) / 2f
    val rawX = ((position.x - left) / cellSize).toInt()
    val rawY = ((position.y - top) / cellSize).toInt()
    val target = com.varp.blockpuzzlesaga.domain.model.CellCoord(rawX, rawY)
    if (target in piece.cells) return target

    return piece.cells.minBy { cell ->
        val dx = cell.x - rawX
        val dy = cell.y - rawY
        dx * dx + dy * dy
    }
}
