package com.example.shots.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shots.ui.components.EmptyState
import com.example.shots.ui.components.ShotCard
import com.example.shots.ui.components.AdvancedFiltersPanel
import com.example.shots.ui.components.ShotFilters
import com.example.shots.ui.components.SkeletonShotsScreen
import com.example.shots.ui.theme.AppSpacing
import com.example.shots.viewmodel.MainViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun ShotsScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val shots = vm.shots.collectAsState()
    val beans = vm.beans.collectAsState()
    val isLoading = vm.isLoadingShots.collectAsState()
    val persistedFilters = vm.persistedFilters.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var shotToDelete by remember { mutableStateOf<Long?>(null) }
    var filterRating by remember { mutableStateOf<Int?>(null) }
    var filterRecent by remember { mutableStateOf(false) }
    
    // Advanced filters state - initialized from persisted filters
    var advancedFilters by remember { mutableStateOf(persistedFilters.value) }
    var expandAdvancedFilters by remember { mutableStateOf(false) }
    
    // Save filters whenever they change
    LaunchedEffect(advancedFilters) {
        vm.saveFilters(advancedFilters)
    }
    
    // Use filtered shots if advanced filters are active
    val shotsToDisplay = if (
        advancedFilters.minRating != null || 
        advancedFilters.maxRating != null || 
        advancedFilters.selectedBeamId != null || 
        advancedFilters.selectedGrinderId != null
    ) {
        vm.getFilteredShots(advancedFilters).collectAsState()
    } else {
        shots
    }

    // Show loading skeleton while fetching initial data
    if (isLoading.value && shots.value.isEmpty()) {
        SkeletonShotsScreen()
        return
    }

    if (shots.value.isEmpty()) {
        EmptyState(
            message = "No hay shots. Agrega uno para empezar.",
            actionLabel = "Agregar shot",
            onAction = { navController.navigate("shots/new") }
        )
        return
    }
    
    // Calcular estadísticas rápidas del día
    val today = LocalDate.now()
    val todayShots = remember(shots.value) {
        shots.value.filter {
            val shotDate = Instant.ofEpochMilli(it.shot.fecha).atZone(ZoneId.systemDefault()).toLocalDate()
            shotDate == today
        }
    }
    val todayAvgRating = remember(todayShots) {
        todayShots.mapNotNull { it.shot.calificacion }.let {
            if (it.isEmpty()) 0.0 else it.average()
        }
    }

    // Filtrar shots por búsqueda y filtros
    val filteredShots = remember(shotsToDisplay.value, searchQuery, filterRating, filterRecent) {
        val currentFilterRating = filterRating  // Extraer a variable local para evitar smart cast issues
        var result = shotsToDisplay.value.filter { shot ->
            val beanLabel = "${shot.beanTostador} - ${shot.beanCafe}".lowercase()
            val notes = (shot.shot.notas ?: "").lowercase()
            val nextShot = (shot.shot.nextShotNotes ?: "").lowercase()
            val searchLower = searchQuery.lowercase()
            
            beanLabel.contains(searchLower) || 
            notes.contains(searchLower) || 
            nextShot.contains(searchLower) ||
            shot.shot.dosisG.toString().contains(searchLower)
        }
        
        // Aplicar filtro de rating
        if (currentFilterRating != null) {
            result = result.filter { (it.shot.calificacion ?: 0) >= currentFilterRating }
        }
        
        // Aplicar filtro de recientes (últimos 7 días)
        if (filterRecent) {
            val weekAgo = today.minusDays(7)
            result = result.filter {
                val shotDate = Instant.ofEpochMilli(it.shot.fecha).atZone(ZoneId.systemDefault()).toLocalDate()
                shotDate.isAfter(weekAgo) || shotDate.isEqual(weekAgo)
            }
        }
        result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // Header con stats rápidas
        if (todayShots.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                    .padding(horizontal = AppSpacing.large, vertical = AppSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☕",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hoy: ${todayShots.size} shot${if (todayShots.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (todayAvgRating > 0) {
                        Text(
                            text = "Rating promedio: ${String.format("%.1f", todayAvgRating)}⭐",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Barra de búsqueda + Filtros en la misma fila
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.large, vertical = AppSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar...", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), textAlign = TextAlign.Center) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.labelSmall.copy(textAlign = TextAlign.Center, fontSize = 10.sp)
            )
            
            // Botón para expandir/contraer filtros
            FilterChip(
                selected = expandAdvancedFilters,
                onClick = { expandAdvancedFilters = !expandAdvancedFilters },
                label = {
                    Text(
                        text = if (advancedFilters != ShotFilters()) "⚙️" else "⚙️",
                        style = TextStyle(fontSize = 12.sp)
                    )
                },
                modifier = Modifier.height(40.dp)
            )
        }
        
        // Panel de filtros avanzados (sin header redundante)
        AdvancedFiltersPanel(
            filters = advancedFilters,
            onFiltersChange = { advancedFilters = it },
            expanded = expandAdvancedFilters,
            onExpandChange = { expandAdvancedFilters = it },
            beans = beans.value.map { it.id to "${it.tostador} - ${it.cafe}" },
            grinders = vm.grinders.collectAsState().value.map { it.id to it.nombre },
            modifier = Modifier.padding(horizontal = AppSpacing.large),
            showHeader = false
        )
        
        // Lista de shots - ocupa el espacio restante con scroll
        if (filteredShots.isEmpty()) {
            EmptyState(
                message = "No se encontraron shots con esos filtros.",
                actionLabel = "Limpiar filtros",
                onAction = {
                    searchQuery = ""
                    filterRating = null
                    filterRecent = false
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = AppSpacing.large),
                contentPadding = PaddingValues(top = AppSpacing.small, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
            ) {
                items(filteredShots.sortedByDescending { it.shot.fecha }) { shot ->
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
