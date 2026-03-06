package com.fitplannerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitplannerpro.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY dayLabel, mealTime")
    fun getAll(): Flow<List<MealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MealEntity)

    @Update
    suspend fun update(item: MealEntity)

    @Delete
    suspend fun delete(item: MealEntity)

    @Query("UPDATE meals SET consumed = :consumed WHERE id = :id")
    suspend fun setConsumed(id: Long, consumed: Boolean)
}
