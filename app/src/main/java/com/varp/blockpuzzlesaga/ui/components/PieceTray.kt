package com.varp.blockpuzzlesaga.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.varp.blockpuzzlesaga.domain.model.Piece
import com.varp.blockpuzzlesaga.ui.screens.game.cellFromRootPosition
import com.varp.blockpuzzlesaga.ui.theme.LocalGameColors
import kotlin.math.roundToInt

@Composable
fun PieceTray(
    pieces: List<Piece?>,
    boardBounds: BoardBounds?,
    selectedPieceIndex: Int?,
    onSelectPiece: (Int) -> Unit,
    onPreview: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onDrop: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onCancelDrag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.trayBackground, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        pieces.forEachIndexed { index, piece ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (piece != null) {
                    DraggablePiece(
                        piece = piece,
                        pieceIndex = index,
                        boardBounds = boardBounds,
                        isSelected = selectedPieceIndex == index,
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
    onSelectPiece: (Int) -> Unit,
    onPreview: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onDrop: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onCancelDrag: () -> Unit
) {
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var lastCell by remember { mutableStateOf<com.varp.blockpuzzlesaga.domain.model.CellCoord?>(null) }

    Box(
        modifier = Modifier
            .sizeIn(minWidth = 72.dp, minHeight = 72.dp)
            .fillMaxWidth(if (isSelected) 0.92f else 0.82f)
            .aspectRatio(1f)
            .zIndex(if (dragOffset != Offset.Zero) 2f else 0f)
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .clickable { onSelectPiece(pieceIndex) }
            .onGloballyPositioned { coordinates = it }
            .pointerInput(piece, boardBounds) {
                detectDragGestures(
                    onDragStart = {
                        dragOffset = Offset.Zero
                        onSelectPiece(pieceIndex)
                    },
                    onDragCancel = {
                        dragOffset = Offset.Zero
                        lastCell = null
                        onCancelDrag()
                    },
                    onDragEnd = {
                        onDrop(pieceIndex, lastCell)
                        dragOffset = Offset.Zero
                        lastCell = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                        val rootPosition = coordinates?.localToRoot(change.position)
                        val cell = rootPosition?.let { root ->
                            boardBounds?.let { bounds ->
                                cellFromRootPosition(
                                    rootX = root.x,
                                    rootY = root.y,
                                    boardLeft = bounds.left,
                                    boardTop = bounds.top,
                                    boardSize = bounds.size
                                )
                            }
                        }
                        lastCell = cell
                        onPreview(pieceIndex, cell)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        PieceCanvas(piece = piece)
    }
}
