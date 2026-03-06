package com.fitplannerpro.data.repository

import com.fitplannerpro.data.local.AppDatabase
import com.fitplannerpro.data.local.entity.ExerciseEntity
import com.fitplannerpro.data.local.entity.MealEntity
import com.fitplannerpro.data.local.entity.ProfileEntity
import com.fitplannerpro.data.local.entity.WorkoutHistoryEntity
import com.fitplannerpro.data.local.entity.WorkoutPlanEntity

class FitRepository(private val db: AppDatabase) {
    val workoutPlans = db.workoutPlanDao().getAll()
    val workoutHistory = db.workoutHistoryDao().getAll()
    val meals = db.mealDao().getAll()
    val exercises = db.exerciseDao().getAll()
    val profile = db.profileDao().observe()

    suspend fun addWorkoutPlan(item: WorkoutPlanEntity) = db.workoutPlanDao().insert(item)
    suspend fun updateWorkoutPlan(item: WorkoutPlanEntity) = db.workoutPlanDao().update(item)
    suspend fun deleteWorkoutPlan(item: WorkoutPlanEntity) = db.workoutPlanDao().delete(item)

    suspend fun markWorkoutCompleted(item: WorkoutPlanEntity, completed: Boolean) {
        db.workoutPlanDao().markCompleted(item.id, completed)
        if (completed) {
            db.workoutHistoryDao().insert(
                WorkoutHistoryEntity(planId = item.id, planName = item.name, dateMillis = System.currentTimeMillis())
            )
        }
    }

    suspend fun addMeal(item: MealEntity) = db.mealDao().insert(item)
    suspend fun updateMeal(item: MealEntity) = db.mealDao().update(item)
    suspend fun deleteMeal(item: MealEntity) = db.mealDao().delete(item)
    suspend fun setMealConsumed(id: Long, consumed: Boolean) = db.mealDao().setConsumed(id, consumed)

    suspend fun addExercise(item: ExerciseEntity) = db.exerciseDao().insert(item)
    suspend fun updateExercise(item: ExerciseEntity) = db.exerciseDao().update(item)
    suspend fun deleteExercise(item: ExerciseEntity) = db.exerciseDao().delete(item)

    suspend fun saveProfile(item: ProfileEntity) {
        db.profileDao().insert(item)
    }
}
