package com.varp.blockpuzzlesaga.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey val scope: String,
    val score: Int,
    val updatedAtMillis: Long
)
