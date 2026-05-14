package com.varp.blockpuzzlesaga.app

import android.content.Context
import androidx.room.Room
import com.varp.blockpuzzlesaga.data.db.AppDatabase
import com.varp.blockpuzzlesaga.data.repository.GameRepository
import com.varp.blockpuzzlesaga.data.repository.RecordsRepository
import com.varp.blockpuzzlesaga.data.repository.SettingsRepository
import com.varp.blockpuzzlesaga.data.repository.StatsRepository

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "block-puzzle-saga.db"
    )
        .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
        .build()

    val gameRepository = GameRepository(database.gameStateDao())
    val recordsRepository = RecordsRepository(database.recordDao())
    val settingsRepository = SettingsRepository(database.settingsDao())
    val statsRepository = StatsRepository(database.statisticsDao())
}
