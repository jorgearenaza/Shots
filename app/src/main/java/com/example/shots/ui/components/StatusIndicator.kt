package com.example.espressoshots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusIndicator(
    label: String,
    value: String,
    status: StatusColor = StatusColor.NEUTRAL,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador circular
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = when (status) {
                        StatusColor.GREEN -> MaterialTheme.colorScheme.primary
                        StatusColor.YELLOW -> MaterialTheme.colorScheme.tertiary
                        StatusColor.RED -> MaterialTheme.colorScheme.error
                        StatusColor.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    shape = CircleShape
                )
        )
        
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

enum class StatusColor {
    GREEN,
    YELLOW,
    RED,
    NEUTRAL
}

fun getTimerStatus(tiempoSeg: Int?, dosisG: Double?, rendimientoG: Double?): StatusColor {
    if (tiempoSeg == null || dosisG == null || rendimientoG == null) return StatusColor.NEUTRAL
    
    // Ratio esperado
    val ratio = rendimientoG / dosisG
    
    return when {
        // Muy rápido (< 25s) - típicamente sobreextracción
        tiempoSeg < 25 -> StatusColor.RED
        // Tiempo corto (25-30s) - podría ser óptimo para ratios altos
        tiempoSeg < 30 -> if (ratio > 2.0) StatusColor.GREEN else StatusColor.YELLOW
        // Tiempo óptimo (30-35s)
        tiempoSeg <= 35 -> StatusColor.GREEN
        // Tiempo largo (35-45s)
        tiempoSeg <= 45 -> StatusColor.YELLOW
        // Muy largo (> 45s) - típicamente subeextracción
        else -> StatusColor.RED
    }
}

fun getYieldStatus(dosisG: Double?, rendimientoG: Double?): StatusColor {
    if (dosisG == null || rendimientoG == null) return StatusColor.NEUTRAL
    
    val ratio = rendimientoG / dosisG
    
    return when {
        // Ratio muy bajo (< 1.5)
        ratio < 1.5 -> StatusColor.RED
        // Ratio bajo (1.5-1.8)
        ratio < 1.8 -> StatusColor.YELLOW
        // Ratio óptimo (1.8-2.2)
        ratio <= 2.2 -> StatusColor.GREEN
        // Ratio alto (2.2-2.5)
        ratio <= 2.5 -> StatusColor.YELLOW
        // Ratio muy alto (> 2.5)
        else -> StatusColor.RED
    }
}
