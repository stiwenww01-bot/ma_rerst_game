package com.varp.blockpuzzlesaga.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.varp.blockpuzzlesaga.R
import com.varp.blockpuzzlesaga.ui.components.BoardBounds
import com.varp.blockpuzzlesaga.ui.components.BoardCanvas
import com.varp.blockpuzzlesaga.ui.components.PieceTray

@Composable
fun GameScreen(
    uiState: GameUiState,
    onBack: () -> Unit,
    onSelectPiece: (Int) -> Unit,
    onRotate: () -> Unit,
    onPreview: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onDrop: (Int, com.varp.blockpuzzlesaga.domain.model.CellCoord?) -> Unit,
    onCancelDrag: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var boardBounds by remember { mutableStateOf<BoardBounds?>(null) }
    val gameState = uiState.gameState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.score_value, gameState.score),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.rotations_value, gameState.rotationManager.remainingRotations),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        BoardCanvas(
            board = gameState.board,
            dragPreview = uiState.dragPreview,
            onBoundsChanged = { boardBounds = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PieceTray(
            pieces = gameState.availablePieces,
            boardBounds = boardBounds,
            selectedPieceIndex = uiState.selectedPieceIndex,
            onSelectPiece = onSelectPiece,
            onPreview = onPreview,
            onDrop = onDrop,
            onCancelDrag = onCancelDrag
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.back))
            }
            Button(
                onClick = onRotate,
                enabled = uiState.selectedPieceIndex != null && gameState.rotationManager.remainingRotations > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.rotate))
            }
        }
    }

    if (gameState.gameOver) {
        AlertDialog(
            onDismissRequest = onBack,
            title = { Text(text = stringResource(R.string.game_over)) },
            text = { Text(text = stringResource(R.string.final_score_value, gameState.score)) },
            confirmButton = {
                Button(onClick = onRestart) {
                    Text(text = stringResource(R.string.restart))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onBack) {
                    Text(text = stringResource(R.string.back))
                }
            }
        )
    }
}
