package com.example.espressoshots.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shots")
data class Shot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val beanName: String,
    val grinder: String,
    val dose: Float,
    val yield: Float,
    val timeSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)
