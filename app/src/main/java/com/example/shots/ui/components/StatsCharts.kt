package com.example.espressoshots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.espressoshots.data.model.ShotDetails

@Composable
fun RatingsStatsCard(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    // Contar ratings
    val ratingCounts = mutableMapOf<Int, Int>()
    shots.forEach { shot ->
        val rating = shot.shot.calificacion ?: 0
        if (rating > 0) {
            ratingCounts[rating] = (ratingCounts[rating] ?: 0) + 1
        }
    }

    val maxCount = ratingCounts.values.maxOrNull() ?: 1

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "⭐ Distribución de Calificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Gráfico de barras simple
            for (rating in 10 downTo 1) {
                val count = ratingCounts[rating] ?: 0
                val barWidth = if (maxCount > 0) (count.toFloat() / maxCount) else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$rating★",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(30.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(16.dp)
                            .background(
                                color = when (rating) {
                                    in 8..10 -> MaterialTheme.colorScheme.primary
                                    in 6..7 -> MaterialTheme.colorScheme.secondary
                                    in 4..5 -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.error
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxWidth(barWidth)
                    )
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AverageMetricsCard(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    if (shots.isEmpty()) return

    val averageRatio = shots.map { it.shot.ratio }.average()
    val averageTime = shots.mapNotNull { it.shot.tiempoSeg }.let {
        if (it.isEmpty()) 0.0 else it.average()
    }
    val averageRating = shots.mapNotNull { it.shot.calificacion }.let {
        if (it.isEmpty()) 0.0 else it.average()
    }
    val totalShots = shots.size

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📊 Estadísticas Generales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBox(
                    label = "Total Shots",
                    value = totalShots.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    label = "Ratio Prom.",
                    value = String.format("%.2f", averageRatio),
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    label = "Tiempo Prom.",
                    value = "${averageTime.toInt()}s",
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    label = "Rating Prom.",
                    value = String.format("%.1f", averageRating),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ShotsPerBeanChart(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    if (shots.isEmpty()) return

    // Agrupar shots por grano
    val shotsPerBean = shots.groupingBy { "${it.beanTostador} - ${it.beanCafe}" }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .take(5)

    if (shotsPerBean.isEmpty()) return

    val maxShots = shotsPerBean.maxOf { it.second }.toFloat()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "☕ Top Granos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            shotsPerBean.forEach { (bean, count) ->
                val barWidth = (count.toFloat() / maxShots).coerceIn(0.1f, 1f)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bean,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(barWidth)
                            .height(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }
    }
}
