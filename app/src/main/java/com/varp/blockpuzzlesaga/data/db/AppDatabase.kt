package com.varp.blockpuzzlesaga.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        GameStateEntity::class,
        RecordEntity::class,
        SettingsEntity::class,
        StatisticsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameStateDao(): GameStateDao
    abstract fun recordDao(): RecordDao
    abstract fun settingsDao(): SettingsDao
    abstract fun statisticsDao(): StatisticsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `game_state` (
                        `id` INTEGER NOT NULL,
                        `boardJson` TEXT NOT NULL,
                        `availablePiecesJson` TEXT NOT NULL,
                        `comboTrackerJson` TEXT NOT NULL,
                        `score` INTEGER NOT NULL,
                        `remainingRotations` INTEGER NOT NULL,
                        `gameOver` INTEGER NOT NULL,
                        `updatedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `settings` (
                        `id` INTEGER NOT NULL,
                        `selectedTheme` TEXT NOT NULL,
                        `soundEnabled` INTEGER NOT NULL,
                        `vibrationEnabled` INTEGER NOT NULL,
                        `sfxVolume` REAL NOT NULL,
                        `musicVolume` REAL NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `statistics` (
                        `id` INTEGER NOT NULL,
                        `gamesPlayed` INTEGER NOT NULL,
                        `totalPlayTimeMillis` INTEGER NOT NULL,
                        `bestComboChain` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `game_state` ADD COLUMN `rotatedPieceIndicesJson` TEXT NOT NULL DEFAULT '[]'"
                )
            }
        }
    }
}
