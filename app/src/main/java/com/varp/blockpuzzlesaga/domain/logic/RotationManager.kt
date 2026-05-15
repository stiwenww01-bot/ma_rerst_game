package com.varp.blockpuzzlesaga.domain.logic

import com.varp.blockpuzzlesaga.domain.model.Piece

data class RotationManager(
    val remainingRotations: Int = MAX_ROTATIONS,
    val rotatedPieceIndices: Set<Int> = emptySet()
) {
    fun canRotate(pieceIndex: Int): Boolean {
        return pieceIndex in rotatedPieceIndices || remainingRotations > 0
    }

    fun rotate(piece: Piece, pieceIndex: Int): RotationResult {
        return if (!canRotate(pieceIndex)) {
            RotationResult(piece = piece, manager = this, rotated = false)
        } else {
            val alreadyRotated = pieceIndex in rotatedPieceIndices
            RotationResult(
                piece = piece.rotatedClockwise(),
                manager = if (alreadyRotated) {
                    this
                } else {
                    copy(
                        remainingRotations = remainingRotations - 1,
                        rotatedPieceIndices = rotatedPieceIndices + pieceIndex
                    )
                },
                rotated = true
            )
        }
    }

    fun releasePiece(pieceIndex: Int): RotationManager {
        return copy(rotatedPieceIndices = rotatedPieceIndices - pieceIndex)
    }

    fun addBonusRotation(): RotationManager {
        return copy(remainingRotations = remainingRotations + 1)
    }

    companion object {
        const val MAX_ROTATIONS = 3
    }
}

data class RotationResult(
    val piece: Piece,
    val manager: RotationManager,
    val rotated: Boolean
)
