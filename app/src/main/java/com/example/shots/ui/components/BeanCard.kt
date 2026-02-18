package com.example.espressoshots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.espressoshots.data.model.BeanEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun BeanCard(
    bean: BeanEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPurchaseUpdate: (() -> Unit)? = null,
    onRoastUpdate: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // La frescura se calcula desde la fecha de tostado
    val roastDate = Instant.ofEpochMilli(bean.fechaTostado).atZone(ZoneId.systemDefault()).toLocalDate()
    val daysSinceRoast = ChronoUnit.DAYS.between(roastDate, LocalDate.now()).toInt()
    
    // Indicador de frescura: 0-14 días = verde, 14-21 = amarillo, 21+ = rojo
    val freshness = when {
        daysSinceRoast <= 14 -> 1.0f
        daysSinceRoast <= 21 -> 0.6f
        else -> 0.3f
    }
    val freshnessColor = when {
        daysSinceRoast <= 14 -> MaterialTheme.colorScheme.primary
        daysSinceRoast <= 21 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit() },
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
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Tostador y café
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bean.tostador,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = bean.cafe,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${daysSinceRoast}d",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = freshnessColor
                )
            }

            // Barra de frescura
            LinearProgressIndicator(
                progress = { freshness },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = freshnessColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Fechas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val tostDate = Instant.ofEpochMilli(bean.fechaTostado).atZone(ZoneId.systemDefault()).toLocalDate()
                val compDate = Instant.ofEpochMilli(bean.fechaCompra).atZone(ZoneId.systemDefault()).toLocalDate()
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text("Tostado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(tostDate.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text("Compra", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(compDate.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            // Información de origen si existe
            if (!bean.pais.isNullOrBlank() || !bean.proceso.isNullOrBlank() || !bean.varietal.isNullOrBlank() || bean.altitud != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "☕ Origen",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!bean.pais.isNullOrBlank()) {
                            Text(
                                text = "🌍 ${bean.pais}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (!bean.proceso.isNullOrBlank()) {
                            Text(
                                text = "⚙️ ${bean.proceso}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!bean.varietal.isNullOrBlank()) {
                            Text(
                                text = "🌱 ${bean.varietal}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (bean.altitud != null) {
                            Text(
                                text = "⛰️ ${bean.altitud}m",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Notas si existen
            if (!bean.notas.isNullOrBlank()) {
                Text(
                    text = "📝 ${bean.notas}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            // Botones de frescura
            if (onPurchaseUpdate != null || onRoastUpdate != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onPurchaseUpdate != null) {
                        Button(
                            onClick = onPurchaseUpdate,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        ) {
                            Text("Compre mas", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    if (onRoastUpdate != null) {
                        Button(
                            onClick = onRoastUpdate,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("Tostado nuevo", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Acciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (onPurchaseUpdate != null || onRoastUpdate != null) 4.dp else 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.padding(horizontal = 4.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete, modifier = Modifier.padding(horizontal = 4.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
