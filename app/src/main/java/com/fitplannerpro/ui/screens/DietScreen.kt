package com.fitplannerpro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitplannerpro.data.local.entity.MealEntity
import com.fitplannerpro.ui.components.ConfirmDeleteDialog
import com.fitplannerpro.ui.components.EmptyState
import com.fitplannerpro.ui.components.Field
import com.fitplannerpro.ui.components.SaveButton
import com.fitplannerpro.ui.viewmodel.AppUiState
import com.fitplannerpro.ui.viewmodel.AppViewModel

@Composable
fun DietScreen(state: AppUiState, viewModel: AppViewModel) {
    var edited by remember { mutableStateOf<MealEntity?>(null) }
    var deleteItem by remember { mutableStateOf<MealEntity?>(null) }

    val calories = state.meals.sumOf { it.calories }
    val protein = state.meals.sumOf { it.protein }
    val fat = state.meals.sumOf { it.fat }
    val carbs = state.meals.sumOf { it.carbs }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                edited = MealEntity(name = "", description = "", calories = 0, protein = 0, fat = 0, carbs = 0, mealTime = "12:00", dayLabel = "Poniedziałek")
            }) { Icon(Icons.Default.Add, contentDescription = "Dodaj posiłek") }
        }
    ) { pv ->
        Column(modifier = Modifier.padding(pv).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Podsumowanie dnia/tygodnia", style = MaterialTheme.typography.titleMedium)
                    Text("Kalorie: $calories kcal | B: $protein g | T: $fat g | W: $carbs g")
                }
            }
            if (state.meals.isEmpty()) {
                EmptyState("Brak posiłków", "Dodaj posiłek, aby zbudować jadłospis")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.meals, key = { it.id }) { item ->
                        Card {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(item.name, style = MaterialTheme.typography.titleMedium)
                                        Text("${item.dayLabel}, ${item.mealTime}")
                                    }
                                    Row {
                                        IconButton(onClick = { edited = item }) { Icon(Icons.Default.Edit, contentDescription = null) }
                                        IconButton(onClick = { deleteItem = item }) { Icon(Icons.Default.Delete, contentDescription = null) }
                                    }
                                }
                                Text(item.description)
                                Text("${item.calories} kcal | B:${item.protein} T:${item.fat} W:${item.carbs}")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = item.consumed, onCheckedChange = { viewModel.toggleMealConsumed(item) })
                                    Text("Spożyte")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    edited?.let { meal ->
        MealDialog(initial = meal, onDismiss = { edited = null }, onSave = {
            viewModel.addOrUpdateMeal(it)
            edited = null
        })
    }

    deleteItem?.let { item ->
        ConfirmDeleteDialog(onDismiss = { deleteItem = null }, onConfirm = {
            viewModel.deleteMeal(item)
            deleteItem = null
        })
    }
}

@Composable
private fun MealDialog(initial: MealEntity, onDismiss: () -> Unit, onSave: (MealEntity) -> Unit) {
    var name by remember(initial.id) { mutableStateOf(initial.name) }
    var description by remember(initial.id) { mutableStateOf(initial.description) }
    var calories by remember(initial.id) { mutableStateOf(initial.calories.toString()) }
    var protein by remember(initial.id) { mutableStateOf(initial.protein.toString()) }
    var fat by remember(initial.id) { mutableStateOf(initial.fat.toString()) }
    var carbs by remember(initial.id) { mutableStateOf(initial.carbs.toString()) }
    var mealTime by remember(initial.id) { mutableStateOf(initial.mealTime) }
    var dayLabel by remember(initial.id) { mutableStateOf(initial.dayLabel) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id == 0L) "Nowy posiłek" else "Edytuj posiłek") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Field(name, { name = it }, "Nazwa")
                Field(description, { description = it }, "Opis")
                Field(calories, { calories = it }, "Kalorie")
                Field(protein, { protein = it }, "Białko")
                Field(fat, { fat = it }, "Tłuszcze")
                Field(carbs, { carbs = it }, "Węglowodany")
                Field(mealTime, { mealTime = it }, "Godzina")
                Field(dayLabel, { dayLabel = it }, "Dzień")
            }
        },
        confirmButton = {
            SaveButton(onClick = {
                onSave(initial.copy(
                    name = name,
                    description = description,
                    calories = calories.toIntOrNull() ?: 0,
                    protein = protein.toIntOrNull() ?: 0,
                    fat = fat.toIntOrNull() ?: 0,
                    carbs = carbs.toIntOrNull() ?: 0,
                    mealTime = mealTime,
                    dayLabel = dayLabel
                ))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
