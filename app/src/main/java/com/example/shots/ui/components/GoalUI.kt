package com.example.shots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shots.data.model.GoalEntity

@Composable
fun GoalCard(
    goal: GoalEntity,
    progressPercentage: Float,
    daysLeft: Int?,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Encabezado con título y botón de eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = goal.emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.nombre,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!goal.descripcion.isNullOrBlank()) {
                            Text(
                                text = goal.descripcion,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.height(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar", modifier = Modifier.padding(0.dp))
                }
            }

            // Detalles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Progreso",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (daysLeft != null && !goal.completado) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Días restantes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (daysLeft > 0) "$daysLeft días" else "Vencido",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (daysLeft > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                LinearProgressIndicator(
                    progress = { (progressPercentage / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(fraction = (progressPercentage / 100f).coerceIn(0f, 1f))
                        .height(8.dp),
                    color = when {
                        goal.completado -> MaterialTheme.colorScheme.primary
                        progressPercentage >= 75f -> MaterialTheme.colorScheme.secondary
                        progressPercentage >= 50f -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)
                )
            }

            // Porcentaje
            Text(
                text = "${progressPercentage.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun GoalsPanel(
    activeGoals: List<Pair<GoalEntity, Float>>,  // Goal + progress percentage
    completedCount: Int,
    onAddGoalClick: () -> Unit,
    onDeleteGoal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeGoals.isEmpty() && completedCount == 0) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 Mis Objetivos (${activeGoals.size} activos, $completedCount completados)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Botón para agregar objetivo (opcional - podrías usar +)
            }

            activeGoals.forEachIndexed { index, (goal, progressPercentage) ->
                if (index < 3) {  // Mostrar máximo 3 principales
                    GoalCard(
                        goal = goal,
                        progressPercentage = progressPercentage,
                        daysLeft = goal.fechaVencimiento?.let {
                            ((it - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
                        },
                        onDelete = { onDeleteGoal(goal.id) },
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }
        }
    }
}
