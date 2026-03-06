package com.fitplannerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitplannerpro.data.local.entity.WorkoutHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutHistoryDao {
    @Query("SELECT * FROM workout_history ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<WorkoutHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorkoutHistoryEntity)
}
