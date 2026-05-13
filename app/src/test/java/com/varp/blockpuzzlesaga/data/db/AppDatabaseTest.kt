package com.varp.blockpuzzlesaga.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDatabaseTest {
    @Test
    fun recordDaoWritesAndReadsRecord() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        try {
            val record = RecordEntity(
                scope = "overall",
                score = 1_250,
                updatedAtMillis = 1_714_000_000_000
            )

            db.recordDao().upsert(record)

            val saved = db.recordDao().findByScope("overall")
            assertNotNull(saved)
            assertEquals(record, saved)
        } finally {
            db.close()
        }
    }
}
