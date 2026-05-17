package com.varp.blockpuzzlesaga.data.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.varp.blockpuzzlesaga.data.repository.GameRepository
import com.varp.blockpuzzlesaga.data.repository.RecordScopes
import com.varp.blockpuzzlesaga.data.repository.RecordsRepository
import com.varp.blockpuzzlesaga.data.repository.SettingsRepository
import com.varp.blockpuzzlesaga.data.repository.StatsRepository
import com.varp.blockpuzzlesaga.domain.model.GameState
import com.varp.blockpuzzlesaga.domain.logic.PieceGenerator
import com.varp.blockpuzzlesaga.domain.model.MoveResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDatabaseTest {
    @Test
    fun recordDaoWritesAndReadsRecord() = runTest {
        withDatabase { db ->
            val record = RecordEntity(
                scope = "overall",
                score = 1_250,
                updatedAtMillis = 1_714_000_000_000
            )

            db.recordDao().upsert(record)

            val saved = db.recordDao().findByScope("overall")
            assertNotNull(saved)
            assertEquals(record, saved)
        }
    }

    @Test
    fun recordsRepositoryKeepsHighestRecordForScope() = runTest {
        withDatabase { db ->
            val repository = RecordsRepository(db.recordDao())

            repository.saveRecord(scope = "daily", score = 500, updatedAtMillis = 1)
            repository.saveRecord(scope = "daily", score = 300, updatedAtMillis = 2)
            repository.saveRecord(scope = "daily", score = 800, updatedAtMillis = 3)

            val saved = repository.getRecord("daily")
            assertEquals(800, saved?.score)
            assertEquals(3L, saved?.updatedAtMillis)
        }
    }

    @Test
    fun recordsRepositorySavesSeparateCurrentPeriodRecords() = runTest {
        withDatabase { db ->
            val repository = RecordsRepository(db.recordDao())
            val firstDay = java.time.LocalDate.of(2026, 5, 16)
            val nextDay = firstDay.plusDays(1)
            val zoneId = java.time.ZoneId.systemDefault()
            val firstMillis = firstDay
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli()
            val nextMillis = nextDay
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli()

            repository.saveGameRecords(score = 2_000, updatedAtMillis = firstMillis)
            repository.saveGameRecords(score = 900, updatedAtMillis = nextMillis)

            assertEquals(2_000, repository.getRecord(RecordScopes.OVERALL)?.score)
            assertEquals(900, repository.getRecord(RecordScopes.today(nextDay))?.score)
        }
    }

    @Test
    fun settingsRepositoryReturnsDefaultsWhenSettingsAreMissing() = runTest {
        withDatabase { db ->
            val settings = SettingsRepository(db.settingsDao()).getSettings()

            assertEquals(SettingsEntity.THEME_SPACE, settings.selectedTheme)
            assertTrue(settings.soundEnabled)
            assertTrue(settings.vibrationEnabled)
        }
    }

    @Test
    fun settingsRepositorySavesAndReadsSettings() = runTest {
        withDatabase { db ->
            val repository = SettingsRepository(db.settingsDao())
            val settings = SettingsEntity(
                selectedTheme = "cyberpunk",
                soundEnabled = false,
                vibrationEnabled = false,
                sfxVolume = 0.4f,
                musicVolume = 0.2f
            )

            repository.saveSettings(settings)

            assertEquals(settings, repository.getSettings())
        }
    }

    @Test
    fun statsRepositoryRecordsFinishedGame() = runTest {
        withDatabase { db ->
            val repository = StatsRepository(db.statisticsDao())

            repository.recordFinishedGame(playTimeMillis = 10_000, comboChain = 2)
            repository.recordFinishedGame(playTimeMillis = 5_000, comboChain = 4)

            val statistics = repository.getStatistics()
            assertEquals(2, statistics.gamesPlayed)
            assertEquals(15_000, statistics.totalPlayTimeMillis)
            assertEquals(4, statistics.bestComboChain)
        }
    }

    @Test
    fun gameRepositorySavesAndRestoresUnfinishedGame() = runTest {
        withDatabase { db ->
            val repository = GameRepository(db.gameStateDao())
            val initial = GameState(
                availablePieces = listOf(
                    PieceGenerator.line2,
                    PieceGenerator.single,
                    PieceGenerator.l
                )
            )
            val afterMove = (initial.placePiece(0, 0, 0) as MoveResult.Placed)
                .state
                .rotatePiece(2)

            repository.saveGame(afterMove, updatedAtMillis = 1_715_000_000_000)
            val restored = repository.loadGame()

            assertNotNull(restored)
            assertEquals(afterMove.score, restored?.score)
            assertEquals(afterMove.board, restored?.board)
            assertEquals(afterMove.availablePieces, restored?.availablePieces)
            assertEquals(afterMove.comboTracker, restored?.comboTracker)
            assertEquals(afterMove.rotationManager, restored?.rotationManager)
            assertFalse(restored?.gameOver ?: true)
        }
    }

    @Test
    fun gameRepositoryClearsSavedGame() = runTest {
        withDatabase { db ->
            val repository = GameRepository(db.gameStateDao())
            repository.saveGame(GameState(), updatedAtMillis = 1)

            repository.clearSavedGame()

            assertNull(repository.loadGame())
        }
    }

    @Test
    fun migrationOneToTwoCreatesStageTwoTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("migration-stage-two.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) = Unit
                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build()
        )

        try {
            val db = helper.writableDatabase
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `records` (
                    `scope` TEXT NOT NULL,
                    `score` INTEGER NOT NULL,
                    `updatedAtMillis` INTEGER NOT NULL,
                    PRIMARY KEY(`scope`)
                )
                """.trimIndent()
            )

            AppDatabase.MIGRATION_1_2.migrate(db)

            assertTrue(db.hasTable("game_state"))
            assertTrue(db.hasTable("settings"))
            assertTrue(db.hasTable("statistics"))
        } finally {
            helper.close()
            context.deleteDatabase("migration-stage-two.db")
        }
    }

    @Test
    fun migrationTwoToThreeAddsRotatedPieceIndices() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("migration-stage-three.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: SupportSQLiteDatabase) = Unit
                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build()
        )

        try {
            val db = helper.writableDatabase
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

            AppDatabase.MIGRATION_2_3.migrate(db)

            assertTrue(db.hasColumn("game_state", "rotatedPieceIndicesJson"))
        } finally {
            helper.close()
            context.deleteDatabase("migration-stage-three.db")
        }
    }

    private suspend fun withDatabase(block: suspend (AppDatabase) -> Unit) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        try {
            block(db)
        } finally {
            db.close()
        }
    }

    private fun SupportSQLiteDatabase.hasTable(tableName: String): Boolean {
        query(
            "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?",
            arrayOf(tableName)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    private fun SupportSQLiteDatabase.hasColumn(tableName: String, columnName: String): Boolean {
        query("PRAGMA table_info(`$tableName`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == columnName) return true
            }
        }
        return false
    }
}
