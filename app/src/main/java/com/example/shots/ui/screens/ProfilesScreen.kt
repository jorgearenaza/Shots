package com.example.espressoshots.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.ui.components.EmptyState
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun ProfilesScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val profiles = vm.profiles.collectAsState()

    if (profiles.value.isEmpty()) {
        EmptyState(
            message = "No hay perfiles. Agrega uno para empezar.",
            actionLabel = "Agregar perfil",
            onAction = { navController.navigate("profiles/new") }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(profiles.value) { profile ->
            Text(
                text = profile.nombre,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { navController.navigate("profiles/edit/${profile.id}") }
            )
        }
    }
}
