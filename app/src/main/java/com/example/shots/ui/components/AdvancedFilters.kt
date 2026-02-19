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
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp

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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        // Header
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
                onClick = { onExpandChange(!expanded) }
            ) {
                Text(text = if (expanded) "▼" else "▶")
            }
        }

        // Expanded content
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rating filters
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
                        label = { Text("Min Rating") },
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
                        label = { Text("Max Rating") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Bean selector
                if (beans.isNotEmpty()) {
                    OutlinedTextField(
                        value = beans.find { it.first == filters.selectedBeamId }?.second ?: "All Beans",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bean") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (filters.selectedBeamId != null) {
                                IconButton(onClick = { onFiltersChange(filters.copy(selectedBeamId = null)) }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        }
                    )
                }

                // Grinder selector
                if (grinders.isNotEmpty()) {
                    OutlinedTextField(
                        value = grinders.find { it.first == filters.selectedGrinderId }?.second ?: "All Grinders",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Grinder") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (filters.selectedGrinderId != null) {
                                IconButton(onClick = { onFiltersChange(filters.copy(selectedGrinderId = null)) }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        }
                    )
                }

                // Clear button
                if (filters != ShotFilters()) {
                    Button(
                        onClick = { onFiltersChange(ShotFilters()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear Filters")
                    }
                }
            }
        }
    }
}
