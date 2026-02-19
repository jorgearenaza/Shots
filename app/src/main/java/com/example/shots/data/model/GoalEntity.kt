package com.example.shots.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class GoalType {
    RATING_AVERAGE,    // Mejorar rating promedio
    BEAN_EXPLORATION,  // Probar N granos nuevos
    CONSISTENCY,       // Shots con rating >= X
    SHOTS_COUNT,       // Hacer N shots
    GRINDER_TEST,      // Probar N molinillos
    RATIO_MASTERY      // Mejorar control del ratio
}

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val descripcion: String?,
    val tipo: GoalType,
    val targetValue: Float,        // Ej: rating 8.0, 5 granos, 20 shots
    val currentValue: Float = 0f,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaVencimiento: Long?,   // Si es null, sin límite
    val completado: Boolean = false,
    val fechaCompletado: Long? = null,
    val emoji: String = "🎯"
)

data class GoalWithProgress(
    val goal: GoalEntity,
    val progressPercentage: Float,
    val daysLeft: Int?,
    val isAchieved: Boolean
)
