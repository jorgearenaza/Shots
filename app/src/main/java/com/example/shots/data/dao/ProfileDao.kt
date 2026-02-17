package com.example.espressoshots.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.espressoshots.data.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE activo = 1 ORDER BY nombre ASC")
    fun observeActive(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles ORDER BY nombre ASC")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getById(id: Long): ProfileEntity?

    @Insert
    fun insert(profile: ProfileEntity): Long

    @Update
    fun update(profile: ProfileEntity)

    @Query("UPDATE profiles SET activo = 0, updatedAt = :updatedAt WHERE id = :id")
    fun deactivate(id: Long, updatedAt: Long)
}
