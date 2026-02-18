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
import com.example.espressoshots.ui.components.ProfileCard
import com.example.espressoshots.ui.theme.AppSpacing
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun ProfilesScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val profiles = vm.profiles.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var profileToDelete by remember { mutableStateOf<Long?>(null) }

    if (profiles.value.isEmpty()) {
        EmptyState(
            message = "No hay perfiles. Agrega uno para empezar.",
            actionLabel = "Agregar perfil",
            onAction = { navController.navigate("profiles/new") }
        )
        return
    }

    val filteredProfiles = profiles.value.filter { profile ->
        val label = profile.nombre.lowercase()
        val desc = (profile.descripcion ?: "").lowercase()
        val searchLower = searchQuery.lowercase()
        label.contains(searchLower) || desc.contains(searchLower)
    }

    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar perfil...") },
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
                .padding(AppSpacing.large),
            singleLine = true
        )

        if (filteredProfiles.isEmpty()) {
            EmptyState(
                message = "No se encontraron perfiles con esa búsqueda.",
                actionLabel = "Limpiar búsqueda",
                onAction = { searchQuery = "" }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = AppSpacing.large, end = AppSpacing.large, top = AppSpacing.large, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
            ) {
                items(filteredProfiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        onEdit = { navController.navigate("profiles/edit/${profile.id}") },
                        onDelete = { profileToDelete = profile.id }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (profileToDelete != null) {
        AlertDialog(
            onDismissRequest = { profileToDelete = null },
            title = { Text("Eliminar perfil") },
            text = { Text("¿Estás seguro de que quieres eliminar este perfil? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileToDelete?.let { vm.deleteProfile(it) }
                        profileToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { profileToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
