package com.example.shots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api

data class ShotFilters(
    val minRating: Int? = null,           // 1-10
    val maxRating: Int? = null,           // 1-10
    val selectedBeamId: Long? = null,
    val selectedGrinderId: Long? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFiltersPanel(
    filters: ShotFilters,
    onFiltersChange: (ShotFilters) -> Unit,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    beans: List<Pair<Long, String>>,
    grinders: List<Pair<Long, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Filtros Avanzados",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onExpandChange(!expanded) },
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Close,
                    contentDescription = "Toggle filters"
                )
            }
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rating filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = filters.minRating?.toString() ?: "",
                        onValueChange = { value ->
                            onFiltersChange(
                                filters.copy(minRating = value.toIntOrNull()?.coerceIn(1, 10))
                            )
                        },
                        label = { Text("Rating mín") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = filters.maxRating?.toString() ?: "",
                        onValueChange = { value ->
                            onFiltersChange(
                                filters.copy(maxRating = value.toIntOrNull()?.coerceIn(1, 10))
                            )
                        },
                        label = { Text("Rating máx") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Bean filter
                if (beans.isNotEmpty()) {
                    var beanExpanded by androidx.compose.runtime.mutableStateOf(false)
                    ExposedDropdownMenuBox(
                        expanded = beanExpanded,
                        onExpandedChange = { beanExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = beans.find { it.first == filters.selectedBeamId }?.second ?: "Todos los granos",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Grano") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = beanExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = beanExpanded,
                            onDismissRequest = { beanExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos los granos") },
                                onClick = {
                                    onFiltersChange(filters.copy(selectedBeamId = null))
                                    beanExpanded = false
                                }
                            )
                            beans.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        onFiltersChange(filters.copy(selectedBeamId = id))
                                        beanExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Grinder filter
                if (grinders.isNotEmpty()) {
                    var grinderExpanded by androidx.compose.runtime.mutableStateOf(false)
                    ExposedDropdownMenuBox(
                        expanded = grinderExpanded,
                        onExpandedChange = { grinderExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = grinders.find { it.first == filters.selectedGrinderId }?.second ?: "Todos los molinos",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Molino") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = grinderExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = grinderExpanded,
                            onDismissRequest = { grinderExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos los molinos") },
                                onClick = {
                                    onFiltersChange(filters.copy(selectedGrinderId = null))
                                    grinderExpanded = false
                                }
                            )
                            grinders.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        onFiltersChange(filters.copy(selectedGrinderId = id))
                                        grinderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Clear filters button
                if (filters != ShotFilters()) {
                    IconButton(
                        onClick = { onFiltersChange(ShotFilters()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Limpiar filtros")
                    }
                }
            }
        }
    }
}
