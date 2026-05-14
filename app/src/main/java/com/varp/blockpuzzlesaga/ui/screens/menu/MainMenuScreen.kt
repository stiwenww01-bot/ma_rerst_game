package com.varp.blockpuzzlesaga.ui.screens.menu

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
    val colors = LocalGameColors.current
    val bestScore = records.maxOfOrNull { it.score } ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.event_timer),
                style = MaterialTheme.typography.labelLarge,
                color = colors.clearHighlight,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(
                onClick = onSettings,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, colors.boardLine),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colors.panelBackground.copy(alpha = 0.75f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text(text = "⚙")
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "BLOCK\nPUZZLE",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.displayMedium.lineHeight
        )
        Text(
            text = "SAGA",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.clearHighlight,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(34.dp))
        GalacticRecordsPanel(bestScore = bestScore)

        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onNewGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, colors.clearHighlight),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.boardLine,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.new_game).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
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
private fun GalacticRecordsPanel(bestScore: Int) {
    val colors = LocalGameColors.current
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colors.boardLine.copy(alpha = 0.75f)),
        colors = CardDefaults.outlinedCardColors(
            containerColor = colors.panelBackground.copy(alpha = 0.82f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.galactic_records),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.clearHighlight.copy(alpha = 0.22f))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "★★★  ${stringResource(R.string.all_time)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = bestScore.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
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
