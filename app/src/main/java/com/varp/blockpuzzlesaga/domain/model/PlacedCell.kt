package com.varp.blockpuzzlesaga.domain.model

data class PlacedCell(
    val pieceType: PieceType,
    val placementId: Long,
    val colorIndex: Int
)
