package com.example.shots.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profiles",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String?,
    val parametros: String?,
    val activo: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
