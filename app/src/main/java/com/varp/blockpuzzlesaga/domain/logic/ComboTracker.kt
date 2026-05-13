package com.varp.blockpuzzlesaga.domain.logic

import com.varp.blockpuzzlesaga.domain.model.CellCoord
import com.varp.blockpuzzlesaga.domain.model.PieceType

data class ComboTracker(
    val currentType: PieceType? = null,
    val count: Int = 0,
    val placements: List<TrackedPlacement> = emptyList()
) {
    fun record(placement: TrackedPlacement): ComboUpdate {
        val nextPlacements = if (placement.pieceType == currentType) {
            placements + placement
        } else {
            listOf(placement)
        }
        val nextCount = nextPlacements.size
        return if (nextCount == COLLAPSE_CHAIN_LENGTH) {
            ComboUpdate(
                tracker = ComboTracker(),
                collapse = CollapseEvent(nextPlacements)
            )
        } else {
            ComboUpdate(
                tracker = ComboTracker(
                    currentType = placement.pieceType,
                    count = nextCount,
                    placements = nextPlacements
                ),
                collapse = null
            )
        }
    }

    companion object {
        const val COLLAPSE_CHAIN_LENGTH = 3
    }
}

data class TrackedPlacement(
    val pieceType: PieceType,
    val placementId: Long,
    val cells: Set<CellCoord>
)

data class CollapseEvent(
    val placements: List<TrackedPlacement>
) {
    val placementIds: Set<Long> = placements.map { it.placementId }.toSet()
    val cells: Set<CellCoord> = placements.flatMap { it.cells }.toSet()
}

data class ComboUpdate(
    val tracker: ComboTracker,
    val collapse: CollapseEvent?
)
