package com.varp.blockpuzzlesaga.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.varp.blockpuzzlesaga.ui.theme.LocalGameColors

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onSoundEnabledChanged: (Boolean) -> Unit,
    onSoundEffectsEnabledChanged: (Boolean) -> Unit,
    onMusicEnabledChanged: (Boolean) -> Unit,
    onVibrationEnabledChanged: (Boolean) -> Unit,
    onSfxVolumeChanged: (Float) -> Unit,
    onMusicVolumeChanged: (Float) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, colors.boardLine.copy(alpha = 0.75f)),
            colors = CardDefaults.outlinedCardColors(
                containerColor = colors.panelBackground.copy(alpha = 0.82f)
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SettingSwitchRow(
                    label = "Звук",
                    checked = uiState.soundEnabled,
                    onCheckedChange = onSoundEnabledChanged
                )
                SettingSwitchRow(
                    label = "Звуковые эффекты",
                    checked = uiState.soundEffectsEnabled,
                    onCheckedChange = onSoundEffectsEnabledChanged
                )
                SettingSwitchRow(
                    label = "Музыка",
                    checked = uiState.musicEnabled,
                    onCheckedChange = onMusicEnabledChanged
                )
                SettingSwitchRow(
                    label = "Вибрация",
                    checked = uiState.vibrationEnabled,
                    onCheckedChange = onVibrationEnabledChanged
                )
                VolumeSlider(
                    label = "Громкость эффектов",
                    value = uiState.sfxVolume,
                    enabled = uiState.soundEnabled && uiState.soundEffectsEnabled,
                    onValueChange = onSfxVolumeChanged
                )
                VolumeSlider(
                    label = "Громкость музыки",
                    value = uiState.musicVolume,
                    enabled = uiState.soundEnabled && uiState.musicEnabled,
                    onValueChange = onMusicVolumeChanged
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Назад")
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun VolumeSlider(
    label: String,
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit
) {
    val colors = LocalGameColors.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                color = colors.clearHighlight,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            enabled = enabled
        )
    }
}
