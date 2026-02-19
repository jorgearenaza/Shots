package com.example.shots.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shots",
    foreignKeys = [
        ForeignKey(
            entity = BeanEntity::class,
            parentColumns = ["id"],
            childColumns = ["bean_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = GrinderEntity::class,
            parentColumns = ["id"],
            childColumns = ["grinder_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("bean_id"),
        Index("grinder_id"),
        Index("profile_id")
    ]
)
data class ShotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: Long,
    @ColumnInfo(name = "bean_id") val beanId: Long,
    @ColumnInfo(name = "grinder_id") val molinoId: Long?,
    @ColumnInfo(name = "profile_id") val perfilId: Long?,
    val dosisG: Double,
    val rendimientoG: Double,
    val ratio: Double,
    val tiempoSeg: Int?,
    val temperaturaC: Double?,
    val ajusteMolienda: String?,
    // Pre-infusion tracking
    val preinfusionTiempoSeg: Int?,
    val preinfusionPresionBar: Double?,
    val aguaMlInfusion: Int?,
    // Tasting notes estructuradas
    val aromaNotes: String?,
    val saborNotes: String?,
    val cuerpo: String?,
    val acidez: String?,
    val finish: String?,
    val notas: String?,
    val nextShotNotes: String?,
    val calificacion: Int?,
    val createdAt: Long,
    val updatedAt: Long
)
