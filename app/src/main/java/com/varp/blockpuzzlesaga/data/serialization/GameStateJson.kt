package com.varp.blockpuzzlesaga.data.serialization

import com.varp.blockpuzzlesaga.data.db.GameStateEntity
import com.varp.blockpuzzlesaga.domain.logic.ComboTracker
import com.varp.blockpuzzlesaga.domain.logic.RotationManager
import com.varp.blockpuzzlesaga.domain.logic.TrackedPlacement
import com.varp.blockpuzzlesaga.domain.model.Board
import com.varp.blockpuzzlesaga.domain.model.CellCoord
import com.varp.blockpuzzlesaga.domain.model.GameState
import com.varp.blockpuzzlesaga.domain.model.Piece
import com.varp.blockpuzzlesaga.domain.model.PieceType
import com.varp.blockpuzzlesaga.domain.model.PlacedCell
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameStateJson(
    private val json: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
) {
    fun toEntity(state: GameState, updatedAtMillis: Long): GameStateEntity {
        return GameStateEntity(
            boardJson = json.encodeToString(state.board.toDto()),
            availablePiecesJson = json.encodeToString(state.availablePieces.map { it?.toDto() }),
            comboTrackerJson = json.encodeToString(state.comboTracker.toDto()),
            score = state.score,
            remainingRotations = state.rotationManager.remainingRotations,
            gameOver = state.gameOver,
            updatedAtMillis = updatedAtMillis
        )
    }

    fun fromEntity(entity: GameStateEntity): GameState {
        return GameState(
            board = json.decodeFromString<BoardDto>(entity.boardJson).toDomain(),
            availablePieces = json.decodeFromString<List<PieceDto?>>(entity.availablePiecesJson)
                .map { it?.toDomain() },
            score = entity.score,
            comboTracker = json.decodeFromString<ComboTrackerDto>(entity.comboTrackerJson).toDomain(),
            rotationManager = RotationManager(entity.remainingRotations),
            gameOver = entity.gameOver
        )
    }
}

@Serializable
private data class BoardDto(
    val cells: List<BoardCellDto>,
    val nextPlacementId: Long
)

@Serializable
private data class BoardCellDto(
    val coord: CellCoordDto,
    val pieceType: PieceType,
    val placementId: Long,
    val colorIndex: Int
)

@Serializable
private data class PieceDto(
    val type: PieceType,
    val cells: List<CellCoordDto>,
    val colorIndex: Int
)

@Serializable
private data class CellCoordDto(
    val x: Int,
    val y: Int
)

@Serializable
private data class ComboTrackerDto(
    val currentType: PieceType?,
    val count: Int,
    val placements: List<TrackedPlacementDto>
)

@Serializable
private data class TrackedPlacementDto(
    val pieceType: PieceType,
    val placementId: Long,
    val cells: List<CellCoordDto>
)

private fun Board.toDto(): BoardDto {
    return BoardDto(
        cells = cells.map { (coord, placed) ->
            BoardCellDto(
                coord = coord.toDto(),
                pieceType = placed.pieceType,
                placementId = placed.placementId,
                colorIndex = placed.colorIndex
            )
        },
        nextPlacementId = nextPlacementId
    )
}

private fun BoardDto.toDomain(): Board {
    return Board(
        cells = cells.associate { cell ->
            cell.coord.toDomain() to PlacedCell(
                pieceType = cell.pieceType,
                placementId = cell.placementId,
                colorIndex = cell.colorIndex
            )
        },
        nextPlacementId = nextPlacementId
    )
}

private fun Piece.toDto(): PieceDto {
    return PieceDto(
        type = type,
        cells = cells.map { it.toDto() },
        colorIndex = colorIndex
    )
}

private fun PieceDto.toDomain(): Piece {
    return Piece(
        type = type,
        cells = cells.map { it.toDomain() }.toSet(),
        colorIndex = colorIndex
    )
}

private fun CellCoord.toDto(): CellCoordDto = CellCoordDto(x = x, y = y)

private fun CellCoordDto.toDomain(): CellCoord = CellCoord(x = x, y = y)

private fun ComboTracker.toDto(): ComboTrackerDto {
    return ComboTrackerDto(
        currentType = currentType,
        count = count,
        placements = placements.map { it.toDto() }
    )
}

private fun ComboTrackerDto.toDomain(): ComboTracker {
    return ComboTracker(
        currentType = currentType,
        count = count,
        placements = placements.map { it.toDomain() }
    )
}

private fun TrackedPlacement.toDto(): TrackedPlacementDto {
    return TrackedPlacementDto(
        pieceType = pieceType,
        placementId = placementId,
        cells = cells.map { it.toDto() }
    )
}

private fun TrackedPlacementDto.toDomain(): TrackedPlacement {
    return TrackedPlacement(
        pieceType = pieceType,
        placementId = placementId,
        cells = cells.map { it.toDomain() }.toSet()
    )
}
