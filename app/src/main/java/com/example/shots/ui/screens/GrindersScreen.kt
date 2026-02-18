package com.example.espressoshots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.ui.components.EmptyState
import com.example.espressoshots.ui.components.GrinderCard
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun GrindersScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val grinders = vm.grinders.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var grinderToDelete by remember { mutableStateOf<Long?>(null) }

    if (grinders.value.isEmpty()) {
        EmptyState(
            message = "No hay molinos. Agrega uno para empezar.",
            actionLabel = "Agregar molino",
            onAction = { navController.navigate("grinders/new") }
        )
        return
    }

    val filteredGrinders = grinders.value.filter { grinder ->
        val label = grinder.nombre.lowercase()
        val notes = (grinder.notas ?: "").lowercase()
        val searchLower = searchQuery.lowercase()
        label.contains(searchLower) || notes.contains(searchLower)
    }

    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar molino...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )

        if (filteredGrinders.isEmpty()) {
            EmptyState(
                message = "No se encontraron molinos con esa búsqueda.",
                actionLabel = "Limpiar búsqueda",
                onAction = { searchQuery = "" }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGrinders) { grinder ->
                    GrinderCard(
                        grinder = grinder,
                        onEdit = { navController.navigate("grinders/edit/${grinder.id}") },
                        onDelete = { grinderToDelete = grinder.id }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (grinderToDelete != null) {
        AlertDialog(
            onDismissRequest = { grinderToDelete = null },
            title = { Text("Eliminar molino") },
            text = { Text("¿Estás seguro de que quieres eliminar este molino? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        grinderToDelete?.let { vm.deleteGrinder(it) }
                        grinderToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { grinderToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
