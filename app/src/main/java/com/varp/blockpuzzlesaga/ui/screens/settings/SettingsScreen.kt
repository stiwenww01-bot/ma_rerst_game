package com.varp.blockpuzzlesaga.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.varp.blockpuzzlesaga.R
import com.varp.blockpuzzlesaga.ui.theme.GameColors
import com.varp.blockpuzzlesaga.ui.theme.GameThemeId
import com.varp.blockpuzzlesaga.ui.theme.GameThemePalettes

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeSelected: (GameThemeId) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.theme_selection),
            style = MaterialTheme.typography.titleMedium
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GameThemePalettes.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { colors ->
                        ThemePreviewCard(
                            colors = colors,
                            selected = colors.themeId.storageKey == uiState.selectedThemeKey,
                            onClick = { onThemeSelected(colors.themeId) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.back))
        }
    }
}

@Composable
private fun ThemePreviewCard(
    colors: GameColors,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) colors.boardLine else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
    val textColor = if (colors.themeId == GameThemeId.Classic) {
        Color(0xFF17202A)
    } else {
        Color(0xFFEAF5FF)
    }
    OutlinedCard(
        modifier = modifier
            .aspectRatio(0.88f)
            .clickable(role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(containerColor = colors.panelBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                ThemePreviewCanvas(
                    colors = colors,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = colors.themeId.displayName,
                style = MaterialTheme.typography.titleSmall,
                color = textColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ThemePreviewCanvas(
    colors: GameColors,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colors.backgroundTop, colors.backgroundMiddle, colors.backgroundBottom)
            ),
            size = size
        )
        drawRect(
            color = colors.boardBackground,
            topLeft = Offset(size.width * 0.12f, size.height * 0.16f),
            size = Size(size.width * 0.76f, size.width * 0.76f)
        )
        repeat(4) { index ->
            val step = size.width * 0.76f / 3f
            val start = size.width * 0.12f + index * step
            drawLine(
                color = colors.boardLine.copy(alpha = 0.8f),
                start = Offset(start, size.height * 0.16f),
                end = Offset(start, size.height * 0.16f + size.width * 0.76f),
                strokeWidth = if (index == 0 || index == 3) 3f else 1.5f
            )
            drawLine(
                color = colors.boardLine.copy(alpha = 0.8f),
                start = Offset(size.width * 0.12f, size.height * 0.16f + index * step),
                end = Offset(size.width * 0.88f, size.height * 0.16f + index * step),
                strokeWidth = if (index == 0 || index == 3) 3f else 1.5f
            )
        }

        val cell = size.width * 0.76f / 3f
        listOf(
            Offset(size.width * 0.12f + cell * 0.15f, size.height * 0.16f + cell * 0.15f),
            Offset(size.width * 0.12f + cell * 1.15f, size.height * 0.16f + cell * 0.15f),
            Offset(size.width * 0.12f + cell * 1.15f, size.height * 0.16f + cell * 1.15f)
        ).forEachIndexed { index, offset ->
            val blockColor = colors.piecePalette[index]
            drawRoundRect(
                color = blockColor,
                topLeft = offset,
                size = Size(cell * 0.72f, cell * 0.72f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cell * 0.12f, cell * 0.12f)
            )
            drawRoundRect(
                color = blockColor.copy(alpha = 0.55f),
                topLeft = Offset(offset.x - cell * 0.05f, offset.y - cell * 0.05f),
                size = Size(cell * 0.82f, cell * 0.82f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cell * 0.16f, cell * 0.16f),
                style = Stroke(width = 4f)
            )
        }
    }
}
