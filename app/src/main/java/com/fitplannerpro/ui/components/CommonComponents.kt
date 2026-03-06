package com.fitplannerpro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(title: String, subtitle: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ConfirmDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Potwierdzenie") },
        text = { Text("Czy na pewno chcesz usunąć ten element?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Usuń") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}

@Composable
fun Field(value: String, onValue: (String) -> Unit, label: String) {
    OutlinedTextField(value = value, onValueChange = onValue, label = { Text(label) }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun SaveButton(onClick: () -> Unit, text: String = "Zapisz") {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(text) }
}
