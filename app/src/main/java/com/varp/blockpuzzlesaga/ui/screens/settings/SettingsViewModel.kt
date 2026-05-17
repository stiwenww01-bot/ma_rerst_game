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
    val soundEffectsEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val sfxVolume: Float = 1f,
    val musicVolume: Float = 0.55f
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            if (current.soundEnabled && current.musicVolume <= 0f) {
                settingsRepository.saveSettings(current.copy(musicVolume = DEFAULT_MUSIC_VOLUME))
            }
        }
    }

    val uiState: StateFlow<SettingsUiState> = settingsRepository.observeSettings()
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.saveSettings(current.copy(soundEnabled = enabled))
        }
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.saveSettings(
                current.copy(sfxVolume = if (enabled) current.sfxVolume.coerceAtLeast(0.65f) else 0f)
            )
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.saveSettings(
                current.copy(musicVolume = if (enabled) current.musicVolume.coerceAtLeast(DEFAULT_MUSIC_VOLUME) else 0f)
            )
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.saveSettings(current.copy(vibrationEnabled = enabled))
        }
    }

    fun setSfxVolume(volume: Float) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.saveSettings(current.copy(sfxVolume = volume.coerceIn(0f, 1f)))
        }
    }

    fun setMusicVolume(volume: Float) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.saveSettings(current.copy(musicVolume = volume.coerceIn(0f, 1f)))
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

    private companion object {
        const val DEFAULT_MUSIC_VOLUME = 0.55f
    }
}

private fun SettingsEntity.toUiState(): SettingsUiState {
    return SettingsUiState(
        selectedThemeKey = GameThemeId.default.storageKey,
        soundEnabled = soundEnabled,
        soundEffectsEnabled = sfxVolume > 0f,
        musicEnabled = musicVolume > 0f,
        vibrationEnabled = vibrationEnabled,
        sfxVolume = sfxVolume,
        musicVolume = musicVolume
    )
}
