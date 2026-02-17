package com.example.espressoshots.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.espressoshots.data.model.Shot
import kotlinx.coroutines.flow.Flow

@Dao
interface ShotDao {
    @Query("SELECT * FROM shots ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<Shot>>

    @Insert
    suspend fun insert(shot: Shot)
}
