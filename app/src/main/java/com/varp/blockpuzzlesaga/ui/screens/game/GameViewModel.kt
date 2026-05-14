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
            _uiState.update {
                it.copy(
                    gameState = state,
                    selectedPieceIndex = null,
                    dragPreview = null,
                    boardOverride = null,
                    clearingCells = emptySet(),
                    spaceFact = null,
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

    fun clearDragPreview() {
        _uiState.update { it.copy(dragPreview = null) }
    }

    fun dropPiece(pieceIndex: Int, boardCell: CellCoord?) {
        if (_uiState.value.isResolvingClear) return
        val origin = boardCell ?: run {
            clearDragPreview()
            return
        }
        when (val result = _uiState.value.gameState.placePiece(pieceIndex, origin.x, origin.y)) {
            is MoveResult.Invalid -> clearDragPreview()
            is MoveResult.Placed -> {
                viewModelScope.launch {
                    val clearingCells = result.clearedCells + result.collapsedCells
                    if (clearingCells.isNotEmpty()) {
                        val fact = nextSpaceFact()
                        _uiState.update {
                            it.copy(
                                gameState = result.state.copy(board = result.boardBeforeClear),
                                selectedPieceIndex = null,
                                dragPreview = null,
                                boardOverride = result.boardBeforeClear,
                                clearingCells = clearingCells,
                                spaceFact = fact,
                                isResolvingClear = true
                            )
                        }
                        delay(CLEAR_HIGHLIGHT_MILLIS)
                    }
                    if (result.state.gameOver) {
                        recordsRepository.saveRecord("overall", result.state.score)
                    }
                    gameRepository.saveGame(result.state)
                    _uiState.update {
                        it.copy(
                            gameState = result.state,
                            records = recordsRepository.getRecords(),
                            selectedPieceIndex = null,
                            dragPreview = null,
                            boardOverride = null,
                            clearingCells = emptySet(),
                            isResolvingClear = false
                        )
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
            _uiState.update {
                it.copy(
                    gameState = state,
                    dragPreview = null,
                    boardOverride = null,
                    clearingCells = emptySet(),
                    isResolvingClear = false
                )
            }
        }
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

        val SpaceFacts = listOf(
            "На Венере день длиннее года.",
            "Солнце держит около 99,8% массы системы.",
            "Следы на Луне могут жить миллионы лет.",
            "У Юпитера больше 90 известных спутников.",
            "Свет Солнца летит к Земле около 8 минут.",
            "Марс красный из-за оксида железа в пыли.",
            "Кольца Сатурна состоят из льда и камня.",
            "МКС облетает Землю примерно за 90 минут.",
            "Луна каждый год отдаляется примерно на 4 см.",
            "На Нептуне дуют самые быстрые ветры планет.",
            "Один год на Меркурии длится 88 земных дней.",
            "Уран вращается почти лежа на боку.",
            "Плутон меньше земной Луны.",
            "Кометы оставляют хвосты из газа и пыли.",
            "Млечный Путь содержит сотни миллиардов звезд.",
            "Черная дыра не светит, но видна по влиянию.",
            "Скафандр защищает от вакуума и перепадов тепла.",
            "Первый спутник Земли запустили в 1957 году.",
            "На Луне нет ветра и привычной погоды.",
            "Ганимед больше планеты Меркурий.",
            "Титан имеет плотную атмосферу.",
            "На Марсе есть крупнейший вулкан системы.",
            "Астероиды часто богаты металлами и камнем.",
            "Полярные сияния бывают и на других планетах.",
            "В космосе звук не идет через вакуум.",
            "Звезды рождаются в облаках газа и пыли.",
            "Белые карлики остывают миллиарды лет.",
            "Сутки на Юпитере длятся около 10 часов.",
            "На Венере облака содержат серную кислоту.",
            "Лед есть в кратерах у полюсов Луны."
        )
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
