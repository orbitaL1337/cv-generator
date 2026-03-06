package com.fitplannerpro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitplannerpro.data.local.entity.ProfileEntity
import com.fitplannerpro.ui.components.Field
import com.fitplannerpro.ui.components.SaveButton
import com.fitplannerpro.ui.viewmodel.AppUiState
import com.fitplannerpro.ui.viewmodel.AppViewModel

@Composable
fun ProfileScreen(state: AppUiState, viewModel: AppViewModel) {
    val profile = state.profile ?: ProfileEntity(name = "", age = 25, heightCm = 170, weightKg = 70f, trainingGoal = "Redukcja")
    var name by remember(profile.name) { mutableStateOf(profile.name) }
    var age by remember(profile.age) { mutableStateOf(profile.age.toString()) }
    var height by remember(profile.heightCm) { mutableStateOf(profile.heightCm.toString()) }
    var weight by remember(profile.weightKg) { mutableStateOf(profile.weightKg.toString()) }
    var goal by remember(profile.trainingGoal) { mutableStateOf(profile.trainingGoal) }

    val bmi = runCatching {
        val h = (height.toFloat() / 100f)
        weight.toFloat() / (h * h)
    }.getOrDefault(0f)

    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Profil użytkownika", style = MaterialTheme.typography.headlineSmall)
        Field(name, { name = it }, "Imię")
        Field(age, { age = it }, "Wiek")
        Field(height, { height = it }, "Wzrost (cm)")
        Field(weight, { weight = it }, "Waga (kg)")
        Field(goal, { goal = it }, "Cel treningowy")
        SaveButton(onClick = {
            viewModel.saveProfile(
                ProfileEntity(
                    id = 1,
                    name = name,
                    age = age.toIntOrNull() ?: 0,
                    heightCm = height.toIntOrNull() ?: 0,
                    weightKg = weight.toFloatOrNull() ?: 0f,
                    trainingGoal = goal
                )
            )
        })

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Statystyki", style = MaterialTheme.typography.titleMedium)
                Text("BMI: ${"%.1f".format(bmi)}")
                Text("Wykonane treningi: ${state.historyCount}")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Ustawienia", style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Tryb ciemny")
                    Switch(checked = state.darkMode, onCheckedChange = { viewModel.setDarkMode(it) })
                }
            }
        }
    }
}
