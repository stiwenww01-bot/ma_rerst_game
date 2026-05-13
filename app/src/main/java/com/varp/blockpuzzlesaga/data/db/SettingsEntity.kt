package com.varp.blockpuzzlesaga.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = DEFAULT_ID,
    val selectedTheme: String = THEME_CLASSIC,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val sfxVolume: Float = 1f,
    val musicVolume: Float = 0.7f
) {
    companion object {
        const val DEFAULT_ID = 1
        const val THEME_CLASSIC = "classic"
    }
}
