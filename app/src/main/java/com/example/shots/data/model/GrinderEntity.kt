package com.example.shots.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grinders",
    indices = [
        Index(value = ["nombre"], unique = true),
        Index("activo")
    ]
)
data class GrinderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val ajusteDefault: String?,
    val notas: String?,
    val activo: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
