package com.example.shots.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShotFilters(
    val minRating: Int? = null,
    val maxRating: Int? = null,
    val selectedBeamId: Long? = null,
    val selectedGrinderId: Long? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

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
    // Check if any filters are active
    val hasActiveFilters = filters != ShotFilters()
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Minimal header - just a chip button
        FilterChip(
            selected = expanded,
            onClick = { onExpandChange(!expanded) },
            label = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "⚙️ ${if (hasActiveFilters) "Filtros(${filters.minRating ?: "-"}-${filters.maxRating ?: "-"})" else "Filtros"}",
                        style = TextStyle(fontSize = 11.sp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Expanded panel
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(6.dp)
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Rating filters - very compact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    OutlinedTextField(
                        value = filters.minRating?.toString() ?: "",
                        onValueChange = { value ->
                            onFiltersChange(
                                filters.copy(minRating = value.toIntOrNull()?.coerceIn(1, 10))
                            )
                        },
                        label = { Text("Min", style = TextStyle(fontSize = 9.sp)) },
                        textStyle = TextStyle(fontSize = 10.sp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(0.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = filters.maxRating?.toString() ?: "",
                        onValueChange = { value ->
                            onFiltersChange(
                                filters.copy(maxRating = value.toIntOrNull()?.coerceIn(1, 10))
                            )
                        },
                        label = { Text("Max", style = TextStyle(fontSize = 9.sp)) },
                        textStyle = TextStyle(fontSize = 10.sp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(0.dp),
                        singleLine = true
                    )
                }

                // Bean selector - ultra compact
                if (beans.isNotEmpty()) {
                    var showBeanMenu by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = beans.find { it.first == filters.selectedBeamId }?.second ?: "",
                            onValueChange = {},
                            placeholder = { Text("Grano", style = TextStyle(fontSize = 9.sp)) },
                            readOnly = true,
                            textStyle = TextStyle(fontSize = 10.sp),
                            trailingIcon = {
                                if (filters.selectedBeamId != null) {
                                    IconButton(
                                        onClick = { onFiltersChange(filters.copy(selectedBeamId = null)) }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null)
                                    }
                                } else {
                                    Text("▼", modifier = Modifier.padding(end = 4.dp))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            singleLine = true
                        )
                        if (showBeanMenu) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                beans.forEach { (id, name) ->
                                    Text(
                                        text = name,
                                        style = TextStyle(fontSize = 9.sp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Grinder selector - ultra compact
                if (grinders.isNotEmpty()) {
                    var showGrinderMenu by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = grinders.find { it.first == filters.selectedGrinderId }?.second ?: "",
                            onValueChange = {},
                            placeholder = { Text("Molino", style = TextStyle(fontSize = 9.sp)) },
                            readOnly = true,
                            textStyle = TextStyle(fontSize = 10.sp),
                            trailingIcon = {
                                if (filters.selectedGrinderId != null) {
                                    IconButton(
                                        onClick = { onFiltersChange(filters.copy(selectedGrinderId = null)) }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null)
                                    }
                                } else {
                                    Text("▼", modifier = Modifier.padding(end = 4.dp))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            singleLine = true
                        )
                        if (showGrinderMenu) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                grinders.forEach { (id, name) ->
                                    Text(
                                        text = name,
                                        style = TextStyle(fontSize = 9.sp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Clear button - appears only if filters active
                if (hasActiveFilters) {
                    FilterChip(
                        selected = false,
                        onClick = { onFiltersChange(ShotFilters()) },
                        label = { Text("Limpiar", style = TextStyle(fontSize = 10.sp)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
