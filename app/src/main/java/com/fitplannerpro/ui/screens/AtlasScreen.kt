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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitplannerpro.data.local.entity.ExerciseEntity
import com.fitplannerpro.ui.components.ConfirmDeleteDialog
import com.fitplannerpro.ui.components.EmptyState
import com.fitplannerpro.ui.components.Field
import com.fitplannerpro.ui.components.SaveButton
import com.fitplannerpro.ui.viewmodel.AppUiState
import com.fitplannerpro.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtlasScreen(state: AppUiState, viewModel: AppViewModel) {
    var query by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf("Wszystkie") }
    var edited by remember { mutableStateOf<ExerciseEntity?>(null) }
    var deleteItem by remember { mutableStateOf<ExerciseEntity?>(null) }

    val muscles = listOf("Wszystkie", "klatka piersiowa", "plecy", "barki", "nogi", "ramiona", "brzuch", "cardio")
    val filtered = state.exercises.filter {
        (query.isBlank() || it.name.contains(query, true)) && (muscle == "Wszystkie" || it.muscleGroup == muscle)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                edited = ExerciseEntity(name = "", technique = "", difficulty = "Początkujący", muscleGroup = "klatka piersiowa", equipment = "Brak", type = "Siłowe", tips = "", commonMistakes = "", isCustom = true)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj ćwiczenie")
            }
        }
    ) { pv ->
        Column(modifier = Modifier.padding(pv).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Szukaj ćwiczeń") }, modifier = Modifier.fillMaxWidth())
            MuscleFilter(selected = muscle, options = muscles, onChange = { muscle = it })
            if (filtered.isEmpty()) {
                EmptyState("Brak ćwiczeń", "Zmień filtr albo dodaj własne ćwiczenie")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered, key = { it.id }) { item ->
                        Card {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("${item.imagePlaceholder} ${item.name}", style = MaterialTheme.typography.titleMedium)
                                        Text("${item.muscleGroup} • ${item.difficulty} • ${item.equipment}")
                                    }
                                    if (item.isCustom) {
                                        Row {
                                            IconButton(onClick = { edited = item }) { Icon(Icons.Default.Edit, null) }
                                            IconButton(onClick = { deleteItem = item }) { Icon(Icons.Default.Delete, null) }
                                        }
                                    }
                                }
                                Text("Technika: ${item.technique}")
                                Text("Wskazówki: ${item.tips}")
                                Text("Błędy: ${item.commonMistakes}")
                            }
                        }
                    }
                }
            }
        }
    }

    edited?.let { ex ->
        ExerciseDialog(initial = ex, onDismiss = { edited = null }, onSave = {
            viewModel.addOrUpdateExercise(it)
            edited = null
        })
    }

    deleteItem?.let { ex ->
        ConfirmDeleteDialog(onDismiss = { deleteItem = null }, onConfirm = {
            viewModel.deleteExercise(ex)
            deleteItem = null
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MuscleFilter(selected: String, options: List<String>, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            label = { Text("Partia mięśniowa") },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { item ->
                DropdownMenuItem(text = { Text(item) }, onClick = {
                    onChange(item)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun ExerciseDialog(initial: ExerciseEntity, onDismiss: () -> Unit, onSave: (ExerciseEntity) -> Unit) {
    var name by remember(initial.id) { mutableStateOf(initial.name) }
    var technique by remember(initial.id) { mutableStateOf(initial.technique) }
    var difficulty by remember(initial.id) { mutableStateOf(initial.difficulty) }
    var muscleGroup by remember(initial.id) { mutableStateOf(initial.muscleGroup) }
    var equipment by remember(initial.id) { mutableStateOf(initial.equipment) }
    var type by remember(initial.id) { mutableStateOf(initial.type) }
    var tips by remember(initial.id) { mutableStateOf(initial.tips) }
    var mistakes by remember(initial.id) { mutableStateOf(initial.commonMistakes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id == 0L) "Nowe ćwiczenie" else "Edytuj ćwiczenie") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Field(name, { name = it }, "Nazwa")
                Field(technique, { technique = it }, "Technika")
                Field(difficulty, { difficulty = it }, "Poziom")
                Field(muscleGroup, { muscleGroup = it }, "Partia")
                Field(equipment, { equipment = it }, "Sprzęt")
                Field(type, { type = it }, "Typ")
                Field(tips, { tips = it }, "Wskazówki")
                Field(mistakes, { mistakes = it }, "Najczęstsze błędy")
            }
        },
        confirmButton = {
            SaveButton(onClick = {
                onSave(initial.copy(
                    name = name,
                    technique = technique,
                    difficulty = difficulty,
                    muscleGroup = muscleGroup,
                    equipment = equipment,
                    type = type,
                    tips = tips,
                    commonMistakes = mistakes,
                    isCustom = true
                ))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
