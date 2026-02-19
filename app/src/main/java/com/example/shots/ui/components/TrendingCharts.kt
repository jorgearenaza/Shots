package com.example.shots.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.DynamicShaders

data class TrendingData(
    val dates: List<String>,
    val ratings: List<Float>,
    val ratios: List<Float>
)

@Composable
fun RatingTrendingChart(
    trendingData: TrendingData?,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📊 Rating Trending (Últimos 7 días)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (trendingData != null && trendingData.ratings.isNotEmpty()) {
                val modelProducer = CartesianChartModelProducer.build {
                    lineSeries { series(trendingData.ratings.map { (it / 10f) * 100f }) }
                }

                CartesianChartHost(
                    modelProducer = modelProducer,
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lines = listOf(
                                com.patrykandpatrick.vico.compose.cartesian.layer.lineSpec(
                                    shader = DynamicShaders.color(MaterialTheme.colorScheme.primary),
                                    backgroundShader = DynamicShaders.color(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                )
                            )
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Text(
                    text = "Promedio: ${trendingData.ratings.average().toFloat()} ⭐",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Sin datos de tendencia disponibles",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun RatioTrendingChart(
    trendingData: TrendingData?,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📈 Ratio Trending (Últimos 7 días)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (trendingData != null && trendingData.ratios.isNotEmpty()) {
                val modelProducer = CartesianChartModelProducer.build {
                    lineSeries { series(trendingData.ratios) }
                }

                CartesianChartHost(
                    modelProducer = modelProducer,
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lines = listOf(
                                com.patrykandpatrick.vico.compose.cartesian.layer.lineSpec(
                                    shader = DynamicShaders.color(MaterialTheme.colorScheme.secondary),
                                    backgroundShader = DynamicShaders.color(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                                )
                            )
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Text(
                    text = "Promedio: ${String.format("%.2f", trendingData.ratios.average())} 1:X",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Sin datos de tendencia disponibles",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
