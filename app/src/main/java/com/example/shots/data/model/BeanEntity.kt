package com.example.shots.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "beans",
    indices = [
        Index(value = ["tostador", "cafe", "fechaTostado"], unique = true)
    ]
)
data class BeanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tostador: String,
    val cafe: String,
    val fechaTostado: Long,
    val fechaCompra: Long,
    val notas: String?,
    // Origin tracking
    val pais: String?,
    val proceso: String?,
    val varietal: String?,
    val altitud: Int?,
    val activo: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
