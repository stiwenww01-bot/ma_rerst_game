package com.varp.blockpuzzlesaga.ui.screens.game

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                .height(88.dp)
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
                NeonIconButton(iconRes = R.drawable.space_icon_settings, fallbackText = "⚙", onClick = onSettings)
                NeonIconButton(iconRes = R.drawable.space_icon_home, fallbackText = "⌂", onClick = onBack)
            }
        }
        SpaceFactBanner(
            fact = uiState.spaceFact,
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            BoardCanvas(
                board = board,
                dragPreview = uiState.dragPreview,
                clearingCells = uiState.clearingCells,
                onBoundsChanged = { boardBounds = it },
                drawBoardChrome = true,
                modifier = Modifier
                    .matchParentSize()
                    .padding(start = 36.dp, top = 40.dp, end = 36.dp, bottom = 40.dp)
            )
            Image(
                painter = painterResource(R.drawable.space_board_frame_overlay),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        PieceTray(
            pieces = gameState.availablePieces,
            boardBounds = boardBounds,
            selectedPieceIndex = uiState.selectedPieceIndex,
            enabled = !uiState.isResolvingClear,
            onSelectPiece = onSelectPiece,
            onPreview = onPreview,
            onDrop = onDrop,
            onCancelDrag = onCancelDrag,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRotate,
            enabled = uiState.selectedPieceIndex != null &&
                gameState.rotationManager.canRotate(uiState.selectedPieceIndex) &&
                !uiState.isResolvingClear,
            modifier = Modifier.size(58.dp),
            shape = CircleShape,
            border = BorderStroke(2.dp, colors.boardLine),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.panelBackground,
                contentColor = colors.clearHighlight,
                disabledContainerColor = colors.panelBackground.copy(alpha = 0.55f),
                disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
            )
        ) {
            Image(
                painter = painterResource(R.drawable.space_icon_rotate),
                contentDescription = stringResource(R.string.rotate),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
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
private fun SpaceFactBanner(
    fact: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (fact != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0x00183B75),
                                Color(0xAA08315F),
                                Color(0x00183B75)
                            )
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fact,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBFFBFF),
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun NeonIconButton(
    @DrawableRes iconRes: Int,
    fallbackText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = fallbackText,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
