package com.varp.blockpuzzlesaga.domain.logic

import com.varp.blockpuzzlesaga.domain.model.Piece

data class RotationManager(
    val remainingRotations: Int = MAX_ROTATIONS
) {
    fun rotate(piece: Piece): RotationResult {
        return if (remainingRotations <= 0) {
            RotationResult(piece = piece, manager = this, rotated = false)
        } else {
            RotationResult(
                piece = piece.rotatedClockwise(),
                manager = copy(remainingRotations = remainingRotations - 1),
                rotated = true
            )
        }
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
