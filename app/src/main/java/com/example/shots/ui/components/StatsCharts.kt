package com.example.shots.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shots.data.model.ShotDetails
import kotlin.math.roundToInt

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

    if (ratingCounts.isEmpty()) return

    val maxCount = ratingCounts.values.maxOrNull() ?: 1
    val totalRated = ratingCounts.values.sum()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "Distribución de Ratings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$totalRated shots calificados",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Gráfico de barras animado
            for (rating in 10 downTo 1) {
                val count = ratingCounts[rating] ?: 0
                val barWidth = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
                val animatedWidth by animateFloatAsState(
                    targetValue = barWidth,
                    animationSpec = tween(durationMillis = 600, delayMillis = (10 - rating) * 50),
                    label = "barWidth"
                )
                val percentage = if (totalRated > 0) (count.toFloat() / totalRated * 100).roundToInt() else 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "$rating★",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.width(38.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedWidth.coerceAtLeast(0.02f))
                                .height(20.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = when (rating) {
                                            in 9..10 -> listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.tertiary
                                            )
                                            in 7..8 -> listOf(
                                                MaterialTheme.colorScheme.secondary,
                                                MaterialTheme.colorScheme.secondaryContainer
                                            )
                                            in 5..6 -> listOf(
                                                MaterialTheme.colorScheme.tertiary,
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            )
                                            else -> listOf(
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                            )
                                        }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )
                    }
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.width(28.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(32.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val averageTemp = shots.mapNotNull { it.shot.temperaturaC }.let {
        if (it.isEmpty()) 0.0 else it.average()
    }
    val averageRating = shots.mapNotNull { it.shot.calificacion }.let {
        if (it.isEmpty()) 0.0 else it.average()
    }
    val totalShots = shots.size
    
    // Calcular mejor y peor shot
    val bestShot = shots.maxByOrNull { it.shot.calificacion ?: 0 }
    val shotsWithRating = shots.filter { (it.shot.calificacion ?: 0) > 0 }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "Resumen General",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBox(
                    emoji = "☕",
                    label = "Total",
                    value = totalShots.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    emoji = "⚖️",
                    label = "Ratio",
                    value = String.format("%.2f", averageRatio),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBox(
                    emoji = "⏱️",
                    label = "Tiempo",
                    value = "${averageTime.roundToInt()}s",
                    modifier = Modifier.weight(1f)
                )
                if (averageTemp > 0) {
                    MetricBox(
                        emoji = "🌡️",
                        label = "Temp",
                        value = "${averageTemp.roundToInt()}°C",
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    MetricBox(
                        emoji = "⭐",
                        label = "Rating",
                        value = String.format("%.1f", averageRating),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Mejor shot
            if (bestShot != null && shotsWithRating.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "🏆 Mejor Shot",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${bestShot.beanTostador} - ${bestShot.beanCafe}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "★${bestShot.shot.calificacion}/10",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Ratio: ${String.format("%.2f", bestShot.shot.ratio)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
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
        .take(8)

    if (shotsPerBean.isEmpty()) return

    val maxShots = shotsPerBean.maxOf { it.second }.toFloat()
    val totalShots = shots.size

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☕",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "Granos Favoritos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Más utilizados",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            shotsPerBean.forEachIndexed { index, (bean, count) ->
                val barWidth = (count.toFloat() / maxShots).coerceIn(0.15f, 1f)
                val animatedWidth by animateFloatAsState(
                    targetValue = barWidth,
                    animationSpec = tween(durationMillis = 500, delayMillis = index * 60),
                    label = "beanBarWidth"
                )
                val percentage = ((count.toFloat() / totalShots) * 100).roundToInt()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = when (index) {
                                            0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            1 -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                            2 -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = bean,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedWidth)
                                .height(8.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = when (index) {
                                            0 -> listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                            )
                                            1 -> listOf(
                                                MaterialTheme.colorScheme.secondary,
                                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                            )
                                            2 -> listOf(
                                                MaterialTheme.colorScheme.tertiary,
                                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                                            )
                                            else -> listOf(
                                                MaterialTheme.colorScheme.secondaryContainer,
                                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                            )
                                        }
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InsightsCard(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    if (shots.isEmpty()) return
    
    val insights = mutableListOf<Pair<String, String>>()
    
    // Calcular insights
    val avgRating = shots.mapNotNull { it.shot.calificacion }.let {
        if (it.isEmpty()) 0.0 else it.average()
    }
    
    val ratioRange = shots.map { it.shot.ratio }
    val avgRatio = ratioRange.average()
    val consistentRatio = ratioRange.map { kotlin.math.abs(it - avgRatio) }.average() < 0.3
    
    val last7Shots = shots.sortedByDescending { it.shot.fecha }.take(7)
    val last7Avg = last7Shots.mapNotNull { it.shot.calificacion }.let {
        if (it.isEmpty()) 0.0 else it.average()
    }
    
    // Generar insights
    if (avgRating >= 8.0) {
        insights.add("🎯" to "Excelente consistencia con rating promedio de ${String.format("%.1f", avgRating)}")
    } else if (avgRating < 6.0 && shots.size > 5) {
        insights.add("💡" to "Hay espacio para mejorar. Experimenta con diferentes ajustes")
    }
    
    if (consistentRatio) {
        insights.add("⚖️" to "Ratio muy consistente alrededor de ${String.format("%.2f", avgRatio)}")
    }
    
    if (last7Shots.size >= 7 && last7Avg > avgRating + 0.5) {
        insights.add("📈" to "Mejorando! Tus últimos shots tienen mejor rating")
    } else if (last7Shots.size >= 7 && last7Avg < avgRating - 0.5) {
        insights.add("📉" to "Últimos shots por debajo del promedio. Revisa tu técnica")
    }
    
    val mostUsedBean = shots.groupingBy { "${it.beanTostador} - ${it.beanCafe}" }
        .eachCount()
        .maxByOrNull { it.value }
    
    if (mostUsedBean != null && mostUsedBean.value > shots.size * 0.3) {
        insights.add("☕" to "Tu grano favorito es ${mostUsedBean.key}")
    }
    
    if (shots.size >= 10) {
        val highRatedShots = shots.filter { (it.shot.calificacion ?: 0) >= 8 }
        val avgHighRatio = highRatedShots.map { it.shot.ratio }.let {
            if (it.isEmpty()) 0.0 else it.average()
        }
        if (highRatedShots.isNotEmpty()) {
            insights.add("✨" to "Tus mejores shots tienen ratio de ${String.format("%.2f", avgHighRatio)}")
        }
    }
    
    if (insights.isEmpty()) {
        insights.add("📊" to "Sigue registrando shots para obtener insights personalizados")
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💡",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "Insights Personalizados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            insights.forEach { (emoji, text) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
@Composable
fun BeanRatingsAnalysis(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    if (shots.isEmpty()) return

    // Agrupar por grano y calcular rating promedio
    val beanStats = shots
        .filter { (it.shot.calificacion ?: 0) > 0 }
        .groupBy { "${it.beanTostador} - ${it.beanCafe}" }
        .mapValues { (_, shotsOfBean) ->
            val avgRating = shotsOfBean.mapNotNull { it.shot.calificacion }.average()
            val count = shotsOfBean.size
            Pair(avgRating, count)
        }
        .toList()
        .sortedByDescending { it.second.first }
        .take(6)

    if (beanStats.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏅", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "Rating por Grano",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            beanStats.forEachIndexed { index, (bean, stats) ->
                val (avgRating, count) = stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bean,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$count shots",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐ ${String.format("%.1f", avgRating)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                avgRating >= 8.5 -> MaterialTheme.colorScheme.primary
                                avgRating >= 7.0 -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.tertiary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GrinderPerformanceCard(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    if (shots.isEmpty()) return

    val grinderStats = shots
        .filter { it.grinderNombre != null && (it.shot.calificacion ?: 0) > 0 }
        .groupBy { it.grinderNombre ?: "Sin molino" }
        .mapValues { (_, shotsOfGrinder) ->
            val avgRating = shotsOfGrinder.mapNotNull { it.shot.calificacion }.average()
            val count = shotsOfGrinder.size
            Pair(avgRating, count)
        }
        .toList()
        .sortedByDescending { it.second.first }
        .take(5)

    if (grinderStats.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚙️", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "Molinos - Rendimiento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            grinderStats.forEach { (grinder, stats) ->
                val (avgRating, count) = stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = grinder,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$count usos",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "⭐ ${String.format("%.1f", avgRating)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            avgRating >= 8.5 -> MaterialTheme.colorScheme.primary
                            avgRating >= 7.0 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WinningCombinationCard(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    if (shots.isEmpty()) return

    val ratedShots = shots.filter { (it.shot.calificacion ?: 0) >= 8 }
    if (ratedShots.isEmpty()) return

    val bestShot = ratedShots.maxByOrNull { it.shot.calificacion ?: 0 } ?: return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("✨", style = MaterialTheme.typography.displayMedium)
                Text(
                    text = "Combo Ganador",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("☕ Grano", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "${bestShot.beanTostador} - ${bestShot.beanCafe}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (bestShot.grinderNombre != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("⚙️ Molino", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = bestShot.grinderNombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rating", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "⭐ ${bestShot.shot.calificacion}/10",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ratio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = String.format("%.2f", bestShot.shot.ratio),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tiempo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${bestShot.shot.tiempoSeg ?: "-"}s",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TimeDistributionCard(shots: List<ShotDetails>, modifier: Modifier = Modifier) {
    if (shots.isEmpty()) return

    val timesWithRating = shots
        .filter { it.shot.tiempoSeg != null && (it.shot.calificacion ?: 0) > 0 }
    
    if (timesWithRating.isEmpty()) return

    // Agrupar en rangos
    val ranges = mapOf(
        "20-30s" to timesWithRating.filter { it.shot.tiempoSeg!! in 20..30 },
        "30-40s" to timesWithRating.filter { it.shot.tiempoSeg!! in 31..40 },
        "40-50s" to timesWithRating.filter { it.shot.tiempoSeg!! in 41..50 },
        "50-60s" to timesWithRating.filter { it.shot.tiempoSeg!! in 51..60 },
        "60s+" to timesWithRating.filter { it.shot.tiempoSeg!! > 60 }
    ).filterValues { it.isNotEmpty() }

    if (ranges.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⏱️", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "Distribución de Tiempos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            ranges.entries.forEach { (range, shotsInRange) ->
                val avgRating = shotsInRange.mapNotNull { it.shot.calificacion }.average()
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = range,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${shotsInRange.size} shots",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(24.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            when {
                                                avgRating >= 8 -> MaterialTheme.colorScheme.primary
                                                avgRating >= 7 -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.tertiary
                                            },
                                            when {
                                                avgRating >= 8 -> MaterialTheme.colorScheme.primary
                                                avgRating >= 7 -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.tertiary
                                            }.copy(alpha = 0.5f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Text(
                            text = "⭐ ${String.format("%.1f", avgRating)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .align(Alignment.CenterStart)
                        )
                    }
                }
            }
        }
    }
}