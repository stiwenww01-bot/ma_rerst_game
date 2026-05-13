package com.varp.blockpuzzlesaga.domain.logic

object ScoreCalculator {
    fun placementScore(pieceSize: Int): Int = pieceSize

    fun clearScore(clearCount: Int): Int {
        if (clearCount <= 0) return 0
        return clearCount * POINTS_PER_CLEAR + comboBonus(clearCount)
    }

    fun comboBonus(clearCount: Int): Int {
        return when {
            clearCount >= 4 -> 400
            clearCount == 3 -> 150
            clearCount == 2 -> 50
            else -> 0
        }
    }

    fun collapseScore(clearedCellCount: Int): Int = clearedCellCount * COLLAPSE_POINTS_PER_CELL

    private const val POINTS_PER_CLEAR = 100
    private const val COLLAPSE_POINTS_PER_CELL = 2
}
