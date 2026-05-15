package com.varp.blockpuzzlesaga.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.varp.blockpuzzlesaga.data.repository.GameRepository
import com.varp.blockpuzzlesaga.data.repository.RecordsRepository
import com.varp.blockpuzzlesaga.domain.model.Board
import com.varp.blockpuzzlesaga.domain.model.CellCoord
import com.varp.blockpuzzlesaga.domain.model.GameState
import com.varp.blockpuzzlesaga.domain.model.MoveResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository,
    private val recordsRepository: RecordsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    private var spaceFactsDeck = SpaceFacts.shuffled()
    private var spaceFactIndex = 0
    private var lastSpaceFact: String? = null
    private var soundEventId = 0
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val savedGame = gameRepository.loadGame() ?: GameState()
            _uiState.update {
                it.copy(
                    gameState = savedGame,
                    records = recordsRepository.getRecords(),
                    isLoading = false
                )
            }
        }
    }

    fun newGame() {
        viewModelScope.launch {
            val state = GameState()
            gameRepository.saveGame(state)
            val sound = nextSoundEvent(GameSoundEvent.NewGame)
            _uiState.update {
                it.copy(
                    gameState = state,
                    selectedPieceIndex = null,
                    dragPreview = null,
                    boardOverride = null,
                    clearingCells = emptySet(),
                    feedbackCells = emptySet(),
                    spaceFact = null,
                    spinBonusText = null,
                    comboText = null,
                    soundEvent = sound.first,
                    soundEventId = sound.second,
                    isResolvingClear = false,
                    isLoading = false
                )
            }
        }
    }

    fun selectPiece(index: Int) {
        if (_uiState.value.isResolvingClear) return
        _uiState.update { it.copy(selectedPieceIndex = index) }
    }

    fun rotateSelectedPiece() {
        if (_uiState.value.isResolvingClear) return
        val index = _uiState.value.selectedPieceIndex ?: return
        val nextState = _uiState.value.gameState.rotatePiece(index)
        if (nextState == _uiState.value.gameState) return
        persist(nextState)
    }

    fun updateDragPreview(pieceIndex: Int, boardCell: CellCoord?) {
        if (_uiState.value.isResolvingClear) return
        val gameState = _uiState.value.gameState
        val piece = gameState.availablePieces.getOrNull(pieceIndex) ?: return
        val preview = boardCell?.let { origin ->
            val cells = piece.cells.map { CellCoord(origin.x + it.x, origin.y + it.y) }.toSet()
            DragPreview(
                pieceIndex = pieceIndex,
                origin = origin,
                cells = cells,
                isValid = gameState.board.canPlace(piece, origin.x, origin.y),
                colorIndex = piece.colorIndex
            )
        }
        _uiState.update { it.copy(selectedPieceIndex = pieceIndex, dragPreview = preview) }
    }

    fun clearDragPreview(soundEvent: GameSoundEvent? = null) {
        val sound = soundEvent?.let(::nextSoundEvent)
        _uiState.update {
            it.copy(
                dragPreview = null,
                soundEvent = sound?.first ?: it.soundEvent,
                soundEventId = sound?.second ?: it.soundEventId
            )
        }
    }

    fun dropPiece(pieceIndex: Int, boardCell: CellCoord?) {
        if (_uiState.value.isResolvingClear) return
        val origin = boardCell ?: run {
            clearDragPreview(GameSoundEvent.Invalid)
            return
        }
        when (val result = _uiState.value.gameState.placePiece(pieceIndex, origin.x, origin.y)) {
            is MoveResult.Invalid -> clearDragPreview(GameSoundEvent.Invalid)
            is MoveResult.Placed -> {
                val clearingCells = result.clearedCells + result.collapsedCells
                val comboText = if (result.clearGroupCount >= 2) "${result.clearGroupCount}x комбо" else null
                val spinBonusText = if (result.spinBonusAwarded) "+1 спин" else null
                val clearSound = if (clearingCells.isNotEmpty()) {
                    nextSoundEvent(if (result.spinBonusAwarded) GameSoundEvent.Bonus else GameSoundEvent.Clear)
                } else {
                    null
                }
                val placeSound = if (clearingCells.isEmpty()) nextSoundEvent(GameSoundEvent.Place) else null

                if (clearingCells.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            gameState = result.state.copy(board = result.boardBeforeClear),
                            selectedPieceIndex = null,
                            dragPreview = null,
                            boardOverride = result.boardBeforeClear,
                            clearingCells = clearingCells,
                            feedbackCells = clearingCells,
                            spaceFact = nextSpaceFact(),
                            spinBonusText = spinBonusText,
                            comboText = comboText,
                            soundEvent = clearSound?.first ?: it.soundEvent,
                            soundEventId = clearSound?.second ?: it.soundEventId,
                            isResolvingClear = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            gameState = result.state,
                            selectedPieceIndex = null,
                            dragPreview = null,
                            boardOverride = null,
                            clearingCells = emptySet(),
                            feedbackCells = emptySet(),
                            spinBonusText = null,
                            comboText = null,
                            soundEvent = placeSound?.first ?: it.soundEvent,
                            soundEventId = placeSound?.second ?: it.soundEventId,
                            isResolvingClear = false
                        )
                    }
                }

                viewModelScope.launch {
                    if (clearingCells.isNotEmpty()) {
                        delay(CLEAR_HIGHLIGHT_MILLIS)
                    }
                    if (result.state.gameOver) {
                        recordsRepository.saveRecord("overall", result.state.score)
                    }
                    gameRepository.saveGame(result.state)

                    if (clearingCells.isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                gameState = result.state,
                                records = recordsRepository.getRecords(),
                                selectedPieceIndex = null,
                                dragPreview = null,
                                boardOverride = null,
                                clearingCells = emptySet(),
                                feedbackCells = if (spinBonusText != null || comboText != null) {
                                    it.feedbackCells
                                } else {
                                    emptySet()
                                },
                                spinBonusText = spinBonusText,
                                comboText = comboText,
                                isResolvingClear = false
                            )
                        }
                    } else if (result.state.gameOver) {
                        _uiState.update { it.copy(records = recordsRepository.getRecords()) }
                    }

                    if (clearingCells.isNotEmpty() && (spinBonusText != null || comboText != null)) {
                        delay(BONUS_MESSAGE_MILLIS - CLEAR_HIGHLIGHT_MILLIS)
                        _uiState.update {
                            it.copy(
                                feedbackCells = emptySet(),
                                spinBonusText = null,
                                comboText = null
                            )
                        }
                    }
                }
            }
        }
    }

    fun refreshRecords() {
        viewModelScope.launch {
            _uiState.update { it.copy(records = recordsRepository.getRecords()) }
        }
    }

    private fun persist(state: GameState) {
        viewModelScope.launch {
            gameRepository.saveGame(state)
            val sound = nextSoundEvent(GameSoundEvent.Rotate)
            _uiState.update {
                it.copy(
                    gameState = state,
                    dragPreview = null,
                    boardOverride = null,
                    clearingCells = emptySet(),
                    feedbackCells = emptySet(),
                    spinBonusText = null,
                    comboText = null,
                    soundEvent = sound.first,
                    soundEventId = sound.second,
                    isResolvingClear = false
                )
            }
        }
    }

    private fun nextSoundEvent(event: GameSoundEvent): Pair<GameSoundEvent, Int> {
        soundEventId += 1
        return event to soundEventId
    }

    private fun nextSpaceFact(): String {
        if (spaceFactIndex >= spaceFactsDeck.size) {
            spaceFactsDeck = SpaceFacts.shuffled()
            spaceFactIndex = 0
            if (spaceFactsDeck.firstOrNull() == lastSpaceFact && spaceFactsDeck.size > 1) {
                spaceFactsDeck = spaceFactsDeck.drop(1) + spaceFactsDeck.first()
            }
        }
        val fact = spaceFactsDeck[spaceFactIndex]
        spaceFactIndex += 1
        lastSpaceFact = fact
        return fact
    }

    private companion object {
        const val CLEAR_HIGHLIGHT_MILLIS = 320L
        const val BONUS_MESSAGE_MILLIS = 2_300L

        val SpaceFacts = buildSpaceFacts()
    }

    class Factory(
        private val gameRepository: GameRepository,
        private val recordsRepository: RecordsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameViewModel(gameRepository, recordsRepository) as T
        }
    }
}

fun cellFromRootPosition(
    rootX: Float,
    rootY: Float,
    boardLeft: Float,
    boardTop: Float,
    boardSize: Float
): CellCoord? {
    if (boardSize <= 0f) return null
    if (rootX < boardLeft || rootY < boardTop || rootX >= boardLeft + boardSize || rootY >= boardTop + boardSize) {
        return null
    }
    val cellSize = boardSize / Board.SIZE
    return CellCoord(
        x = ((rootX - boardLeft) / cellSize).toInt().coerceIn(0, Board.SIZE - 1),
        y = ((rootY - boardTop) / cellSize).toInt().coerceIn(0, Board.SIZE - 1)
    )
}
