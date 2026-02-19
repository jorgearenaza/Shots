package com.example.shots.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.shots.R
import com.example.shots.ui.components.EmptyState
import com.example.shots.ui.components.ProfileCard
import com.example.shots.ui.theme.AppSpacing
import com.example.shots.viewmodel.MainViewModel

@Composable
fun ProfilesScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val profiles = vm.profiles.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var profileToDelete by remember { mutableStateOf<Long?>(null) }

    if (profiles.value.isEmpty()) {
        EmptyState(
            message = stringResource(R.string.empty_profiles_message),
            actionLabel = stringResource(R.string.empty_profiles_action),
            onAction = { navController.navigate("profiles/new") }
        )
        return
    }

    // Usar búsqueda en BD si hay query, si no mostrar todos
    val filteredProfiles = if (searchQuery.isNotEmpty()) {
        vm.searchProfiles(searchQuery).collectAsState(emptyList()).value
    } else {
        profiles.value
    }

    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_profiles_hint)) },
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
                message = stringResource(R.string.no_profiles_found),
                actionLabel = stringResource(R.string.clear_profiles_search),
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
