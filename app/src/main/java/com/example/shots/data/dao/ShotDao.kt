package com.example.shots.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shots.data.model.ShotDetails
import com.example.shots.data.model.ShotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShotDao {
    @Query(
        """
        SELECT s.*, 
            b.tostador AS bean_tostador,
            b.cafe AS bean_cafe,
            g.nombre AS grinder_nombre,
            p.nombre AS profile_nombre
        FROM shots s
        INNER JOIN beans b ON b.id = s.bean_id
        LEFT JOIN grinders g ON g.id = s.grinder_id
        LEFT JOIN profiles p ON p.id = s.profile_id
        ORDER BY s.fecha DESC
        """
    )
    fun observeAll(): Flow<List<ShotDetails>>

    @Query("SELECT * FROM shots WHERE id = :id")
    fun getById(id: Long): ShotEntity?

    @Insert
    fun insert(shot: ShotEntity): Long

    @Update
    fun update(shot: ShotEntity)

    @Query("DELETE FROM shots WHERE id = :id")
    fun delete(id: Long)

    @Query(
        """
        SELECT s.*, 
            b.tostador AS bean_tostador,
            b.cafe AS bean_cafe,
            g.nombre AS grinder_nombre,
            p.nombre AS profile_nombre
        FROM shots s
        INNER JOIN beans b ON b.id = s.bean_id
        LEFT JOIN grinders g ON g.id = s.grinder_id
        LEFT JOIN profiles p ON p.id = s.profile_id
        WHERE (s.calificacion >= :minRating AND s.calificacion <= :maxRating)
            AND (:beanId IS NULL OR s.bean_id = :beanId)
            AND (:grinderId IS NULL OR s.grinder_id = :grinderId)
            AND (:startDate IS NULL OR s.fecha >= :startDate)
            AND (:endDate IS NULL OR s.fecha <= :endDate)
        ORDER BY s.fecha DESC
        """
    )
    fun queryFiltered(
        minRating: Int,
        maxRating: Int,
        beanId: Long?,
        grinderId: Long?,
        startDate: Long?,
        endDate: Long?
    ): Flow<List<ShotDetails>>
}
