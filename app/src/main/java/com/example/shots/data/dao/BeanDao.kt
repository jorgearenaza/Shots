package com.example.shots.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shots.data.model.BeanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BeanDao {
    @Query("SELECT * FROM beans WHERE activo = 1 ORDER BY fechaTostado DESC")
    fun observeActive(): Flow<List<BeanEntity>>

    @Query("SELECT * FROM beans ORDER BY fechaTostado DESC")
    fun observeAll(): Flow<List<BeanEntity>>

    @Query("SELECT * FROM beans WHERE id = :id")
    fun getById(id: Long): BeanEntity?

    @Insert
    fun insert(bean: BeanEntity): Long

    @Update
    fun update(bean: BeanEntity)

    @Query("UPDATE beans SET activo = 0, updatedAt = :updatedAt WHERE id = :id")
    fun deactivate(id: Long, updatedAt: Long)
}
