package com.example.shots.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shots.data.model.GrinderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrinderDao {
    @Query("SELECT * FROM grinders WHERE activo = 1 ORDER BY nombre ASC")
    fun observeActive(): Flow<List<GrinderEntity>>

    @Query("SELECT * FROM grinders ORDER BY nombre ASC")
    fun observeAll(): Flow<List<GrinderEntity>>

    @Query("SELECT * FROM grinders WHERE id = :id")
    fun getById(id: Long): GrinderEntity?

    @Insert
    fun insert(grinder: GrinderEntity): Long

    @Update
    fun update(grinder: GrinderEntity)

    @Query("UPDATE grinders SET activo = 0, updatedAt = :updatedAt WHERE id = :id")
    fun deactivate(id: Long, updatedAt: Long)
}
