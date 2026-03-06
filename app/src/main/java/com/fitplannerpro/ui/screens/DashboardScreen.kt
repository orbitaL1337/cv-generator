package com.fitplannerpro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitplannerpro.ui.viewmodel.AppUiState

@Composable
fun DashboardScreen(state: AppUiState) {
    val todayCalories = state.meals.filter { it.consumed }.sumOf { it.calories }
    val plannedTraining = state.plans.firstOrNull()?.name ?: "Brak planu"
    val weeklyProgress = (state.historyCount % 7) / 7f

    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cześć ${state.profile?.name ?: "Sportowcu"}!", style = MaterialTheme.typography.headlineSmall)
        DashboardCard("Zaplanowany trening", plannedTraining)
        DashboardCard("Kalorie (spożyte dziś)", "$todayCalories kcal")
        DashboardCard("Liczba posiłków", state.meals.size.toString())
        DashboardCard("Postęp tygodniowy", "${(weeklyProgress * 100).toInt()}%")
        Text("Postęp tygodniowy", style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(progress = { weeklyProgress }, modifier = Modifier.fillMaxWidth())

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickCard("Plany", "${state.plans.size}")
            QuickCard("Dieta", "${state.meals.size}")
            QuickCard("Atlas", "${state.exercises.size}")
        }
    }
}

@Composable
private fun DashboardCard(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun QuickCard(name: String, count: String) {
    Card(modifier = Modifier.weight(1f)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name)
            Text(count, style = MaterialTheme.typography.titleLarge)
        }
    }
}
