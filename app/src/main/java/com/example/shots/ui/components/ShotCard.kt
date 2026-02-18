package com.example.shots.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shots.data.model.ShotDetails
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.shots.ui.components.getTimerStatus
import com.example.shots.ui.components.getYieldStatus
import com.example.shots.ui.components.StatusColor
import com.example.shots.ui.components.StatusIndicator

@Composable
fun ShotCard(
    shot: ShotDetails,
    beanLabel: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expanded = remember { mutableStateOf(false) }
    
    val date = remember(shot.shot.fecha) {
        Instant.ofEpochMilli(shot.shot.fecha).atZone(ZoneId.systemDefault()).toLocalDate()
    }
    
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()) }
    
    val ratio = remember(shot.shot.ratio) { String.format("%.2f", shot.shot.ratio) }
    val rating = remember(shot.shot.calificacion) { shot.shot.calificacion ?: 0 }
    
    // Calcular estado de timer y yield
    val timerStatus = remember(shot.shot.tiempoSeg, shot.shot.dosisG, shot.shot.rendimientoG) {
        getTimerStatus(shot.shot.tiempoSeg, shot.shot.dosisG, shot.shot.rendimientoG)
    }
    
    val yieldStatus = remember(shot.shot.dosisG, shot.shot.rendimientoG) {
        getYieldStatus(shot.shot.dosisG, shot.shot.rendimientoG)
    }
    
    // Color según rating - se calcula en contexto @Composable
    val ratingGradient = when {
        rating >= 9 -> listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
        rating >= 7 -> listOf(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer
        )
        rating >= 5 -> listOf(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiaryContainer
        )
        else -> listOf(
            MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
            MaterialTheme.colorScheme.errorContainer
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded.value = !expanded.value }
        ) {
            // Header compacto con gradiente según rating
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (rating > 0) {
                            Brush.horizontalGradient(
                                colors = ratingGradient.map { it.copy(alpha = 0.15f) }
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grano + Fecha (izquierda)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = beanLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = date.format(dateFormatter),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.9f
                        )
                    }
                    
                    // Timer y Yield (centro)
                    if (shot.shot.tiempoSeg != null || shot.shot.rendimientoG > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (shot.shot.tiempoSeg != null) {
                                StatusIndicator(
                                    label = "Timer",
                                    value = "${shot.shot.tiempoSeg}s",
                                    status = timerStatus
                                )
                            }
                            StatusIndicator(
                                label = "Yield",
                                value = "${shot.shot.rendimientoG}g",
                                status = yieldStatus
                            )
                        }
                    }
                    
                    // Rating + Chevron (derecha)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (rating > 0) {
                            Text(
                                text = "★",
                                style = MaterialTheme.typography.labelMedium,
                                color = ratingGradient[0]
                            )
                            Text(
                                text = rating.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ratingGradient[0]
                            )
                        }
                        Icon(
                            imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expandir",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Contenido expandible
            if (expanded.value) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // SECCIÓN 1: MÉTRICAS PRINCIPALES
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "☕ Core Metrics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CompactMetricBadge(
                                emoji = "☕",
                                value = "${shot.shot.dosisG}g",
                                label = "Dose",
                                modifier = Modifier.weight(1f)
                            )
                            CompactMetricBadge(
                                emoji = "💧",
                                value = "${shot.shot.rendimientoG}g",
                                label = "Yield",
                                modifier = Modifier.weight(1f)
                            )
                            CompactMetricBadge(
                                emoji = "⚖️",
                                value = ratio,
                                label = "Ratio",
                                modifier = Modifier.weight(1f)
                            )
                            if (shot.shot.tiempoSeg != null) {
                                CompactMetricBadge(
                                    emoji = "⏱️",
                                    value = "${shot.shot.tiempoSeg}s",
                                    label = "Time",
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // SECCIÓN 2: EQUIPO
                    if (shot.shot.temperaturaC != null || !shot.shot.ajusteMolienda.isNullOrBlank() || 
                        !shot.grinderNombre.isNullOrBlank() || !shot.profileNombre.isNullOrBlank()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "⚙️ Equipment",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (shot.shot.temperaturaC != null) {
                                        CompactInfoChip(icon = "🌡️", text = "${shot.shot.temperaturaC.toInt()}°C")
                                    }
                                    if (!shot.shot.ajusteMolienda.isNullOrBlank()) {
                                        CompactInfoChip(icon = "⚙️", text = shot.shot.ajusteMolienda)
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!shot.grinderNombre.isNullOrBlank()) {
                                        CompactInfoChip(icon = "🔧", text = shot.grinderNombre)
                                    }
                                    if (!shot.profileNombre.isNullOrBlank()) {
                                        CompactInfoChip(icon = "📋", text = shot.profileNombre)
                                    }
                                }
                            }
                        }
                    }

                    // SECCIÓN 3: PRE-INFUSIÓN
                    if (shot.shot.preinfusionTiempoSeg != null || shot.shot.preinfusionPresionBar != null) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "⏱️ Pre-Infusion",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (shot.shot.preinfusionTiempoSeg != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "⏱️",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f
                                        )
                                        Text(
                                            text = "${shot.shot.preinfusionTiempoSeg}s",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f
                                        )
                                    }
                                }
                                if (shot.shot.preinfusionPresionBar != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "📊",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f
                                        )
                                        Text(
                                            text = "${String.format("%.1f", shot.shot.preinfusionPresionBar)}bar",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SECCIÓN 4: TASTING NOTES
                    if (!shot.shot.aromaNotes.isNullOrBlank() || !shot.shot.saborNotes.isNullOrBlank() || 
                        !shot.shot.cuerpo.isNullOrBlank() || !shot.shot.acidez.isNullOrBlank() || !shot.shot.finish.isNullOrBlank()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "👃 Tasting Notes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!shot.shot.aromaNotes.isNullOrBlank()) {
                                        TastingNoteBadge("Aroma", shot.shot.aromaNotes, MaterialTheme.colorScheme.primary)
                                    }
                                    if (!shot.shot.saborNotes.isNullOrBlank()) {
                                        TastingNoteBadge("Sabor", shot.shot.saborNotes, MaterialTheme.colorScheme.secondary)
                                    }
                                    if (!shot.shot.cuerpo.isNullOrBlank()) {
                                        TastingNoteBadge("Cuerpo", shot.shot.cuerpo, MaterialTheme.colorScheme.tertiary)
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!shot.shot.acidez.isNullOrBlank()) {
                                        TastingNoteBadge("Acidez", shot.shot.acidez, MaterialTheme.colorScheme.primary)
                                    }
                                    if (!shot.shot.finish.isNullOrBlank()) {
                                        TastingNoteBadge("Finish", shot.shot.finish, MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }

                    // SECCIÓN 5: RECOMENDACIÓN
                    if (!shot.shot.nextShotNotes.isNullOrBlank()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "💡 Next Shot",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = shot.shot.nextShotNotes,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // SECCIÓN 6: NOTAS
                    if (!shot.shot.notas.isNullOrBlank()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "📝 Notes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = shot.shot.notas,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // SECCIÓN 7: ACCIONES
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== COMPONENTES ====================

@Composable
fun CompactMetricBadge(
    emoji: String,
    value: String,
    label: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.5.dp)
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.labelSmall,
            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.9f
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f
        )
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.65f
            )
        }
    }
}

@Composable
fun TastingNoteBadge(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 3.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.75f
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.7f
        )
    }
}

@Composable
fun CompactInfoChip(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 3.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.labelSmall,
            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.75f
        )
    }
}

@Composable
fun MetricBadge(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
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
fun InfoBadge(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
