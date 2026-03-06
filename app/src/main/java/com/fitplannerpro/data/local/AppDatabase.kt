package com.fitplannerpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fitplannerpro.data.local.dao.ExerciseDao
import com.fitplannerpro.data.local.dao.MealDao
import com.fitplannerpro.data.local.dao.ProfileDao
import com.fitplannerpro.data.local.dao.WorkoutHistoryDao
import com.fitplannerpro.data.local.dao.WorkoutPlanDao
import com.fitplannerpro.data.local.entity.ExerciseEntity
import com.fitplannerpro.data.local.entity.MealEntity
import com.fitplannerpro.data.local.entity.ProfileEntity
import com.fitplannerpro.data.local.entity.WorkoutHistoryEntity
import com.fitplannerpro.data.local.entity.WorkoutPlanEntity
import com.fitplannerpro.utils.Constants
import com.fitplannerpro.utils.Converters

@Database(
    entities = [
        WorkoutPlanEntity::class,
        WorkoutHistoryEntity::class,
        MealEntity::class,
        ExerciseEntity::class,
        ProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutHistoryDao(): WorkoutHistoryDao
    abstract fun mealDao(): MealDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .addCallback(prepopulateCallback)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val prepopulateCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO profile (id, name, age, heightCm, weightKg, trainingGoal) VALUES (1, 'Marta', 29, 168, 63.5, 'Redukcja')")
                db.execSQL("INSERT INTO workout_plans (name, description, difficulty, trainingDays, goal, category, exercisesByDay, completedToday) VALUES ('FBW Start', 'Plan ogólnorozwojowy 3x tygodniowo.', 'Średni', 3, 'Redukcja', 'full body workout', 'Dzień 1: Przysiad, pompki||Dzień 2: Martwy ciąg, plank||Dzień 3: Wiosłowanie, wykroki', 0)")
                db.execSQL("INSERT INTO meals (name, description, calories, protein, fat, carbs, mealTime, dayLabel, consumed) VALUES ('Owsianka z owocami', 'Owsianka na mleku z bananem i borówkami', 430, 18, 12, 60, '08:00', 'Poniedziałek', 0)")
                db.execSQL("INSERT INTO exercises (name, technique, difficulty, muscleGroup, equipment, type, tips, commonMistakes, imagePlaceholder, isCustom) VALUES ('Wyciskanie sztangi leżąc', 'Łopatki ściągnięte, stopy stabilnie na podłożu.', 'Średni', 'klatka piersiowa', 'Sztanga + ławka', 'Siłowe', 'Kontroluj tempo opuszczania ciężaru.', 'Odbijanie sztangi od klatki.', '🏋️', 0)")
                db.execSQL("INSERT INTO workout_history (planId, planName, dateMillis) VALUES (1, 'FBW Start', strftime('%s','now')*1000 - 86400000)")
            }
        }
    }
}
