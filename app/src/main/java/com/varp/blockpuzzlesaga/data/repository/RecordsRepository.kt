package com.varp.blockpuzzlesaga.data.repository

import com.varp.blockpuzzlesaga.data.db.RecordDao
import com.varp.blockpuzzlesaga.data.db.RecordEntity

class RecordsRepository(
    private val recordDao: RecordDao
) {
    suspend fun saveRecord(scope: String, score: Int, updatedAtMillis: Long = System.currentTimeMillis()) {
        val current = recordDao.findByScope(scope)
        if (current == null || score > current.score) {
            recordDao.upsert(
                RecordEntity(
                    scope = scope,
                    score = score,
                    updatedAtMillis = updatedAtMillis
                )
            )
        }
    }

    suspend fun getRecord(scope: String): RecordEntity? {
        return recordDao.findByScope(scope)
    }

    suspend fun getRecords(): List<RecordEntity> {
        return recordDao.findAll()
    }
}
