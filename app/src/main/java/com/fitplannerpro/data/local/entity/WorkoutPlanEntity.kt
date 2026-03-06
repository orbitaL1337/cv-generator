package com.fitplannerpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val difficulty: String,
    val trainingDays: Int,
    val goal: String,
    val category: String,
    val exercisesByDay: List<String>,
    val completedToday: Boolean = false
)
