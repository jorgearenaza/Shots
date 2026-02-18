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
import com.example.espressoshots.ui.components.ShotCard
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun ShotsScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val shots = vm.shots.collectAsState()
    val beans = vm.beans.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var shotToDelete by remember { mutableStateOf<Long?>(null) }

    if (shots.value.isEmpty()) {
        EmptyState(
            message = "No hay shots. Agrega uno para empezar.",
            actionLabel = "Agregar shot",
            onAction = { navController.navigate("shots/new") }
        )
        return
    }

    // Filtrar shots por búsqueda
    val filteredShots = shots.value.filter { shot ->
        val beanLabel = "${shot.beanTostador} - ${shot.beanCafe}".lowercase()
        val notes = (shot.shot.notas ?: "").lowercase()
        val nextShot = (shot.shot.nextShotNotes ?: "").lowercase()
        val searchLower = searchQuery.lowercase()
        
        beanLabel.contains(searchLower) || 
        notes.contains(searchLower) || 
        nextShot.contains(searchLower) ||
        shot.shot.dosisG.toString().contains(searchLower)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por grano, notas...") },
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

        // Lista de shots
        if (filteredShots.isEmpty()) {
            EmptyState(
                message = "No se encontraron shots con esa búsqueda.",
                actionLabel = "Limpiar búsqueda",
                onAction = { searchQuery = "" }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredShots) { shot ->
                    val beanLabel = "${shot.beanTostador} - ${shot.beanCafe}"
                    ShotCard(
                        shot = shot,
                        beanLabel = beanLabel,
                        onEdit = { navController.navigate("shots/edit/${shot.shot.id}") },
                        onDelete = { shotToDelete = shot.shot.id }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (shotToDelete != null) {
        AlertDialog(
            onDismissRequest = { shotToDelete = null },
            title = { Text("Eliminar shot") },
            text = { Text("¿Estás seguro de que quieres eliminar este shot? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        shotToDelete?.let { vm.deleteShot(it) }
                        shotToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { shotToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
