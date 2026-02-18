package com.example.espressoshots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.ui.components.AverageMetricsCard
import com.example.espressoshots.ui.components.EmptyState
import com.example.espressoshots.ui.components.InsightsCard
import com.example.espressoshots.ui.components.RatingsStatsCard
import com.example.espressoshots.ui.components.ShotsPerBeanChart
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun StatsScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val shots = vm.shots.collectAsState()

    if (shots.value.isEmpty()) {
        EmptyState(
            message = "No hay datos de shots para mostrar estadísticas.",
            actionLabel = "Ir a Shots",
            onAction = { navController.navigateUp() }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "📈 Estadísticas y Analytics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Análisis de tus ${shots.value.size} shots",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            AverageMetricsCard(shots.value)
        }

        item {
            RatingsStatsCard(shots.value)
        }

        item {
            ShotsPerBeanChart(shots.value)
        }
        
        item {
            InsightsCard(shots.value)
        }
    }
}
