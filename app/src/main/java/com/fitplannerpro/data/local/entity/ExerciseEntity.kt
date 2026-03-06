package com.fitplannerpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val technique: String,
    val difficulty: String,
    val muscleGroup: String,
    val equipment: String,
    val type: String,
    val tips: String,
    val commonMistakes: String,
    val imagePlaceholder: String = "🏋️",
    val isCustom: Boolean = false
)
