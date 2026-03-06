package com.fitplannerpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitplannerpro.data.datastore.UserSettingsStore
import com.fitplannerpro.data.local.entity.ExerciseEntity
import com.fitplannerpro.data.local.entity.MealEntity
import com.fitplannerpro.data.local.entity.ProfileEntity
import com.fitplannerpro.data.local.entity.WorkoutPlanEntity
import com.fitplannerpro.data.repository.FitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppUiState(
    val plans: List<WorkoutPlanEntity> = emptyList(),
    val meals: List<MealEntity> = emptyList(),
    val exercises: List<ExerciseEntity> = emptyList(),
    val profile: ProfileEntity? = null,
    val historyCount: Int = 0,
    val darkMode: Boolean = false,
    val message: String? = null
)

class AppViewModel(
    private val repository: FitRepository,
    private val settingsStore: UserSettingsStore
) : ViewModel() {

    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AppUiState> = combine(
        repository.workoutPlans,
        repository.meals,
        repository.exercises,
        repository.profile,
        repository.workoutHistory,
        settingsStore.isDarkMode,
        message
    ) { plans, meals, exercises, profile, history, dark, msg ->
        AppUiState(
            plans = plans,
            meals = meals,
            exercises = exercises,
            profile = profile,
            historyCount = history.size,
            darkMode = dark,
            message = msg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUiState())

    fun consumeMessage() {
        message.value = null
    }

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        settingsStore.setDarkMode(enabled)
    }

    fun addOrUpdatePlan(item: WorkoutPlanEntity) = viewModelScope.launch {
        if (item.name.isBlank()) {
            message.value = "Nazwa planu jest wymagana"
            return@launch
        }
        if (item.id == 0L) repository.addWorkoutPlan(item) else repository.updateWorkoutPlan(item)
        message.value = "Zapisano plan treningowy"
    }

    fun deletePlan(item: WorkoutPlanEntity) = viewModelScope.launch {
        repository.deleteWorkoutPlan(item)
        message.value = "Usunięto plan"
    }

    fun togglePlanCompleted(item: WorkoutPlanEntity) = viewModelScope.launch {
        repository.markWorkoutCompleted(item, !item.completedToday)
        message.value = if (!item.completedToday) "Oznaczono trening jako wykonany" else "Cofnięto oznaczenie"
    }

    fun addOrUpdateMeal(item: MealEntity) = viewModelScope.launch {
        if (item.name.isBlank()) {
            message.value = "Nazwa posiłku jest wymagana"
            return@launch
        }
        if (item.calories <= 0) {
            message.value = "Kalorie muszą być większe od zera"
            return@launch
        }
        if (item.id == 0L) repository.addMeal(item) else repository.updateMeal(item)
        message.value = "Zapisano posiłek"
    }

    fun deleteMeal(item: MealEntity) = viewModelScope.launch {
        repository.deleteMeal(item)
        message.value = "Usunięto posiłek"
    }

    fun toggleMealConsumed(item: MealEntity) = viewModelScope.launch {
        repository.setMealConsumed(item.id, !item.consumed)
        message.value = "Zaktualizowano status posiłku"
    }

    fun addOrUpdateExercise(item: ExerciseEntity) = viewModelScope.launch {
        if (item.name.isBlank()) {
            message.value = "Nazwa ćwiczenia jest wymagana"
            return@launch
        }
        if (item.id == 0L) repository.addExercise(item.copy(isCustom = true)) else repository.updateExercise(item)
        message.value = "Zapisano ćwiczenie"
    }

    fun deleteExercise(item: ExerciseEntity) = viewModelScope.launch {
        repository.deleteExercise(item)
        message.value = "Usunięto ćwiczenie"
    }

    fun saveProfile(item: ProfileEntity) = viewModelScope.launch {
        if (item.name.isBlank() || item.heightCm <= 0 || item.weightKg <= 0f) {
            message.value = "Uzupełnij poprawnie dane profilu"
            return@launch
        }
        repository.saveProfile(item)
        message.value = "Zapisano profil"
    }
}

class AppViewModelFactory(
    private val repository: FitRepository,
    private val settingsStore: UserSettingsStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(repository, settingsStore) as T
    }
}
