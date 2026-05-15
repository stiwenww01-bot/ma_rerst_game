package com.varp.blockpuzzlesaga.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = DEFAULT_ID,
    val selectedTheme: String = THEME_SPACE,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val sfxVolume: Float = 0.8f,
    val musicVolume: Float = 0.45f
) {
    companion object {
        const val DEFAULT_ID = 1
        const val THEME_SPACE = "space"
        const val THEME_CLASSIC = "classic"
    }
}
