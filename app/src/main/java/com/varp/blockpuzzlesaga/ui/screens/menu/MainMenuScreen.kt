package com.varp.blockpuzzlesaga.ui.screens.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.varp.blockpuzzlesaga.R
import com.varp.blockpuzzlesaga.data.db.RecordEntity
import com.varp.blockpuzzlesaga.ui.theme.LocalGameColors

@Composable
fun MainMenuScreen(
    records: List<RecordEntity>,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onRecords: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bestScore = records.maxOfOrNull { it.score } ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MenuHeader(onSettings = onSettings)

        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(R.drawable.cosmodoku_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(22.dp))
        GalacticRecordsPanel(bestScore = bestScore)

        Spacer(modifier = Modifier.height(26.dp))
        NeonNewGameButton(
            onClick = onNewGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuSmallButton(
                text = stringResource(R.string.continue_game),
                onClick = onContinue,
                modifier = Modifier.weight(1f)
            )
            MenuSmallButton(
                text = stringResource(R.string.records),
                onClick = onRecords,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MenuHeader(onSettings: () -> Unit) {
    val colors = LocalGameColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.space_event_rocket),
                contentDescription = null,
                modifier = Modifier.size(54.dp),
                contentScale = ContentScale.Fit
            )
            Column {
                Text(
                    text = "TIME-LIMITED EVENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "2d 3h",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.clearHighlight,
                    fontWeight = FontWeight.Black
                )
            }
        }
        OutlinedButton(
            onClick = onSettings,
            modifier = Modifier.size(54.dp),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(2.dp, colors.boardLine),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = colors.panelBackground.copy(alpha = 0.72f),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Image(
                painter = painterResource(R.drawable.space_icon_settings),
                contentDescription = stringResource(R.string.settings),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun GalacticRecordsPanel(bestScore: Int) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF8AB6D9).copy(alpha = 0.82f)),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color(0xD607142D)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                text = stringResource(R.string.galactic_records),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            BestRecordRow(bestScore = bestScore)
            RecordPeriodRow(starColor = Color(0xFFFFD56A), label = "За год", score = bestScore)
            RecordPeriodRow(starColor = Color(0xFFFF7CC9), label = "За месяц", score = bestScore)
            RecordPeriodRow(starColor = Color(0xFFC4D8FF), label = "За неделю", score = bestScore)
            RecordPeriodRow(starColor = Color(0xFF36E7FF), label = "Сегодня", score = bestScore)
        }
    }
}

@Composable
private fun BestRecordRow(bestScore: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF5C3308),
                        Color(0xFFDDA54D),
                        Color(0xFF70400D)
                    )
                )
            )
            .border(2.dp, Color(0xFFFFE08A), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Text(
            text = "★★★★  За все время",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFF7D0),
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = bestScore.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFFFF7D0),
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun RecordPeriodRow(
    starColor: Color,
    label: String,
    score: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "★",
                style = MaterialTheme.typography.titleLarge,
                color = starColor,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFE5B5),
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFE5B5),
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun NeonNewGameButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF29FFF6),
                        Color(0xFF0D8FD1),
                        Color(0xFF062D5F)
                    )
                )
            )
            .border(3.dp, Color(0xFF85FFFF), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.new_game).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun MenuSmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colors.boardLine.copy(alpha = 0.8f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colors.panelBackground.copy(alpha = 0.72f),
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
