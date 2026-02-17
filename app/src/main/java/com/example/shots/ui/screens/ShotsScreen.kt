package com.example.espressoshots.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ShotsScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val shots = vm.shots.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    if (shots.value.isEmpty()) {
        EmptyState(
            message = "No hay shots. Agrega uno para empezar.",
            actionLabel = "Agregar shot",
            onAction = { navController.navigate("shots/new") }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(shots.value) { item ->
            val date = Instant.ofEpochMilli(item.shot.fecha)
                .atZone(ZoneId.systemDefault()).toLocalDate()
                .format(formatter)
            val beanLabel = "${item.beanTostador} - ${item.beanCafe}"
            val grinderLabel = item.grinderNombre ?: "Sin molino"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("shots/edit/${item.shot.id}") }
            ) {
                Text(text = beanLabel, style = MaterialTheme.typography.titleMedium)
                Text(text = "$date | ${item.shot.dosisG}g -> ${item.shot.rendimientoG}g | ratio ${"%.2f".format(item.shot.ratio)}")
                Text(text = grinderLabel, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
