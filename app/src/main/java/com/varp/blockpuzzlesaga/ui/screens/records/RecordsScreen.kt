package com.varp.blockpuzzlesaga.ui.screens.records

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
import com.varp.blockpuzzlesaga.data.repository.RecordScopes
import com.varp.blockpuzzlesaga.ui.theme.LocalGameColors

@Composable
fun RecordsScreen(
    records: List<RecordEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val summary = RecordScopes.summary(records)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.galactic_records),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, colors.boardLine.copy(alpha = 0.75f)),
            colors = CardDefaults.outlinedCardColors(containerColor = colors.panelBackground.copy(alpha = 0.86f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RecordHeroRow(summary.overall)
                if (records.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_records),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    RecordLine(label = "За год", score = summary.year)
                    RecordLine(label = "За месяц", score = summary.month)
                    RecordLine(label = "За неделю", score = summary.week)
                    RecordLine(label = "Сегодня", score = summary.today)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, colors.boardLine.copy(alpha = 0.85f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = colors.panelBackground.copy(alpha = 0.75f),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(
                text = stringResource(R.string.back),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecordHeroRow(score: Int) {
    val colors = LocalGameColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.clearHighlight.copy(alpha = 0.22f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = "★★★  ${stringResource(R.string.all_time)}",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = score.toString(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun RecordLine(label: String, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = score.toString(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
    }
}
