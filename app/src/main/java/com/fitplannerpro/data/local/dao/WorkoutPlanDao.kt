package com.fitplannerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitplannerpro.data.local.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans ORDER BY id DESC")
    fun getAll(): Flow<List<WorkoutPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorkoutPlanEntity)

    @Update
    suspend fun update(item: WorkoutPlanEntity)

    @Delete
    suspend fun delete(item: WorkoutPlanEntity)

    @Query("UPDATE workout_plans SET completedToday = :completed WHERE id = :id")
    suspend fun markCompleted(id: Long, completed: Boolean)
}
