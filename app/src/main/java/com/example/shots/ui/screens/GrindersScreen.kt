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
fun GrindersScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val grinders = vm.grinders.collectAsState()

    if (grinders.value.isEmpty()) {
        EmptyState(
            message = "No hay molinos. Agrega uno para empezar.",
            actionLabel = "Agregar molino",
            onAction = { navController.navigate("grinders/new") }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(grinders.value) { grinder ->
            Text(
                text = grinder.nombre,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { navController.navigate("grinders/edit/${grinder.id}") }
            )
        }
    }
}
