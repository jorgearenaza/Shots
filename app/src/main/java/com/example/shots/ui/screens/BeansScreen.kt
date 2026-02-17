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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun BeansScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val beans = vm.beans.collectAsState()

    if (beans.value.isEmpty()) {
        EmptyState(
            message = "No hay granos. Agrega uno para empezar.",
            actionLabel = "Agregar grano",
            onAction = { navController.navigate("beans/new") }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(beans.value) { bean ->
            val roast = Instant.ofEpochMilli(bean.fechaTostado).atZone(ZoneId.systemDefault()).toLocalDate()
            val days = ChronoUnit.DAYS.between(roast, LocalDate.now())
            val label = "${bean.tostador} - ${bean.cafe}"
            Text(
                text = "$label (frescura: ${days}d)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { navController.navigate("beans/edit/${bean.id}") }
            )
        }
    }
}
