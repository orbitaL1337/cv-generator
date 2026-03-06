package com.fitplannerpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val heightCm: Int,
    val weightKg: Float,
    val trainingGoal: String
)
