package com.varp.blockpuzzlesaga.data.repository

import com.varp.blockpuzzlesaga.data.db.SettingsDao
import com.varp.blockpuzzlesaga.data.db.SettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val settingsDao: SettingsDao
) {
    suspend fun getSettings(): SettingsEntity {
        return settingsDao.find() ?: SettingsEntity()
    }

    suspend fun saveSettings(settings: SettingsEntity) {
        settingsDao.upsert(settings)
    }

    fun observeSettings(): Flow<SettingsEntity> {
        return settingsDao.observe().map { it ?: SettingsEntity() }
    }
}
