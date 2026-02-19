package com.example.shots.ui.components

data class ValidationWarning(
    val type: ValidationType,
    val message: String,
    val severity: WarningSeverity,
    val emoji: String
)

enum class ValidationType {
    RATIO_LOW,          // Ratio < 1.5
    RATIO_HIGH,         // Ratio > 3.5
    TIME_VERY_SHORT,    // < 20s
    TIME_VERY_LONG,     // > 70s
    RATING_INCONSISTENT, // Rating alto pero parámetros raros
    DOSE_UNUSUAL,       // Dosis muy diferente al promedio
    YIELD_LOW           // Rendimiento muy bajo para dosis
}

enum class WarningSeverity {
    INFO,      // Información curiosa
    WARNING,   // Atención, algo inusual
    ERROR      // Muy fuera de rango
}

/**
 * Analiza un shot y retorna validaciones
 */
fun analyzeShot(
    tiempoSeg: Int?,
    dosisG: Double,
    rendimientoG: Double,
    calificacion: Int?,
    allShots: List<Pair<Int?, Double>>  // Datos históricos: (tiempo, dosis)
): List<ValidationWarning> {
    val warnings = mutableListOf<ValidationWarning>()
    
    val ratio = if (dosisG > 0) rendimientoG / dosisG else 0.0
    val avgDosis = if (allShots.isNotEmpty()) allShots.map { it.second }.average() else dosisG
    val avgTiempo = if (allShots.isNotEmpty()) allShots.mapNotNull { it.first }.average() else tiempoSeg?.toDouble() ?: 30.0
    
    // Validar Ratio
    when {
        ratio < 1.5 -> warnings.add(
            ValidationWarning(
                ValidationType.RATIO_LOW,
                "Ratio muy bajo (${"%.2f".format(ratio)}:1) - extracción insuficiente",
                WarningSeverity.WARNING,
                "📉"
            )
        )
        ratio > 3.5 -> warnings.add(
            ValidationWarning(
                ValidationType.RATIO_HIGH,
                "Ratio muy alto (${"%.2f".format(ratio)}:1) - puede ser sobre-extracción",
                WarningSeverity.WARNING,
                "📈"
            )
        )
    }
    
    // Validar Tiempo
    tiempoSeg?.let {
        when {
            it < 20 -> warnings.add(
                ValidationWarning(
                    ValidationType.TIME_VERY_SHORT,
                    "Extracción muy rápida (${it}s) - típicamente subextracción",
                    WarningSeverity.ERROR,
                    "⚡"
                )
            )
            it > 70 -> warnings.add(
                ValidationWarning(
                    ValidationType.TIME_VERY_LONG,
                    "Extracción muy larga (${it}s) - riesgo de sobre-extracción",
                    WarningSeverity.WARNING,
                    "🐌"
                )
            )
            it < (avgTiempo * 0.7) -> warnings.add(
                ValidationWarning(
                    ValidationType.TIME_VERY_SHORT,
                    "Mucho más rápido que tu promedio (${it}s vs ${"%.0f".format(avgTiempo)}s)",
                    WarningSeverity.INFO,
                    "⏱️"
                )
            )
        }
    }
    
    // Validar Dosis
    if (dosisG < (avgDosis * 0.6)) {
        warnings.add(
            ValidationWarning(
                ValidationType.DOSE_UNUSUAL,
                "Dosis inusualmente baja (${dosisG}g vs ${"%.1f".format(avgDosis)}g promedio)",
                WarningSeverity.INFO,
                "⚖️"
            )
        )
    }
    
    // Validar Rating vs Parámetros
    calificacion?.let {
        if (it >= 8 && ratio < 1.8) {
            warnings.add(
                ValidationWarning(
                    ValidationType.RATING_INCONSISTENT,
                    "Rating alto pero ratio bajo - inusual comparado a tu historial",
                    WarningSeverity.INFO,
                    "🤔"
                )
            )
        }
    }
    
    // Validar Yield
    if (rendimientoG < (dosisG * 1.2)) {
        warnings.add(
            ValidationWarning(
                ValidationType.YIELD_LOW,
                "Rendimiento muy bajo - posible pérdida de café",
                WarningSeverity.WARNING,
                "💧"
            )
        )
    }
    
    return warnings.distinctBy { it.type }
}
