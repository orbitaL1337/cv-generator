package com.fitplannerpro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitplannerpro.data.local.entity.WorkoutPlanEntity
import com.fitplannerpro.ui.components.ConfirmDeleteDialog
import com.fitplannerpro.ui.components.EmptyState
import com.fitplannerpro.ui.components.Field
import com.fitplannerpro.ui.components.SaveButton
import com.fitplannerpro.ui.viewmodel.AppUiState
import com.fitplannerpro.ui.viewmodel.AppViewModel

@Composable
fun PlansScreen(state: AppUiState, viewModel: AppViewModel) {
    var edited by remember { mutableStateOf<WorkoutPlanEntity?>(null) }
    var deleteItem by remember { mutableStateOf<WorkoutPlanEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { edited = WorkoutPlanEntity(name = "", description = "", difficulty = "Początkujący", trainingDays = 3, goal = "", category = "redukcja", exercisesByDay = emptyList()) }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj plan")
            }
        }
    ) { pv ->
        if (state.plans.isEmpty()) {
            EmptyState("Brak planów treningowych", "Dodaj pierwszy plan przyciskiem +")
        } else {
            LazyColumn(modifier = Modifier.padding(pv).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.plans, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                                    Text("${item.category} • ${item.difficulty}")
                                }
                                Row {
                                    IconButton(onClick = { edited = item }) { Icon(Icons.Default.Edit, contentDescription = "Edytuj") }
                                    IconButton(onClick = { deleteItem = item }) { Icon(Icons.Default.Delete, contentDescription = "Usuń") }
                                }
                            }
                            Text(item.description)
                            Text("Dni: ${item.trainingDays}, cel: ${item.goal}")
                            item.exercisesByDay.forEach { Text("• $it") }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = item.completedToday, onCheckedChange = { viewModel.togglePlanCompleted(item) })
                                Text("Wykonano dziś")
                            }
                        }
                    }
                }
            }
        }
    }

    edited?.let { plan ->
        PlanDialog(initial = plan, onDismiss = { edited = null }, onSave = {
            viewModel.addOrUpdatePlan(it)
            edited = null
        })
    }

    deleteItem?.let { item ->
        ConfirmDeleteDialog(onDismiss = { deleteItem = null }, onConfirm = {
            viewModel.deletePlan(item)
            deleteItem = null
        })
    }
}

@Composable
private fun PlanDialog(initial: WorkoutPlanEntity, onDismiss: () -> Unit, onSave: (WorkoutPlanEntity) -> Unit) {
    var name by remember(initial.id) { mutableStateOf(initial.name) }
    var description by remember(initial.id) { mutableStateOf(initial.description) }
    var difficulty by remember(initial.id) { mutableStateOf(initial.difficulty) }
    var trainingDays by remember(initial.id) { mutableStateOf(initial.trainingDays.toString()) }
    var goal by remember(initial.id) { mutableStateOf(initial.goal) }
    var category by remember(initial.id) { mutableStateOf(initial.category) }
    var exercises by remember(initial.id) { mutableStateOf(initial.exercisesByDay.joinToString("\n")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id == 0L) "Nowy plan" else "Edytuj plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Field(name, { name = it }, "Nazwa")
                Field(description, { description = it }, "Opis")
                Field(difficulty, { difficulty = it }, "Poziom")
                Field(trainingDays, { trainingDays = it }, "Liczba dni")
                Field(goal, { goal = it }, "Cel")
                Field(category, { category = it }, "Kategoria")
                Field(exercises, { exercises = it }, "Ćwiczenia (linia = dzień)")
            }
        },
        confirmButton = {
            SaveButton(onClick = {
                onSave(initial.copy(
                    name = name,
                    description = description,
                    difficulty = difficulty,
                    trainingDays = trainingDays.toIntOrNull() ?: 0,
                    goal = goal,
                    category = category,
                    exercisesByDay = exercises.lines().filter { it.isNotBlank() }
                ))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
