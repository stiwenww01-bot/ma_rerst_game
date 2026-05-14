package com.varp.blockpuzzlesaga.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.varp.blockpuzzlesaga.data.db.SettingsEntity
import com.varp.blockpuzzlesaga.data.repository.SettingsRepository
import com.varp.blockpuzzlesaga.ui.theme.GameThemeId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val selectedThemeKey: String = GameThemeId.default.storageKey,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val sfxVolume: Float = 1f,
    val musicVolume: Float = 0.7f
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = settingsRepository.observeSettings()
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun selectTheme(themeId: GameThemeId) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.saveSettings(current.copy(selectedTheme = themeId.storageKey))
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository) as T
        }
    }
}

private fun SettingsEntity.toUiState(): SettingsUiState {
    return SettingsUiState(
        selectedThemeKey = selectedTheme,
        soundEnabled = soundEnabled,
        vibrationEnabled = vibrationEnabled,
        sfxVolume = sfxVolume,
        musicVolume = musicVolume
    )
}
