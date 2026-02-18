package com.example.shots.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ShotDetails(
    @Embedded val shot: ShotEntity,
    @ColumnInfo(name = "bean_tostador") val beanTostador: String,
    @ColumnInfo(name = "bean_cafe") val beanCafe: String,
    @ColumnInfo(name = "grinder_nombre") val grinderNombre: String?,
    @ColumnInfo(name = "profile_nombre") val profileNombre: String?
)
