package com.varp.blockpuzzlesaga.domain.logic

import com.varp.blockpuzzlesaga.domain.model.Piece
import com.varp.blockpuzzlesaga.domain.model.PieceType
import kotlin.math.min
import kotlin.random.Random

class PieceGenerator(
    private val random: Random = Random.Default
) {
    fun generateTray(score: Int = 0): List<Piece> {
        val difficulty = difficultyModifier(score)
        val small = smallPieces.random(random)
        val medium = mediumPieces.random(random)
        val any = allPieces.random(random)
        val pieces = listOf(
            if (random.nextFloat() < difficulty * 0.35f) mediumPieces.random(random) else small,
            if (random.nextFloat() < difficulty) hardPieces.random(random) else medium,
            if (random.nextFloat() < difficulty) hardPieces.random(random) else any
        )
        return pieces.map { piece ->
            piece.copy(colorIndex = random.nextInt(COLOR_COUNT))
        }
    }

    fun allTemplates(): List<Piece> = allPieces

    fun difficultyModifier(score: Int): Float {
        val steps = (score.coerceAtLeast(0) / DIFFICULTY_SCORE_STEP)
        return min(steps * DIFFICULTY_STEP, DIFFICULTY_MAX)
    }

    companion object {
        const val COLOR_COUNT = 3
        const val DIFFICULTY_SCORE_STEP = 3_000
        const val DIFFICULTY_STEP = 0.10f
        const val DIFFICULTY_MAX = 0.60f

        val single = Piece.of(PieceType.SINGLE, 0 to 0)
        val line2 = Piece.of(PieceType.LINE_2, 0 to 0, 1 to 0)
        val line3 = Piece.of(PieceType.LINE_3, 0 to 0, 1 to 0, 2 to 0)
        val line4 = Piece.of(PieceType.LINE_4, 0 to 0, 1 to 0, 2 to 0, 3 to 0)
        val line5 = Piece.of(PieceType.LINE_5, 0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0)
        val square2 = Piece.of(PieceType.SQUARE_2, 0 to 0, 1 to 0, 0 to 1, 1 to 1)
        val square3 = Piece.of(
            PieceType.SQUARE_3,
            0 to 0, 1 to 0, 2 to 0,
            0 to 1, 1 to 1, 2 to 1,
            0 to 2, 1 to 2, 2 to 2
        )
        val lSmall = Piece.of(PieceType.L_SMALL, 0 to 0, 0 to 1, 1 to 1)
        val l = Piece.of(PieceType.L, 0 to 0, 0 to 1, 0 to 2, 1 to 2)
        val j = Piece.of(PieceType.J, 1 to 0, 1 to 1, 1 to 2, 0 to 2)
        val t = Piece.of(PieceType.T, 0 to 0, 1 to 0, 2 to 0, 1 to 1)
        val s = Piece.of(PieceType.S, 1 to 0, 2 to 0, 0 to 1, 1 to 1)
        val z = Piece.of(PieceType.Z, 0 to 0, 1 to 0, 1 to 1, 2 to 1)
        val i = Piece.of(PieceType.I, 0 to 0, 0 to 1, 0 to 2, 0 to 3)
        val o = Piece.of(PieceType.O, 0 to 0, 1 to 0, 0 to 1, 1 to 1)
        val corner5 = Piece.of(PieceType.CORNER_5, 0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2)
        val step3 = Piece.of(PieceType.STEP_3, 0 to 0, 1 to 0, 1 to 1)
        val plus = Piece.of(PieceType.PLUS, 1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2)
        val u = Piece.of(PieceType.U, 0 to 0, 2 to 0, 0 to 1, 1 to 1, 2 to 1)
        val v = Piece.of(PieceType.V, 0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2)
        val shortT = Piece.of(PieceType.SHORT_T, 0 to 0, 1 to 0, 2 to 0, 1 to 1, 1 to 2)
        val longL = Piece.of(PieceType.LONG_L, 0 to 0, 0 to 1, 0 to 2, 0 to 3, 1 to 3)
        val bigJ = Piece.of(PieceType.BIG_J, 1 to 0, 1 to 1, 1 to 2, 1 to 3, 0 to 3)
        val diagonal2 = Piece.of(PieceType.DIAGONAL_2, 0 to 0, 1 to 1)
        val diagonal3 = Piece.of(PieceType.DIAGONAL_3, 0 to 0, 1 to 1, 2 to 2)
        val pentominoP = Piece.of(PieceType.PENTOMINO_P, 0 to 0, 1 to 0, 0 to 1, 1 to 1, 0 to 2)

        private val smallPieces = listOf(single, line2, line3, lSmall, step3, diagonal2)
        private val mediumPieces = listOf(square2, l, j, t, s, z, i, o, diagonal3)
        private val hardPieces = listOf(
            line4,
            line5,
            l,
            j,
            t,
            s,
            z,
            i,
            corner5,
            plus,
            u,
            v,
            shortT,
            longL,
            bigJ,
            diagonal3,
            pentominoP
        )
        private val allPieces = listOf(
            single,
            line2,
            line3,
            line4,
            line5,
            square2,
            lSmall,
            l,
            j,
            t,
            s,
            z,
            i,
            o,
            corner5,
            step3,
            plus,
            u,
            v,
            shortT,
            longL,
            bigJ,
            diagonal2,
            diagonal3,
            pentominoP
        )
    }
}
