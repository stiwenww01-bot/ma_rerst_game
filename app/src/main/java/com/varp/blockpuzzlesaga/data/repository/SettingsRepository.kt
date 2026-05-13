package com.varp.blockpuzzlesaga.data.repository

import com.varp.blockpuzzlesaga.data.db.SettingsDao
import com.varp.blockpuzzlesaga.data.db.SettingsEntity

class SettingsRepository(
    private val settingsDao: SettingsDao
) {
    suspend fun getSettings(): SettingsEntity {
        return settingsDao.find() ?: SettingsEntity()
    }

    suspend fun saveSettings(settings: SettingsEntity) {
        settingsDao.upsert(settings)
    }
}
