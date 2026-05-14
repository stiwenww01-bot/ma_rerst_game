package com.varp.blockpuzzlesaga.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.varp.blockpuzzlesaga.R
import com.varp.blockpuzzlesaga.ui.components.BoardBounds
import com.varp.blockpuzzlesaga.ui.components.BoardCanvas
import com.varp.blockpuzzlesaga.ui.components.PieceTray
import com.varp.blockpuzzlesaga.ui.theme.LocalGameColors

@Composable
fun GameScreen(
    uiState: GameUiState,
    onBack: () -> Unit,
    onSettings: () -> Unit,
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
    val board = uiState.boardOverride ?: gameState.board
    val bestScore = uiState.records.maxOfOrNull { it.score } ?: 0
    val colors = LocalGameColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✦",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.clearHighlight
                )
                Text(
                    text = bestScore.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = gameState.score.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonIconButton(text = "⚙", onClick = onSettings)
                NeonIconButton(text = "⌂", onClick = onBack)
            }
        }
        BoardCanvas(
            board = board,
            dragPreview = uiState.dragPreview,
            clearingCells = uiState.clearingCells,
            onBoundsChanged = { boardBounds = it }
        )
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.panelBackground)
                .padding(8.dp)
        ) {
            PieceTray(
                pieces = gameState.availablePieces,
                boardBounds = boardBounds,
                selectedPieceIndex = uiState.selectedPieceIndex,
                enabled = !uiState.isResolvingClear,
                onSelectPiece = onSelectPiece,
                onPreview = onPreview,
                onDrop = onDrop,
                onCancelDrag = onCancelDrag
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRotate,
            enabled = uiState.selectedPieceIndex != null &&
                gameState.rotationManager.remainingRotations > 0 &&
                !uiState.isResolvingClear,
            modifier = Modifier.size(58.dp),
            shape = CircleShape,
            border = BorderStroke(2.dp, colors.boardLine),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.panelBackground,
                contentColor = colors.clearHighlight,
                disabledContainerColor = colors.panelBackground.copy(alpha = 0.55f),
                disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = stringResource(R.string.rotate),
                style = MaterialTheme.typography.headlineSmall
            )
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

@Composable
private fun NeonIconButton(
    text: String,
    onClick: () -> Unit
) {
    val colors = LocalGameColors.current
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp),
        shape = CircleShape,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        border = BorderStroke(2.dp, colors.boardLine),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colors.panelBackground.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
