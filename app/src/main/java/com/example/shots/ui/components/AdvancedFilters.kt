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
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onExpandChange(!expanded) },
                modifier = Modifier.then(
                    Modifier.then(
                        if (expanded) Modifier else Modifier
                    )
                )
            ) {
                Text(text = if (expanded) "▼" else "▶", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Expanded content
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Rating filters - compact row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = filters.minRating?.toString() ?: "",
                        onValueChange = { value ->
                            onFiltersChange(
                                filters.copy(minRating = value.toIntOrNull()?.coerceIn(1, 10))
                            )
                        },
                        label = { Text("Min", style = MaterialTheme.typography.labelSmall) },
                        textStyle = MaterialTheme.typography.labelSmall,
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
                        label = { Text("Max", style = MaterialTheme.typography.labelSmall) },
                        textStyle = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(0.dp),
                        singleLine = true
                    )
                }

                // Bean selector - compact
                if (beans.isNotEmpty()) {
                    OutlinedTextField(
                        value = beans.find { it.first == filters.selectedBeamId }?.second ?: "Todo",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Grano", style = MaterialTheme.typography.labelSmall) },
                        textStyle = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        trailingIcon = {
                            if (filters.selectedBeamId != null) {
                                IconButton(
                                    onClick = { onFiltersChange(filters.copy(selectedBeamId = null)) },
                                    modifier = Modifier.padding(0.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.then(Modifier))
                                }
                            }
                        }
                    )
                }

                // Grinder selector - compact
                if (grinders.isNotEmpty()) {
                    OutlinedTextField(
                        value = grinders.find { it.first == filters.selectedGrinderId }?.second ?: "Todo",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Molino", style = MaterialTheme.typography.labelSmall) },
                        textStyle = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        trailingIcon = {
                            if (filters.selectedGrinderId != null) {
                                IconButton(
                                    onClick = { onFiltersChange(filters.copy(selectedGrinderId = null)) },
                                    modifier = Modifier.padding(0.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        }
                    )
                }

                // Clear button - small
                if (filters != ShotFilters()) {
                    Button(
                        onClick = { onFiltersChange(ShotFilters()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    ) {
                        Text("Limpiar", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
