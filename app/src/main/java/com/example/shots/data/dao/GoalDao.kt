package com.example.shots.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import com.example.shots.data.model.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    
    @Insert
    suspend fun insertGoal(goal: GoalEntity): Long
    
    @Update
    suspend fun updateGoal(goal: GoalEntity)
    
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
    
    @Query("SELECT * FROM goals ORDER BY fechaCreacion DESC")
    fun observeGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE completado = 0 ORDER BY fechaCreacion DESC")
    fun observeActiveGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE completado = 1 ORDER BY fechaCompletado DESC")
    fun observeCompletedGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoal(goalId: Long): GoalEntity?
    
    @Query("UPDATE goals SET currentValue = :newValue WHERE id = :goalId")
    suspend fun updateGoalProgress(goalId: Long, newValue: Float)
    
    @Query("UPDATE goals SET completado = 1, fechaCompletado = :timestamp WHERE id = :goalId")
    suspend fun completeGoal(goalId: Long, timestamp: Long)
}
