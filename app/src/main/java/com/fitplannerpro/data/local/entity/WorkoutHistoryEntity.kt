package com.fitplannerpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_history")
data class WorkoutHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val planName: String,
    val dateMillis: Long
)
